package com.sheepybot.api.entities.module;


import com.google.common.collect.Maps;
import com.sheepybot.api.entities.event.EventHandler;
import com.sheepybot.api.entities.event.EventListener;
import com.sheepybot.util.Objects;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class EventWaiter implements EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventWaiter.class);
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("Event Waiter Thread Group");

    private final ScheduledExecutorService service;
    private final Map<Class<?>, Set<WaitingEvent>> waiters;

    /**
     * Construct a new {@link EventWaiter} with the default {@link ScheduledExecutorService}
     */
    EventWaiter(final EventRegistry registry) {
        this(registry, Executors.newSingleThreadScheduledExecutor(runnable -> new Thread(THREAD_GROUP, runnable, "Event Waiter Executor Thread")));
    }

    /**
     * Construct a new {@link EventWaiter} with the provided {@link ScheduledExecutorService}
     *
     * @param registry The {@link EventRegistry} to build to
     * @param service  The {@link ScheduledExecutorService} to use
     */
    EventWaiter(@NotNull(value = "registry cannot be null") final EventRegistry registry,
                @NotNull(value = "executor service cannot be null") final ScheduledExecutorService service) {
        this.service = service;
        this.waiters = Maps.newHashMap();
        registry.registerEvent(this);
    }

    /**
     * Waits a predetermined amount of time for the {@link Event} that returns true
     * when tested with the provided {@link Predicate}
     *
     * <p>Should the time allotted lapse the event will not be calls and will be purged from
     * the internal list calling the {@link Runnable} should one be present</p>
     *
     * @param event         The {@link Class} of the {@link Event}
     * @param predicate     The {@link Predicate} to test when a compatible {@link Event} is called
     * @param function      The {@link Function} to perform the action
     * @param timeoutAfter  The maximum length of time to wait for, or 0 to wait indefinitely
     * @param unit          The {@link TimeUnit} to measure the {@code timeout} in
     * @param timeoutAction The {@link Runnable} to execute should a compatible {@link Event} not be thrown before
     * @return The {@link WaitingEvent}
     * @throws IllegalArgumentException If this {@link EventWaiter} is shutdown or if there is no timeout set
     */
    public <T extends GenericEvent> WaitingEvent<T> newWaiter(@NotNull(value = "event cannot be null") final Class<T> event,
                                                              final Predicate<T> predicate,
                                                              @NotNull(value = "function cannot be null") final Function<T, Boolean> function,
                                                              final long timeoutAfter,
                                                              @NotNull(value = "unit cannot be null") final TimeUnit unit,
                                                              final Runnable timeoutAction) {

        Objects.checkArgument(!this.isShutdown(), "Cannot register new WaitingEvent's on a shutdown EventWaiter");
        Objects.checkArgument(timeoutAfter > 0, "Timeout cannot be negative or equal to 0");

        final WaitingEvent waiter = new WaitingEvent<>(predicate, function);
        final Set<WaitingEvent> waiters = this.waiters.computeIfAbsent(event, __ -> new HashSet<>());

        waiters.add(waiter);

        //noinspection ConstantConditions
        if (timeoutAfter > 0 && unit != null) {
            this.service.schedule(() -> {
                if (waiters.remove(waiter) && timeoutAction != null) {
                    try {
                        timeoutAction.run();
                    } catch (final Throwable ignored) {
                    }
                }
            }, timeoutAfter, unit);
        }

        return waiter;
    }

    /**
     * Checks whether the internal {@link ExecutorService} is either {@code null}, shutdown or terminated
     *
     * @return {@code true} if the {@link ExecutorService} is shutdown, {@code false otherwise}
     */
    public boolean isShutdown() {
        return this.service == null || this.service.isShutdown() || this.service.isTerminated();
    }

    /**
     * Shutdown this {@link EventWaiter} and clear any registered {@link WaitingEvent}
     */
    public void shutdown() {
        if (!this.isShutdown()) {
            this.waiters.clear();
            this.service.shutdownNow();
        }
    }

    /**
     * of a new {@link WaitingEventBuilder}
     *
     * @param clazz The {@link Class} of the {@link Event}
     *
     * @return The {@link WaitingEventBuilder}
     */
    public <T extends Event> WaitingEventBuilder<T> newBuilder(final Class<T> clazz) {
        return new WaitingEventBuilder<>(this, clazz);
    }

    @EventHandler
    public final void onEvent(final GenericEvent event) {
        if (this.isShutdown()) return;

        Class clazz = event.getClass();
        while (clazz != null) {

            if (this.waiters.containsKey(clazz)) {

                LOGGER.info("Found valid event waiter, processing event...");

                final Set<WaitingEvent> waiters = this.waiters.get(clazz);
                final WaitingEvent[] events = waiters.toArray(new WaitingEvent[0]);

                waiters.removeAll(Stream.of(events).filter(waiter -> waiter.call(event)).collect(Collectors.toSet()));

            }

            clazz = clazz.getSuperclass();
        }

    }

    /**
     *
     */
    public class WaitingEvent<T extends GenericEvent> {

        private final Predicate<T> predicate;
        private final Function<T, Boolean> function;

        WaitingEvent(final Predicate<T> predicate,
                     final Function<T, Boolean> function) {
            this.predicate = predicate;
            this.function = function;
        }

        /**
         * Call the handler for this {@link WaitingEvent}
         *
         * @param event The event
         *
         * @return {@code true} if this event was handled successfully
         */
        public boolean call(final T event) {
            try {
                return this.predicate == null || this.predicate.test(event) ? this.function.apply(event) : false;
            } catch (final Throwable ex) {
                LOGGER.info("WaitingEvent threw an exception", ex);
            }
            return true;
        }

    }

    /**
     * A utility class aimed at creating a more cleanly styled way of building {@link WaitingEvent}s
     */
    public class WaitingEventBuilder<T extends GenericEvent> {

        protected final EventWaiter waiter;
        protected final Class<T> clazz;

        private Predicate<T> predicate;
        private Function<T, Boolean> function;
        private long timeout;
        private TimeUnit unit;
        private Runnable timeoutAction;

        /**
         * Construct a new {@link WaitingEventBuilder}
         *
         * @param waiter The {@link EventWaiter} to build to
         * @param clazz  The {@link Class} of the {@link Event}
         */
        WaitingEventBuilder(final EventWaiter waiter,
                            final Class<T> clazz) {
            this.waiter = waiter;
            this.clazz = clazz;
        }

        /**
         * Set the {@link Predicate} to check prior to execution of the {@link WaitingEvent}
         *
         * @param predicate The {@link Predicate}
         *
         * @return This {@link WaitingEventBuilder}
         */
        public WaitingEventBuilder<T> before(@NotNull(value = "predicate cannot be null") final Predicate<T> predicate) {
            this.predicate = predicate;
            return this;
        }

        /**
         * Set the executor for the {@link WaitingEvent}
         *
         * <p>Using a consumer means there is no way for a {@link WaitingEvent} to know
         * whether it was successful so instead it is assumed that it is and returns true upon completion
         * regardless of any exception being thrown</p>
         *
         * @param consumer The {@link Consumer}
         *
         * @return This {@link WaitingEventBuilder}
         */
        public WaitingEventBuilder<T> executor(@NotNull(value = "consumer cannot be null") final Consumer<T> consumer) {
            this.function = (event) -> {
                try {
                    consumer.accept(event);
                } catch (final Throwable ignored) {
                }
                return true;
            };
            return this;
        }

        /**
         * Set the executor for the {@link WaitingEvent}
         *
         * <p>This method gives more power to the {@link WaitingEvent} as it also gives
         * the opportunity for it to say it wasn't successful meaning that the {@link WaitingEvent}
         * could still be called again until it either expires or returns {@code true}</p>
         *
         * @param function The {@link Function}
         *
         * @return This {@link WaitingEventBuilder}
         */
        public WaitingEventBuilder<T> executor(@NotNull(value = "function cannot be null") final Function<T, Boolean> function) {
            this.function = function;
            return this;
        }

        /**
         * Set the timeout duration
         *
         * <p>This is how long we want to listen for a given {@link Event}, should this time
         * elapse then the event will be purged from the listening queue and any {@link Runnable}
         * specified will be called to say that the allotted time has ran out</p>
         *
         * @param timeout The timeout (in seconds)
         *
         * @return This {@link WaitingEventBuilder}
         */
        public WaitingEventBuilder<T> timeoutAfter(final long timeout) {
            return this.timeoutAfter(timeout, TimeUnit.SECONDS);
        }

        /**
         * Set the timeout duration and the unit to measure it in
         *
         * <p>This is how long we want to listen for a given {@link Event}, should this time
         * elapse then the event will be purged from the listening queue and any {@link Runnable}
         * specified will be called to say that the allotted time has ran out</p>
         *
         * @param timeout The timeout
         * @param unit    The {@link TimeUnit} to measure the {@code timeout} in
         *
         * @return This {@link WaitingEventBuilder}
         */
        public WaitingEventBuilder<T> timeoutAfter(final long timeout,
                                                   @NotNull(value = "unit cannot be null") final TimeUnit unit) {
            Objects.checkArgument(timeout > 0, "timeout cannot be negative or equal to 0");
            this.timeout = timeout;
            this.unit = unit;
            return this;
        }

        /**
         * Set the {@link Runnable} to be executed should a compatible {@link Event} not be called before the time is
         * up
         *
         * @param timeoutAction The {@link Runnable} to execute
         *
         * @return This {@link WaitingEventBuilder}
         */
        public WaitingEventBuilder<T> timeoutAction(@NotNull(value = "timeoutAction cannot be null") final Runnable timeoutAction) {
            this.timeoutAction = timeoutAction;
            return this;
        }

        /**
         * Create and register the {@link WaitingEvent}
         *
         * @return The {@link WaitingEvent}
         */
        public WaitingEvent<T> build() {
            return this.waiter.newWaiter(this.clazz, this.predicate, this.function, this.timeout, this.unit, this.timeoutAction);
        }

    }

}
