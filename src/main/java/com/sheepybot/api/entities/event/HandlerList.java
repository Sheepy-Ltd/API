package com.sheepybot.api.entities.event;

import com.sheepybot.api.entities.module.Module;

import java.util.*;

public class HandlerList {

    /**
     * Handler array.
     */
    private volatile RegisteredListener[] handlers = null;

    /**
     * Dynamic handler lists. These are changed using register() and
     * unregister() and are automatically baked to the handlers array any time
     * they have changed.
     */
    private final EnumMap<EventHandler.EventPriority, List<RegisteredListener>> handlerSlots;

    /**
     * List of all HandlerLists which have been created, for use in bakeAll()
     */
    private static final List<HandlerList> allLists = new ArrayList<>();

    /**
     * Bake all handler lists. Best used just after all normal event
     * registration is complete, ie just after all modules are loaded.
     */
    public static void bakeAll() {
        synchronized (allLists) {
            for (HandlerList h : allLists) {
                h.bake();
            }
        }
    }

    /**
     * Unregister all listeners from all handler lists.
     */
    public static void unregisterAll() {
        synchronized (allLists) {
            for (HandlerList h : allLists) {
                for (List<RegisteredListener> list : h.handlerSlots.values()) {
                    list.clear();
                }
                h.handlers = null;
            }
        }
    }

    /**
     * Unregister a specific plugin's listeners from all handler lists.
     *
     * @param module The module to unregister
     */
    public static void unregisterAll(final Module module) {
        synchronized (allLists) {
            for (HandlerList h : allLists) {
                h.unregister(module);
            }
        }
    }

    /**
     * Unregister a specific listener from all handler lists.
     *
     * @param listener listener to unregister
     */
    public static void unregisterAll(EventListener listener) {
        synchronized (allLists) {
            for (HandlerList h : allLists) {
                h.unregister(listener);
            }
        }
    }

    /**
     * Create a new handler list and initialize using EventPriority.
     * <p>
     * The HandlerList is then added to meta-list for use in bakeAll()
     */
    public HandlerList() {
        this.handlerSlots = new EnumMap<>(EventHandler.EventPriority.class);
        for (EventHandler.EventPriority o : EventHandler.EventPriority.values()) {
            this.handlerSlots.put(o, new ArrayList<>());
        }
        synchronized (allLists) {
            allLists.add(this);
        }
    }

    /**
     * Register a new listener in this handler list
     *
     * @param listener listener to register
     */
    public synchronized void register(final RegisteredListener listener) {
        if (this.handlerSlots.get(listener.getPriority()).contains(listener))
            throw new IllegalStateException("This listener is already registered to priority " + listener.getPriority().toString());
        this.handlers = null;
        this.handlerSlots.get(listener.getPriority()).add(listener);
    }

    /**
     * Register a collection of new listeners in this handler list
     *
     * @param listeners listeners to register
     */
    public void registerAll(final Collection<RegisteredListener> listeners) {
        for (final RegisteredListener listener : listeners) {
            register(listener);
        }
    }

    /**
     * Remove a listener from a specific order slot
     *
     * @param listener listener to remove
     */
    public synchronized void unregister(RegisteredListener listener) {
        if (this.handlerSlots.get(listener.getPriority()).remove(listener)) {
            handlers = null;
        }
    }

    /**
     * Remove a specific module's listeners from this handler
     *
     * @param module Module to remove
     */
    public synchronized void unregister(final Module module) {
        boolean changed = false;
        for (List<RegisteredListener> list : this.handlerSlots.values()) {
            for (ListIterator<RegisteredListener> i = list.listIterator(); i.hasNext(); ) {
                if (i.next().getModule().equals(module)) {
                    i.remove();
                    changed = true;
                }
            }
        }
        if (changed) this.handlers = null;
    }

    /**
     * Remove a specific listener from this handler
     *
     * @param listener listener to remove
     */
    public synchronized void unregister(final EventListener listener) {
        boolean changed = false;
        for (List<RegisteredListener> list : this.handlerSlots.values()) {
            for (ListIterator<RegisteredListener> i = list.listIterator(); i.hasNext(); ) {
                if (i.next().getListener().equals(listener)) {
                    i.remove();
                    changed = true;
                }
            }
        }
        if (changed) this.handlers = null;
    }

    /**
     * Bake HashMap and ArrayLists to 2d array - does nothing if not necessary
     */
    public synchronized void bake() {
        if (this.handlers != null) return; // don't re-bake when still valid
        final List<RegisteredListener> entries = new ArrayList<>();
        for (final Map.Entry<EventHandler.EventPriority, List<RegisteredListener>> entry : this.handlerSlots.entrySet()) {
            entries.addAll(entry.getValue());
        }
        this.handlers = entries.toArray(new RegisteredListener[0]);
    }

    /**
     * Get the baked registered listeners associated with this handler list
     *
     * @return the array of registered listeners
     */
    public RegisteredListener[] getRegisteredListeners() {
        RegisteredListener[] handlers;
        while ((handlers = this.handlers) == null) bake(); // This prevents fringe cases of returning null
        return handlers;
    }

    /**
     * Get a specific module's registered listeners associated with this
     * handler list
     *
     * @param module The module to get the listeners of
     * @return the list of registered listeners
     */
    public static List<RegisteredListener> getRegisteredListeners(final Module module) {
        List<RegisteredListener> listeners = new ArrayList<>();
        synchronized (allLists) {
            for (final HandlerList h : allLists) {
                for (List<RegisteredListener> list : h.handlerSlots.values()) {
                    for (RegisteredListener listener : list) {
                        if (listener.getModule().equals(module)) {
                            listeners.add(listener);
                        }
                    }
                }
            }
        }
        return listeners;
    }

    /**
     * Get a list of all handler lists for every event type
     *
     * @return the list of all handler lists
     */
    @SuppressWarnings("unchecked")
    public static List<HandlerList> getHandlerLists() {
        synchronized (allLists) {
            return new ArrayList<>(allLists);
        }
    }

}
