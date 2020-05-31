package com.sheepybot;

import com.google.gson.JsonParser;
import com.moandjiezana.toml.Toml;
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
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
import com.sheepybot.internal.command.defaults.admin.EvaluateCommand;
import com.sheepybot.internal.event.EventRegistryImpl;
import com.sheepybot.internal.module.ModuleLoaderImpl;
import com.sheepybot.listeners.*;
import com.sheepybot.util.BotUtils;
import com.sheepybot.util.Options;
import net.dv8tion.jda.api.JDAInfo;
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
            (bot = new Bot()).start(args); //start in current directory
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

    private void start(final String[] args) throws IOException, LoginException {
        Objects.checkArgument(!this.running, "bot is already running");

        Bot.instance = this;

        this.running = true;
        this.startTime = System.currentTimeMillis();

        final Options options = Options.parse(args);

        final Options.Option buildInfo = options.getOption("buildinfo");
        if (buildInfo != null) {
            LOGGER.info("Detected build info flag, logging build info then exiting...");

            LOGGER.info("--------------- Discord ---------------");
            LOGGER.info(String.format("Rest API Version: %d", JDAInfo.DISCORD_REST_VERSION));
            LOGGER.info(String.format("Audio Gateway Version: %d", JDAInfo.AUDIO_GATEWAY_VERSION));
            LOGGER.info("----------------- JDA -----------------");
            LOGGER.info(String.format("JDA Version: %s", JDAInfo.VERSION));
            LOGGER.info("----------------- Bot -----------------");
            LOGGER.info(String.format("API Version: %s", BotInfo.VERSION));
            LOGGER.info(String.format("Commit Long: %s", BotInfo.GIT_COMMIT));
            LOGGER.info(String.format("Branch: %s", BotInfo.GIT_BRANCH));
            LOGGER.info(String.format("Build Date: %s", BotInfo.BUILD_DATE));
            LOGGER.info(String.format("Lavaplayer Version: %s", PlayerLibrary.VERSION));
            LOGGER.info(String.format("JVM Version: %s", System.getProperty("java.version")));
            LOGGER.info("---------------------------------------");

            return;
        }

        final File file = new File("bot.toml");
        if (!file.exists()) {
            LOGGER.info("Couldn't find required file bot.toml when starting, creating it...");

            FileUtils.copyURLToFile(this.getClass().getResource("/bot.toml"), file); //config wasn't found so copy internal one (resources/bot.toml)

            LOGGER.info(String.format("File bot.toml was created at %s, please configure it then restart the bot.", file.getCanonicalPath()));

            ModuleLoaderImpl.MODULE_DIRECTORY.mkdirs(); //assuming first time start so we're making the modules directory too
            I18n.extractLanguageFiles(); //also extracting internal language files so people can change how we respond
        } else {

            LOGGER.info("Loading configuration...");

            this.config = new Toml().read(file);

            if (this.config.getString("client.token").isEmpty()) {
                LOGGER.info("No discord token specified, please configure it in the bot.toml");
                return;
            }

            I18n.setDefaultI18n(this.config.getString("client.languageFile"));

            if (this.config.getBoolean("db.enabled", false)) {
                LOGGER.info("Connecting to database...");

                this.database = new Database(new DatabaseInfo(this.config.getTable("db")));
            }

            LOGGER.info("Loading data managers...");

            this.commandRegistry = new CommandRegistryImpl();
            this.eventRegistry = new EventRegistryImpl();
            this.moduleLoader = new ModuleLoaderImpl();

            final String token = this.config.getString("client.token");

            int shards = Math.toIntExact(this.config.getLong("shards.shard_total"));
            int recommendedShards = BotUtils.getRecommendedShards(token);

            if (shards != -1 && shards < recommendedShards) {
                LOGGER.info("Cannot use less than discords recommended shard count, using recommended shard count from discord instead.");
                shards = recommendedShards;
            }

            final DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.create(
                    GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_BANS,
                    GatewayIntent.GUILD_VOICE_STATES,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_MESSAGE_REACTIONS)
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS, CacheFlag.MEMBER_OVERRIDES)
                    .setChunkingFilter(ChunkingFilter.NONE)
                    .setMemberCachePolicy(MemberCachePolicy.NONE)
                    .setToken(token)
                    .setAutoReconnect(true)
                    .setEnableShutdownHook(false) //we dont use the shutdown hook on JDA as we handle that ourselves
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

            if (shards != -1) {

                int shardMin = Math.toIntExact(this.config.getLong("sharding.shard_min"));
                int shardMax = Math.toIntExact(this.config.getLong("sharding.shard_max"));

                if (shardMin < 0) shardMin = 0;
                if (shardMax < 0 || shardMax > shards) shardMax = (shards - 1);

                builder.setShards(shardMin, shardMax);

            }

            final String activity = this.config.getString("client.activity");
            if (activity != null && !activity.isEmpty()) {
                LOGGER.info(activity + " / " + this.config.getString("client.activity_type") + " / " + BotUtils.getActivityTypeFromString(this.config.getString("client.activity_type")));
                builder.setActivity(Activity.of(BotUtils.getActivityTypeFromString(this.config.getString("client.activity_type")), activity));
            }

            LOGGER.info("Starting shards and attempting to connect to the Discord API...");

            this.shardManager = builder.build();

            LOGGER.info("Registering default commands...");

            this.commandRegistry.registerCommand(Command.builder()
                    .names("eval")
                    .executor(new EvaluateCommand())
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

        if (this.moduleLoader != null) {
            this.moduleLoader.disableModules();
        }

        if (this.shardManager != null) {
            this.shardManager.shutdown();
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
