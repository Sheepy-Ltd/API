package com.sheepybot.api.entities.module.loader;

import com.sheepybot.api.entities.module.Module;
import com.sheepybot.api.exception.module.InvalidModuleException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;

public interface ModuleLoader {

    Logger LOGGER = LoggerFactory.getLogger(ModuleLoader.class);

    /**
     * Retrieve a {@link Module} by-names
     *
     * @param name The names of the {@link Module}
     * @return The {@link Module}, or {@code null} if no module was found
     */
    Module getModuleByName(@NotNull("module names cannot be null") final String name);

    /**
     * Get every loaded {@link Module}. Even if an error occurred during {@link Module#onEnable()} it will still appear
     * in this list
     *
     * @return A {@link Collection} of loaded {@link Module}'s
     */
    Collection<Module> getModules();

    /**
     * @return A {@link Collection} of enabled {@link Module}'s
     */
    Collection<Module> getEnabledModules();

    /**
     * @param directory The module file
     *
     * @return The loaded {@link Module}
     *
     * @throws IllegalArgumentException If the directory is not a valid jar file
     * @throws IllegalArgumentException If this module is already loaded
     * @throws InvalidModuleException   If this module does not contain a main class
     */
    Module loadModule(@NotNull("module file cannot be null") final File directory) throws IllegalArgumentException, IllegalStateException, InvalidModuleException;

    /**
     * Loads all {@link Module}'s in the current directory, if the directory has no child files it will just return an
     * empty list.
     *
     * @return A {@link Collection} containing every loaded module
     *
     * @throws NullPointerException     If the specified directory is null
     * @throws IllegalArgumentException If the specified directory is not a valid directory
     */
    Collection<Module> loadModules() throws NullPointerException, IllegalArgumentException;

    /**
     * Disables every enabled {@link Module}
     */
    void disableModules();

    /**
     * Enable a loaded {@link Module}
     * <p></p>
     * <p>Note that calling this method is <strong>NOT</strong> the same as calling {@link Module#onEnable()} or {@link Module#setEnabled(boolean)}
     * and those methods are not a replacement for calling this method. Outside of all advice, iff you are loading modules outside of the API instead
     * of using its default commands then this method <em>must</em> be called</p>
     *
     * @param module
     */
    void enableModule(@NotNull("module cannot be null") final Module module);

    /**
     * @param module The {@link Module} to disable
     */
    void disableModule(@NotNull("module cannot be null") final Module module);

    /**
     * Unload a module from memory
     *
     * @param module The {@link Module} to unload
     */
    void unloadModule(@NotNull("module cannot be null") Module module);

    /**
     * Reload all {@link Module}s
     *
     * @see ModuleLoader#disableModule(Module)
     */
    void reloadModules();

}
