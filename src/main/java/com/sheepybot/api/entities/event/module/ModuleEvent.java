package com.sheepybot.api.entities.event.module;

import com.sheepybot.api.entities.event.Event;
import com.sheepybot.api.entities.module.Module;

public class ModuleEvent extends Event {

    private final Module module;

    /**
     * @param module The {@link Module}
     */
    public ModuleEvent(final Module module) {
        this.module = module;
    }

    /**
     * @return The {@link Module}
     */
    public Module getModule() {
        return this.module;
    }
}
