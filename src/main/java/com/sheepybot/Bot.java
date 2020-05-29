package com.sheepybot;

import com.google.gson.JsonParser;
import com.moandjiezana.toml.Toml;
import com.sheepybot.api.entities.command.Command;
import com.sheepybot.api.entities.command.RootCommandRegistry;
import com.sheepybot.api.entities.database.Database;
import com.sheepybot.api.entities.database.auth.DatabaseInfo;
import com.sheepybot.api.entities.event.RootEventRegistry;
import com.sheepybot.api.entities.language.I18n;
import com.sheepybot.api.entities.module.Module;
import com.sheepybot.api.entities.module.loader.ModuleLoader;
import com.sheepybot.api.entities.scheduler.Scheduler;
import com.sheepybot.api.entities.utils.Objects;
import com.sheepybot.internal.command.CommandRegistryImpl;
import com.sheepybot.internal.command.defaults.admin.BuildInfoCommand;
import com.sheepybot.internal.command.defaults.admin.EvaluateCommand;
import com.sheepybot.internal.command.defaults.admin.StopCommand;
import com.sheepybot.internal.event.EventRegistryImpl;
import com.sheepybot.internal.module.ModuleLoaderImpl;
import com.sheepybot.listeners.*;
import com.sheepybot.util.BotUtils;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.BiFunction;

/**
 * The main class for the bot
 */
public class Bot {

    /**
     * Logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

    /**
     * User-Agent header used for web queries
     */
    @SuppressWarnings("ConstantConditions")
    //Intellij thinks everything from BotInfo won't change, but it does at compile time thanks to gradle
    public static final String USER_AGENT = BotInfo.BOT_NAME + (BotInfo.VERSION_MAJOR.startsWith("@") ? "" : " v" + BotInfo.VERSION);

    /**
     * A {@link JsonParser} instance
     */
    public static final JsonParser JSON_PARSER = new JsonParser();

    /**
     * A {@link MediaType}
     */
    public static final MediaType MEDIA_JSON = MediaType.parse("application/json;charset=utf8");

    /**
     * The {@link OkHttpClient} used for executing web requests
     */
    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().addInterceptor(chain -> {
        Request request = chain.request();

        if (request.header("User-Agent") == null) {
            request = request.newBuilder().header("User-Agent", Bot.USER_AGENT).build();
        }

        return chain.proceed(request);
    }).build();

    /**
     * The thread group used for threads created by {@link #SCHEDULED_EXECUTOR_SERVICE}
     */
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("Executor-Thread-Group");

    /**
     * A convenience {@link BiFunction} to create a new {@link Thread} with the {@code threadName} and {@code threadExecutor}
     */
    private static final BiFunction<String, Runnable, Thread> THREAD_FUNCTION = (threadName, threadExecutor) -> new Thread(THREAD_GROUP, threadExecutor, threadName);

    /**
     * A cached {@link ExecutorService}
     */
    public static final ExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(30, threadExecutor -> THREAD_FUNCTION.apply("Cached Thread Service", threadExecutor));

    /**
     * A single threaded {@link ScheduledExecutorService}.
     */
    public static final ScheduledExecutorService SINGLE_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor(threadExecutor -> THREAD_FUNCTION.apply("Single Thread Service", threadExecutor));

    private static Bot instance;

    static {
        THREAD_GROUP.setMaxPriority(Thread.NORM_PRIORITY);
    }

    private long startTime;
    private boolean running;
    private Toml config;
    private RootCommandRegistry commandRegistry;
    private RootEventRegistry eventRegistry;
    private ModuleLoader moduleLoader;
    private ShardManager shardManager;
    private Database database;

    public static void main(final String[] args) {
        Bot bot = null;
        try {
            (bot = new Bot()).start(); //start in current directory
        } catch (final Throwable ex) {
            LOGGER.info("An error occurred during startup and the bot has to shutdown...");
            ex.printStackTrace();
            if (bot != null) {
                bot.shutdown();
            }
        }
    }

    /**
     * @return The current instance
     */
    public static Bot get() {
        return Bot.instance;
    }

    private void start() throws IOException, LoginException {
        Objects.checkArgument(!this.running, "bot is already running");

        Bot.instance = this;

        this.running = true;
        this.startTime = System.currentTimeMillis();

        final File file = new File("bot.toml");
        if (!file.exists()) {
            FileUtils.copyURLToFile(this.getClass().getResource("/bot.toml"), file); //config wasn't found so copy internal one (resources/bot.toml)
            LOGGER.info(String.format("bot.toml was created at %s.", file.getCanonicalPath()));
            System.exit(ExitCode.EXIT_CODE_NORMAL); //need to use System.exit() because if the starter is present it will error on restart
        } else {

            LOGGER.info("Loading configuration...");

            this.config = new Toml().read(file);

            if (this.config.getString("client.token").isEmpty()) {
                LOGGER.info("No discord token specified, please configure it in the bot.toml");
                System.exit(ExitCode.EXIT_CODE_NORMAL);
                return;
            }

            LOGGER.info("Connecting to database...");

            this.database = new Database(new DatabaseInfo(this.config.getTable("db")));

            LOGGER.info("Loading data managers...");

            this.commandRegistry = new CommandRegistryImpl();
            this.eventRegistry = new EventRegistryImpl();
            this.moduleLoader = new ModuleLoaderImpl();

            final String token = this.config.getString("client.token");

            int shards = Math.toIntExact(this.config.getLong("client.shards"));
            int recommendedShards = BotUtils.getRecommendedShards(token);
            if (shards < recommendedShards) {
                LOGGER.info("Cannot use less than discords recommended shard count, using recommended shard count from discord instead.");
                shards = recommendedShards;
            }

            final DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.create(
                    GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_BANS,
                    GatewayIntent.GUILD_VOICE_STATES,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_MESSAGE_REACTIONS
            )
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS, CacheFlag.MEMBER_OVERRIDES)
                    .setChunkingFilter(ChunkingFilter.NONE)
                    .setMemberCachePolicy(MemberCachePolicy.NONE)
                    .setToken(token)
                    .setAutoReconnect(true)
                    .setEnableShutdownHook(false) //we dont use the shutdown hook on JDA as we handle that ourselves
                    .setActivity(Activity.of(Activity.ActivityType.DEFAULT, this.config.getString("client.activity", "Beep Boop, Boop Beep?")))
                    .setBulkDeleteSplittingEnabled(false)
                    .setHttpClient(HTTP_CLIENT)
                    .setShardsTotal(shards) //use default shard count
                    .addEventListeners(
                            new GuildMessageListener(),
                            new GuildMemberListener(),
                            new GuildMemberReactionListener(),
                            new GuildMemberVoiceListener(),
                            new GuildModerationListener(),
                            new GuildRoleListener(),
                            new GuildUpdateListener()
                    );

            LOGGER.info("Starting shards and attempting to connect to the Discord API...");

            this.shardManager = builder.build();

            LOGGER.info("Registering default commands...");

            this.commandRegistry.registerCommand(Command.builder()
                    .names("buildinfo")
                    .executor(new BuildInfoCommand())
                    .build());

            this.commandRegistry.registerCommand(Command.builder()
                    .names("eval")
                    .executor(new EvaluateCommand())
                    .build());

            this.commandRegistry.registerCommand(Command.builder()
                    .names("stop")
                    .executor(new StopCommand())
                    .build());

            LOGGER.info("Loading language files...");

            I18n.loanI18n(this.getClass());

            LOGGER.info("Loading modules...");

            //If there were no modules to load it just returns an empty list, so no harm done
            final Collection<Module> modules = this.moduleLoader.loadModules();
            modules.forEach(this.moduleLoader::enableModule);

            LOGGER.info(String.format("Loaded %d modules", this.moduleLoader.getEnabledModules().size()));

            LOGGER.info("Registering shutdown hook...");

            //register our shutdown hook so if something happens we get properly shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "Auto-Shutdown-Thread"));

            LOGGER.info(String.format("Startup completed! Took %dms, running api version: %s.", (System.currentTimeMillis() - this.startTime), BotInfo.VERSION));
        }
    }

    private void shutdown() {
        Objects.checkArgument(this.running, "bot not running");

        this.running = false;

        if (this.shardManager != null) {
            this.shardManager.shutdown();
        }

        if (this.moduleLoader != null) {
            this.moduleLoader.disableModules();
        }

        if (this.eventRegistry != null) {
            this.eventRegistry.unregisterAll();
        }

        if (this.commandRegistry != null) {
            this.commandRegistry.unregisterAll();
        }

        Scheduler.getInstance().shutdown();

        Bot.SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
        Bot.SINGLE_EXECUTOR_SERVICE.shutdownNow();

        Bot.instance = null;
    }

    /**
     * @return The bot config
     */
    public Toml getConfig() {
        return this.config;
    }

    /**
     * @return The time of startup
     */
    public long getStartTime() {
        return this.startTime;
    }

    /**
     * @return The {@link Database} instance
     */
    public Database getDatabase() {
        return this.database;
    }

    /**
     * @return The {@link RootCommandRegistry} impl
     */
    public RootCommandRegistry getCommandRegistry() {
        return this.commandRegistry;
    }

    /**
     * @return The {@link RootEventRegistry} impl
     */
    public RootEventRegistry getEventRegistry() {
        return this.eventRegistry;
    }

    /**
     * @return The {@link ModuleLoader} impl
     */
    public ModuleLoader getModuleLoader() {
        return this.moduleLoader;
    }

    /**
     * @return The {@link ShardManager}
     */
    public ShardManager getShardManager() {
        return this.shardManager;
    }

}
