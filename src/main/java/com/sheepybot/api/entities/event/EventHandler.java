package com.sheepybot.api.entities.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {

    /**
     * @return {@code true} if the {@link Event} is ignoring cancelled, {@code false} otherwise
     */
    boolean ignoreCancelled() default false;

    /**
     * @return The {@link EventPriority}
     */
    EventPriority priority() default EventPriority.NORMAL;

    /**
     * Represents an event's priority in execution
     */
    enum EventPriority {

        /**
         * Event call is of very low importance and should be ran first, to allow
         * other plugins to further customise the outcome
         */
        LOWEST(0),

        /**
         * Event call is of low importance
         */
        LOW(1),

        /**
         * Event call is neither important nor unimportant, and may be ran
         * normally
         */
        NORMAL(2),

        /**
         * Event call is of high importance
         */
        HIGH(3),

        /**
         * Event call is critical and must have the final say in what happens
         * to the event
         */
        HIGHEST(4);

        private final int slot;

        EventPriority(final int slot) {
            this.slot = slot;
        }

        public int getSlot() {
            return slot;
        }
    }

}
