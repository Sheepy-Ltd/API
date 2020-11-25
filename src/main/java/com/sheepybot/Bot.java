package com.sheepybot;

import com.google.gson.JsonParser;
import com.moandjiezana.toml.Toml;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
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
import com.sheepybot.internal.command.CommandRegistryImpl;
import com.sheepybot.internal.command.defaults.admin.*;
import com.sheepybot.internal.event.EventRegistryImpl;
import com.sheepybot.internal.listeners.GuildMessageListener;
import com.sheepybot.internal.listeners.JdaGenericListener;
import com.sheepybot.internal.module.ModuleLoaderImpl;
import com.sheepybot.util.BotUtils;
import com.sheepybot.util.Objects;
import com.sheepybot.util.Options;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
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
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

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
     * The thread group used for our {@link ExecutorService}s
     */
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("Executor-Thread-Group");

    /**
     * A convenience {@link BiFunction} to create a new {@link Thread} with the {@code threadName} and {@code threadExecutor}
     */
    private static final BiFunction<String, Runnable, Thread> THREAD_FUNCTION = (threadName, threadExecutor) -> new Thread(THREAD_GROUP, threadExecutor, threadName);

    /**
     * A scheduled {@link ExecutorService}
     */
    public static final ExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(30, threadExecutor -> THREAD_FUNCTION.apply("Cached Thread Service", threadExecutor));

    /**
     * A single threaded {@link ScheduledExecutorService}.
     */
    public static final ScheduledExecutorService SINGLE_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor(threadExecutor -> THREAD_FUNCTION.apply("Single Thread Service", threadExecutor));

    public static Function<Guild, String> defaultPrefixGenerator;

    public static Function<Guild, String> prefixGenerator;

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
            LOGGER.info("An error occurred during startup and the API has to shutdown...", ex);
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

            LOGGER.info("----------------- JDA -----------------");
            LOGGER.info(String.format("JDA Version: %s", JDAInfo.VERSION));
            LOGGER.info(String.format("Rest API Version: %d", JDAInfo.DISCORD_REST_VERSION));
            LOGGER.info(String.format("Audio Gateway Version: %d", JDAInfo.AUDIO_GATEWAY_VERSION));
            LOGGER.info("----------------- Bot -----------------");
            LOGGER.info(String.format("API Version: %s", BotInfo.VERSION));
            LOGGER.info(String.format("Commit Long: %s", BotInfo.GIT_COMMIT));
            LOGGER.info(String.format("Branch: %s", BotInfo.GIT_BRANCH));
            LOGGER.info(String.format("Build Date: %s", BotInfo.BUILD_DATE));
            LOGGER.info(String.format("Lava Player Version: %s", PlayerLibrary.VERSION));
            LOGGER.info(String.format("JVM Version: %s", System.getProperty("java.version")));
            LOGGER.info("---------------------------------------");

            return;
        }

        final Options.Option ignoreUpdates = options.getOption("ignore-repository-updates");
        if (ignoreUpdates == null || !ignoreUpdates.getAsBoolean(false)) {
            LOGGER.info("Checking for updates...");
        } else {
            LOGGER.warn("You have disabled automatic update checking using the '--ignore-repository-updates' flag.");
            LOGGER.warn("This is not advised as it means when issues are resolved you won't automatically receive them.");
            LOGGER.warn("You can check for newer versions on the repository: https://github.com/Sheepy-Ltd/API/releases");
            LOGGER.info("Startup will resume as normal in 10 seconds...");
            if (options.getOption("ignore-startup-warning") != null) {
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(10));
                } catch (final InterruptedException ignored) {
                }
            }
        }

        //noinspection ResultOfMethodCallIgnored
        ModuleLoaderImpl.MODULE_DIRECTORY.mkdirs();
        I18n.extractLanguageFiles(); //also extracting internal language files so people can change how we respond

        final File file = new File("bot.toml");
        if (!file.exists()) {
            LOGGER.info("Couldn't find required file bot.toml when starting, creating it...");

            FileUtils.copyURLToFile(this.getClass().getResource("/bot.toml"), file); //config wasn't found so copy internal one (resources/bot.toml)

            LOGGER.info(String.format("File bot.toml was created at %s, please configure it then restart the bot.", file.getCanonicalPath()));
        } else {

            //register our shutdown hook so if something happens we get properly shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "API-Auto-Shutdown-Thread"));

            LOGGER.info("Loading configuration...");

            this.config = new Toml().read(file);

            final double configVersion = this.config.getDouble("version", -1D);
            if (configVersion == -1D) {
                LOGGER.warn("The version number is missing from the configuration file, have you deleted it?");
            } else {

                try (final InputStream in = Bot.class.getResourceAsStream("/bot.toml")) {
                    if (in != null) {

                        try {
                            final Toml internalConfig = new Toml().read(in);

                            final double internalVersion = internalConfig.getDouble("version", -1D);
                            if (internalVersion == -1D) {
                                LOGGER.info("Internal configuration file version is missing, can't properly compare versions.");
                            } else {

                                //they should always be equal, if not either they've changed it in which case we're setting it back to what
                                //it should be or they haven't and there's actually a newer file version
                                if (configVersion != internalVersion) {
                                    LOGGER.info("Detected a newer internal configuration file than what is on disk, updating configuration to " + internalVersion + " from " + configVersion + "...");
                                }

                            }

                        } catch (final IllegalStateException ex) {
                            LOGGER.info("An error occurred during version comparison between hard disk config and internal config file", ex);
                        }

                    }

                }

            }

            if (this.config.getString("jda.token").isEmpty()) {
                LOGGER.info("No discord token specified, please configure it in the bot.toml");
                return;
            }

            I18n.setDefaultI18n(this.config.getString("client.language"));

            if (this.config.getBoolean("db.enabled", false)) {
                LOGGER.info("Database has been enabled in configuration file, loading up connection pool...");
                final Toml db = this.config.getTable("db");
                if (db == null || db.isEmpty()) {
                    LOGGER.error("Couldn't retrieve database configuration from the configuration file, has it been removed?");
                } else {
                    this.database = new Database(new DatabaseInfo(db));
                }
            }

            LOGGER.info("Loading data managers...");

            this.commandRegistry = new CommandRegistryImpl();
            this.eventRegistry = new EventRegistryImpl();
            this.moduleLoader = new ModuleLoaderImpl();

            final String token = this.config.getString("jda.token");
            if (token == null || token.isEmpty()) {
                LOGGER.error("Cannot start bot without a valid bot token.");
                System.exit(0);
            }

            int shards = Math.toIntExact(this.config.getLong("jda.shard_total"));
            int recommendedShards = BotUtils.getRecommendedShards(token);

            if (shards != -1 && shards < recommendedShards) {
                LOGGER.info("Cannot use less than discords recommended shard count, using recommended shard count from discord instead.");
                shards = recommendedShards;
            }

            final List<GatewayIntent> gatewayIntents = BotUtils.getGatewayIntentsFromList(this.config.getList("jda.gateway_intents", Collections.emptyList()));
            final List<CacheFlag> enabledCacheFlags = BotUtils.getCacheFlagsFromList(this.config.getList("jda.enabled_cache_flags", Collections.emptyList()));
            final List<CacheFlag> disabledCacheFlags = BotUtils.getCacheFlagsFromList(this.config.getList("jda.disabled_cache_flags", Collections.emptyList()));

            for (final CacheFlag flag : CacheFlag.values()) {
                if (flag.getRequiredIntent() != null && !gatewayIntents.contains(flag.getRequiredIntent()) && enabledCacheFlags.contains(flag)) {
                    LOGGER.info(String.format("Missing required gateway intent %s for cache flag %s, disabling cache flag...", flag.getRequiredIntent().name(), flag.name()));
                    enabledCacheFlags.remove(flag);
                    disabledCacheFlags.add(flag);
                }
            }

            final DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.create(token, gatewayIntents)
                    .enableCache(enabledCacheFlags)
                    .disableCache(disabledCacheFlags)
                    .setChunkingFilter(BotUtils.getChunkingFilterFromString(this.config.getString("jda.chunking_filter", "NONE")))
                    .setMemberCachePolicy(BotUtils.getMemberCachePolicyFromString(this.config.getString("jda.member_cache_policy", "NONE")))
                    .setAutoReconnect(this.config.getBoolean("jda.auto_reconnect", true))
                    .setEnableShutdownHook(false)
                    .setBulkDeleteSplittingEnabled(this.config.getBoolean("jda.bulk_delete_splitting", false))
                    .setHttpClient(HTTP_CLIENT)
                    .setShardsTotal(shards)
                    .addEventListeners(
                            new GuildMessageListener(),
                            new JdaGenericListener()
                    );

            if (this.config.getBoolean("jda.use-jda-nas", false)) {

                final String os = System.getProperty("os.name").toLowerCase();
                final String arch = System.getProperty("os.arch");

                if ((os.contains("windows") || os.contains("linux")) && !arch.equalsIgnoreCase("arm") && !arch.equalsIgnoreCase("arm-linux")) {
                    LOGGER.info("System supports JDA Nas, registering audio send factory...");
                    builder.setAudioSendFactory(new NativeAudioSendFactory());
                } else {
                    LOGGER.info("Attempting to enable JDA Nas on a system architecture which doesn't support it, please disable this config option as there's no benefit to using it on this kind of system.");
                    LOGGER.info(String.format("System OS: %s, arch: %s", os, arch));
                }

            }

            if (shards != -1) {

                int shardMin = Math.toIntExact(this.config.getLong("jda.shard_min", -1L));
                int shardMax = Math.toIntExact(this.config.getLong("jda.shard_max", -1L));

                if (shardMin < 0) shardMin = 0;
                if (shardMax < 0 || shardMax > shards) shardMax = (shards - 1);

                builder.setShards(shardMin, shardMax);

            }

            final String activity = this.config.getString("jda.activity");
            if (activity != null && !activity.isEmpty()) {
                builder.setActivity(Activity.of(Activity.ActivityType.DEFAULT, activity));
            }

            LOGGER.info("Loading language files...");

            I18n.loadI18n(this.getClass());

            if (this.config.getBoolean("client.load_default_commands", true)) {

                LOGGER.info("Loading default commands...");

                this.commandRegistry.registerCommand(Command.builder().names("reloadmodule", "rmod").usage("<module>").description("Reload a module").executor(new ReloadModuleCommand()).build(), null);
                this.commandRegistry.registerCommand(Command.builder().names("enablemodule", "emod").usage("<module>").description("Enable a module").executor(new EnableModuleCommand()).build(), null);
                this.commandRegistry.registerCommand(Command.builder().names("disablemodule", "dmod").usage("<module>").description("Disable a module").executor(new DisableModuleCommand()).build(), null);
                this.commandRegistry.registerCommand(Command.builder().names("loadmodule", "lmod").usage("<module>").description("Load a module from its exact jar file name").executor(new LoadModuleCommand()).build(), null);
                this.commandRegistry.registerCommand(Command.builder().names("unloadmodule", "umod").usage("<module>").description("Unload a module from memory").executor(new UnloadModuleCommand()).build(), null);

            }

            Bot.defaultPrefixGenerator = (event) -> this.config.getString("client.prefix", "!");
            Bot.prefixGenerator = defaultPrefixGenerator;

            LOGGER.info("Loading modules...");

            //If there were no modules to load it just returns an empty list, so no harm done
            final Collection<Module> modules = this.moduleLoader.loadModules();
            modules.forEach(module -> this.moduleLoader.enableModule(module));

            LOGGER.info(String.format("Loaded %d modules", this.moduleLoader.getEnabledModules().size()));

            LOGGER.info("Starting shards and attempting to connect to the Discord API...");

            this.shardManager = builder.build();

            LOGGER.info(String.format("Startup completed! Took %dms, implementing api version: %s.", (System.currentTimeMillis() - this.startTime), BotInfo.VERSION));
        }
    }

    private void shutdown() {
        Objects.checkArgument(this.running, "bot not running");

        LOGGER.info("Shutting down...");

        if (this.moduleLoader != null) {
            LOGGER.info("Disabling modules...");
            this.moduleLoader.disableModules();
        }

        if (this.shardManager != null) {
            LOGGER.info("Shutting down shard manager...");
            this.shardManager.shutdown();
        }

        if (this.eventRegistry != null) {
            LOGGER.info("Unregistering events...");
            this.eventRegistry.unregisterAll();
        }

        if (this.commandRegistry != null) {
            LOGGER.info("Unregistering commands...");
            this.commandRegistry.unregisterAll();
        }

        if (this.database != null) {
            LOGGER.info("Disconnecting from database...");
            this.database.shutdown();
        }

        LOGGER.info("Shutting down scheduler...");
        Scheduler.getInstance().shutdown();

        LOGGER.info("Shutting down thread pools...");
        Bot.SCHEDULED_EXECUTOR_SERVICE.shutdownNow();
        Bot.SINGLE_EXECUTOR_SERVICE.shutdownNow();

        Bot.instance = null;

        this.running = false;

        LOGGER.info("Shutdown complete!");
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
