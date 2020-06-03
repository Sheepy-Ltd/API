package com.sheepybot.api.entities.database.object;

import com.google.common.collect.Maps;
import com.google.gson.internal.LazilyParsedNumber;
import com.sheepybot.util.Objects;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class DBObject {

    private final Map<String, Object> data;

    /**
     * Initialize an empty {@link DBObject}
     */
    public DBObject() {
        this.data = new LinkedHashMap<>();
    }

    /**
     * @param data The {@link Map} to initialize this {@link DBObject} with
     */
    public DBObject(@NotNull(value = "initMap cannot be null") final Map<String, Object> data) {
        this.data = data;
    }

    /**
     * @param key The key to search for
     * @return {@code true} if the key is present, {@code false} otherwise
     */
    public boolean has(@NotNull(value = "key cannot be null") final String key) {
        Objects.checkNotBlank(key, "key cannot be empty");
        return this.data.containsKey(key);
    }

    /**
     * Add a key/value pair to the internal data {@link Map}
     *
     * @param key The key
     * @param value The value for the given key
     *
     * @return This {@link DBObject} instance, useful for chaining.
     */
    public DBObject add(@NotNull(value = "key cannot be null") final String key,
                       @NotNull(value = "value cannot be null") final Object value) {
        Objects.checkNotBlank(key, "key cannot be empty");
        Objects.checkNotNull(value, "value cannot be null");
        this.data.put(key, value);
        return this;
    }

    /**
     * Copy the contents of a {@link Map} to the internal {@link Map}
     *
     * @param data The {@link Map} to copy
     */
    public void addAll(@NotNull(value = "data cannot be null") final Map<String, Object> data) {
        this.data.putAll(data);
    }

    /**
     * Iterate over all element in the internal {@link Map}
     *
     * @param consumer The {@link BiConsumer}
     */
    public void forEach(@NotNull(value = "consumer cannot be null") final BiConsumer<String, Object> consumer) {
        this.data.forEach(consumer);
    }

    /**
     * @param key The key
     *
     * @return The {@link Integer} value associated with the key
     *
     * @throws IllegalArgumentException If the key is null or effectively null (empty)
     * @throws NullPointerException     If there is no value for the given key
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public int getInt(@NotNull(value = "key cannot be null") final String key) throws IllegalArgumentException, NullPointerException {
        Objects.checkNotBlank(key, "key cannot be empty");

        final Object result = this.get(key);
        if (result == null) {
            this.throwError(key);
        }

        return this.toNumber(result).intValue();
    }

    /**
     * Retrieve the value associated with a key
     *
     * @param key The key
     *
     * @return The value associated with the key or {@code null} if there is no value
     *
     * @throws IllegalArgumentException If the key is null or effectively null (empty)
     */
    public Object get(@NotNull(value = "key cannot be null") final String key) throws IllegalArgumentException {
        Objects.checkNotBlank(key, "key cannot be empty");
        return this.data.get(key);
    }

    private void throwError(final String key) {
        throw new NullPointerException("No value exists for key '" + key + "'");
    }

    private Number toNumber(final Object node) {
        return new LazilyParsedNumber(node.toString());
    }

    /**
     * @param key The key
     * @param def The default value to return
     *
     * @return The {@link Integer} value or {@code def} if the given key has no value
     *
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public int getInt(@NotNull(value = "key cannot be null") final String key,
                      final int def) throws IllegalArgumentException {
        final Object result = this.get(key);
        if (result == null) {
            return def;
        }

        return this.toNumber(result).intValue();
    }

    /**
     * @param key The key
     *
     * @return The {@link Long} value
     *
     * @throws IllegalArgumentException If the key is null or effectively null (empty)
     * @throws NullPointerException     If there is no value for the given key
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public long getLong(@NotNull(value = "key cannot be null") final String key) throws IllegalArgumentException, NullPointerException {
        final Object result = this.get(key);
        if (result == null) {
            this.throwError(key);
        }

        return this.toNumber(result).longValue();
    }

    /**
     * @param key The key
     * @param def The default value to return
     *
     * @return The {@link Long} value or {@code def} if the given key has no value
     *
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public long getLong(@NotNull(value = "key cannot be null") final String key,
                        final long def) throws IllegalArgumentException {
        final Object result = this.get(key);
        if (result == null) {
            return def;
        }

        return this.toNumber(result).longValue();
    }

    /**
     * @param key The key
     *
     * @return The {@link Float} value
     *
     * @throws IllegalArgumentException If the key is null or effectively null (empty)
     * @throws NullPointerException     If there is no value for the given key
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public float getFloat(@NotNull(value = "key cannot be null") final String key) throws IllegalArgumentException, NullPointerException {
        final Object result = this.get(key);
        if (result == null) {
            this.throwError(key);
        }

        return this.toNumber(result).floatValue();
    }

    /**
     * @param key The key
     * @param def The default value to return
     *
     * @return The {@link Float} value or {@code def} if the given key has no value
     *
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public float getFloat(@NotNull(value = "key cannot be null") final String key,
                          final long def) throws IllegalArgumentException {
        final Object result = this.get(key);
        if (result == null) {
            return def;
        }

        return this.toNumber(result).floatValue();
    }

    /**
     * @param key The key
     *
     * @return The {@link Double} value
     *
     * @throws IllegalArgumentException If the key is null or effectively null (empty)
     * @throws NullPointerException     If there is no value for the given key
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public double getDouble(@NotNull(value = "key cannot be null") final String key) throws IllegalArgumentException, NullPointerException {
        final Object result = this.get(key);
        if (result == null) {
            this.throwError(key);
        }

        return this.toNumber(result).doubleValue();
    }

    /**
     * @param key The key
     * @param def The default value to return
     *
     * @return The {@link Double} value or {@code def} if the given key has no value
     *
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public double getDouble(@NotNull(value = "key cannot be null") final String key,
                            final double def) throws IllegalArgumentException {
        final Object result = this.get(key);
        if (result == null) {
            return def;
        }

        return this.toNumber(result).doubleValue();
    }

    /**
     * @param key The key
     *
     * @return The {@link Boolean} value
     *
     * @throws IllegalArgumentException If the value for the given key is not a {@link Boolean}
     */
    public boolean getBoolean(@NotNull(value = "key cannot be null") final String key) throws IllegalArgumentException {
        return this.getBoolean(key, false);
    }

    /**
     * @param key The key
     * @param def The default value to return
     *
     * @return The {@link Boolean} value or {@code def} if the given key has no value
     *
     * @throws IllegalArgumentException If the value for the given key is not a {@link Boolean}
     */
    public boolean getBoolean(@NotNull(value = "key cannot be null") final String key,
                              final boolean def) throws IllegalArgumentException {
        final Object result = this.get(key);
        if (result == null) {
            return def;
        }

        return (Boolean) result;
    }

    /**
     * @param key The key
     *
     * @return The {@link String} value
     *
     * @throws IllegalArgumentException If the key is null or effectively null (empty)
     * @throws NullPointerException     If there is no value for the given key
     */
    public String getString(@NotNull(value = "key cannot be null") final String key) throws IllegalArgumentException, NullPointerException {
        final Object result = this.get(key);
        if (result == null) {
            this.throwError(key);
        }

        return result.toString();
    }

    /**
     * @param key The key
     * @param def The default value to return
     *
     * @return The {@link String} value or {@code def} if the given key has no value
     *
     * @throws IllegalArgumentException If the key is null or effectively null (empty)
     */
    public String getString(@NotNull(value = "key cannot be null") final String key,
                            @NotNull(value = "def cannot be null") final String def) throws IllegalArgumentException {
        final Object result = this.get(key);
        if (result == null || result.toString().isEmpty()) {
            return def;
        }

        return result.toString();
    }

    /**
     * @return {@code true} if there is no data, {@code false} otherwise
     */
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    /**
     * @return How many elements there are inside of this {@link DBObject}
     */
    public int size() {
        return this.data.size();
    }

    /**
     * Get a clone of this {@link DBObject}s internal {@link Map}
     *
     * @return A clone of the internal {@link Map}
     */
    public Map<String, Object> getData() {
        return Maps.newHashMap(this.data);
    }
}
