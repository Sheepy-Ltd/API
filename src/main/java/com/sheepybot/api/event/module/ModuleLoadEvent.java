package com.sheepybot.api.event.module;

import org.jetbrains.annotations.NotNull;
import com.sheepybot.api.entities.module.Module;

/**
 * Called after a {@link Module} has been loaded
 */
public class ModuleLoadEvent extends ModuleEvent {

    /**
     * @param module The {@link Module} loaded
     */
    public ModuleLoadEvent(@NotNull(value = "module cannot be null") final Module module) {
        super(module);
    }

}
