package com.sheepybot.api.entities.event.module;

import com.sheepybot.api.entities.module.Module;
import org.jetbrains.annotations.NotNull;

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
