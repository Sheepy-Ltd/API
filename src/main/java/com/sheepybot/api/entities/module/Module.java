package com.sheepybot.api.entities.module;

import com.moandjiezana.toml.Toml;
import com.sheepybot.api.entities.command.RootCommandRegistry;
import com.sheepybot.api.entities.database.Database;
import com.sheepybot.api.entities.event.RootEventRegistry;
import com.sheepybot.api.entities.scheduler.Scheduler;
import com.sheepybot.util.Objects;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.function.Function;

public abstract class Module {

    private final ModuleData data;

    private Logger logger;
    private CommandRegistry commandRegistry;
    private EventRegistry eventRegistry;
    private Database database;
    private SchedulerRegistry schedulerRegistry;
    private Metrics metrics;
    private Function<GuildMessageReceivedEvent, String> prefixFunction = null;
    private File dataFolder;
    private File jar;

    private Toml config;
    private EventWaiter eventWaiter;

    private boolean enabled;

    public Module() {
        if (!getClass().isAnnotationPresent(ModuleData.class)) {
            throw new IllegalArgumentException("Class extends Module but is not annotated with ModuleData");
        }
        this.data = this.getClass().getAnnotation(ModuleData.class);
    }

    //this is called in the module loader, making it final prevents it being overridden
    public final void init(@NotNull(value = "command manager cannot be null") final RootCommandRegistry rootCommandRegistry,
                           @NotNull(value = "event manager cannot be null") final RootEventRegistry rootEventRegistry,
                           final Database database,
                           @NotNull(value = "data folder cannot be null") final File dataFolder,
                           @NotNull(value = "jar cannot be null") final File jar) {
        this.logger = LoggerFactory.getLogger(this.data.name());
        this.commandRegistry = new CommandRegistry(rootCommandRegistry, this);
        this.eventRegistry = new EventRegistry(rootEventRegistry, this);
        this.database = database;
        this.schedulerRegistry = new SchedulerRegistry();
        this.metrics = new Metrics(this.getName());
        this.dataFolder = dataFolder;
        this.jar = jar;
    }

    
    /**
     * Called on {@link Module} enable
     */
    public void onEnable() {

    }

    /**
     * Called on {@link Module} disable
     */
    public void onDisable() {

    }

    /**
     * @return The {@link ModuleData}
     */
    public ModuleData getData() {
        return this.data;
    }

    /**
     * @return The name of this {@link Module}
     */
    public String getName() {
        return this.data.name();
    }

    /**
     * @return This {@link Module}'s names and version
     */
    public String getFullName() {
        return this.data.name() + " v" + this.data.version();
    }

    /**
     * @return The generated {@link Logger} for this {@link Module}
     */
    public Logger getLogger() {
        return this.logger;
    }

    /**
     * @return This {@link Module}s {@link CommandRegistry}
     */
    public CommandRegistry getCommandRegistry() {
        return this.commandRegistry;
    }

    /**
     * @return The {@link EventRegistry}
     */
    public EventRegistry getEventRegistry() {
        return this.eventRegistry;
    }

    /**
     * @return The {@link Database}, this can return {@code null} if the database is not enabled in your bot configuration.
     */
    public Database getDatabase() {
        return this.database;
    }

    /**
     * @return The {@link EventWaiter}
     */
    public EventWaiter getEventWaiter() {
        return this.getEventWaiter(true);
    }

    /**
     * @param init Whether to initialize the {@link EventWaiter} if not already.
     *
     * @return The {@link EventWaiter}, or {@code null} if the {@link EventWaiter} is
     * not initialized and {@code init} is {@code false}
     */
    public EventWaiter getEventWaiter(final boolean init) {
        Objects.checkArgument(this.isEnabled(), "cannot retrieve event waiter whilst not enabled");

        //this can only be requested whilst the module is enabled (because you cannot register events whilst not enabled)
        //and as such cannot be called in Module.init

        EventWaiter result = this.eventWaiter;
        if (result == null && init) { //https://en.wikipedia.org/wiki/Double-checked_locking
            synchronized (this) {
                result = this.eventWaiter;
                if (result == null) {
                    this.eventWaiter = result = new EventWaiter(this.eventRegistry);
                }
            }
        }

        return result;
    }

    /**
     * @return The {@link Scheduler}
     */
    public SchedulerRegistry getScheduler() {
        return this.schedulerRegistry;
    }

    /**
     * @return The {@link Metrics} for this {@link Module}
     */
    public Metrics getMetrics() {
        return this.metrics;
    }

    /**
     * @return The {@link Function} used in generating prefixs for the API
     */
    public Function<GuildMessageReceivedEvent, String> getPrefixGenerator() {
        return this.prefixFunction;
    }

    /**
     * Sets the prefix generator to use for the entire API.
     *
     * <p>It's only advised to set this for an individual module as it will serve for the entire API and is not module specific</p>
     *
     * @param prefixFunction The {@link Function} to use in generating prefixes for the API.
     */
    public void setPrefixGenerator(@NotNull(value = "prefix generator cannot be null") final Function<GuildMessageReceivedEvent, String> prefixFunction) {
        this.prefixFunction = prefixFunction;
    }

    /**
     * @return This {@link Module}s data folder
     */
    public File getDataFolder() {
        return this.dataFolder;
    }

    /**
     * @return The jar file
     */
    public File getJar() {
        return this.jar;
    }

    /**
     * @return This {@link Module}s configuration file.
     */
    public Toml getConfig() {
        if (this.config == null) {
            final File destination = new File(this.dataFolder, "config.toml");
            final URL internal = this.getClass().getResource("/config.toml");

            if (internal == null) {
                throw new IllegalArgumentException("Module does not contain 'config.toml'");
            }

            if (!destination.exists()) {
                try {
                    FileUtils.copyURLToFile(internal, destination);
                } catch (final IOException ex) {
                    throw new IllegalStateException(String.format("Couldn't copy internal file config.toml to %s: %s",
                            destination.getAbsolutePath(), ex.getMessage()));
                }
            }

            this.config = new Toml().read(destination);
        }
        return this.config;
    }

    /**
     * @return {@code true} if this {@link Module} is enabled, {@code false} otherwise
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Sets this {@link Module}'s current enabled status.
     *
     * @param enabled The new enabled status of this {@link Module}
     * @see Module#onDisable()
     */
    public void setEnabled(final boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (this.enabled) {
                this.onEnable();
            } else {
                this.onDisable();
            }
        }
    }

    @Override
    public String toString() {
        return "Module{data = " + this.data.toString() + "}";
    }

}
