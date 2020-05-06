package com.sheepybot.api.event.module;

import org.jetbrains.annotations.NotNull;
import com.sheepybot.api.entities.module.Module;

/**
 * Called when a {@link Module} has been enabled
 */
public class ModuleEnabledEvent extends ModuleEvent {

    /**
     * @param module The {@link Module} enabled
     */
    public ModuleEnabledEvent(@NotNull(value = "module cannot be null") final Module module) {
        super(module);
    }

}
