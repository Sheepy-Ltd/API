package com.sheepybot.api.entities.database.object;

import com.google.common.collect.Lists;
import com.sheepybot.util.Objects;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class DBCursor implements Iterator<DBObject> {

    private final List<DBObject> cursor;
    private int cursorPos;

    /**
     * Construct an empty {@link DBCursor}
     */
    public DBCursor() {
        this.cursor = Lists.newArrayList();
    }

    /**
     * @param cursor The {@link List} of {@link DBObject}s to initialize this {@link DBCursor} with
     */
    public DBCursor(@NotNull("cursor cannot be null") final List<DBObject> cursor) {
        this.cursor = cursor;
    }

    /**
     * Add a {@link DBObject} to the internal {@link List}
     *
     * @param object The {@link DBObject}  to use
     * @return This {@link DBCursor} instance, useful for chaining.
     */
    public DBCursor add(@NotNull("key cannot be null") final DBObject object) {
        this.cursor.add(object);
        return this;
    }

    /**
     * Copy the contents of a {@link List} to the internal {@link List}
     *
     * @param data The {@link List} to copy
     */
    public void addAll(@NotNull("data cannot be null") final List<DBObject> data) {
        this.cursor.addAll(data);
    }

    /**
     * Copy the contents of a {@link DBCursor} to the internal {@link List}
     *
     * @param data The {@link List} to copy
     */
    public void addAll(@NotNull("data cannot be null") final DBCursor data) {
        this.cursor.addAll(data.getCursor());
    }

    /**
     * Iterate over all element in the internal {@link List} without incrementing the internal counter
     *
     * @param consumer The {@link Consumer}
     */
    public void forEach(@NotNull("consumer cannot be null") final Consumer<DBObject> consumer) {
        this.cursor.forEach(consumer);
    }

    /**
     * Returns a stream consisting of the results of applying the function to the elements of this {@link DBCursor}
     *
     * @param mapper A {@link Function} to apply to each element in this {@link DBCursor}
     * @param <R>    The element type of the new {@link Stream}
     * @return The new {@link Stream}
     */
    public <R> Stream<R> map(@NotNull("function cannot be null") final Function<DBObject, ? extends R> mapper) {
        return this.cursor.stream().map(mapper);
    }

    /**
     * @return {@code true} if there's anymore elements to iterate over, {@code false} otherwise
     */
    public boolean hasNext() {
        return this.cursorPos != this.cursor.size();
    }

    /**
     * @return The next {@link DBObject}
     *
     * @throws IllegalStateException If there are no more elements to iterate over
     */
    public DBObject next() {
        Objects.checkState(this.hasNext(), "no more elements");
        return this.cursor.get(this.cursorPos++);
    }

    /**
     * @return The size of the internal {@link List}
     */
    public int size() {
        return this.cursor.size();
    }

    /**
     * @return A clone of the internal {@link List}
     */
    public List<DBObject> getCursor() {
        return Lists.newArrayList(this.cursor);
    }
}
