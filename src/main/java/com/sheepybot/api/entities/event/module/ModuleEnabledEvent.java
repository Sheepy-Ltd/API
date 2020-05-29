package com.sheepybot.api.entities.event.module;

import com.sheepybot.api.entities.module.Module;
import org.jetbrains.annotations.NotNull;

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
