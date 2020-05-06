package com.sheepybot.api.event.module;

import org.jetbrains.annotations.NotNull;
import com.sheepybot.api.entities.module.Module;

/**
 * Called when a {@link Module} has been disabled
 */
public class ModuleDisabledEvent extends ModuleEvent {

    /**
     * @param module The module that has been disabled
     */
    public ModuleDisabledEvent(@NotNull(value = "module cannot be null") final Module module) {
        super(module);
    }

}
