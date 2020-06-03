package com.sheepybot.api.entities.settings;

import com.google.common.collect.Maps;
import com.google.gson.internal.LazilyParsedNumber;
import com.sheepybot.util.Objects;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public class GuildSettings {

    /**
     * Retrieve the {@link GuildSettings} of the specified guild, should there be
     * no settings for the requested guild this will return an empty {@link GuildSettings} instance
     *
     * @param guild The {@link net.dv8tion.jda.api.entities.Guild}
     * @return A {@link GuildSettings} instance
     */
    public static GuildSettings getSettingsOf(@NotNull(value = "guild cannot be null") final Guild guild) {
        return GuildSettings.getSettingsOf(guild.getIdLong());
    }

    /**
     * Retrieve the {@link GuildSettings} of the specified guild, should there be
     * no settings for the requested guild this will return an empty {@link GuildSettings} instance
     *
     * @param guildId The guild id
     * @return A {@link GuildSettings} instance
     */
    public static GuildSettings getSettingsOf(final long guildId) {
        return new GuildSettings(guildId, Maps.newHashMapWithExpectedSize(0));
    }

    private final long guildId;
    private final Map<String, Object> settings;

    public GuildSettings(final long guildId,
                         @NotNull(value = "settings cannot be null") final Map<String, Object> settings) {
        this.guildId = guildId;
        this.settings = settings;
    }

    /**
     * @return The guild id this {@link GuildSettings} is for
     */
    public long getGuildId() {
        return this.guildId;
    }

    /**
     * @return An unmodifiable collection of settings used by this {@link GuildSettings}
     */
    public Map<String, Object> getSettings() {
        return Collections.unmodifiableMap(this.settings);
    }

    /**
     * Retrieve the value associated with a key
     *
     * @param key The key
     * @return The value associated with the key or {@code null} if there is no value
     * @throws IllegalArgumentException If the key is null or effectively null (empty)
     */
    public Object get(@NotNull(value = "key cannot be null") final String key) throws IllegalArgumentException {
        Objects.checkArgument(!key.isEmpty(), "key cannot be null or effectively null");
        return this.settings.get(key);
    }

    /**
     * Set the value for the given key overriding the previous value should there be one.
     *
     * @param key   The key to add
     * @param value The value associated with the given {@code key}
     */
    public void set(@NotNull(value = "key cannot be null") final String key,
                    @NotNull(value = "value cannot be null") final Object value) {
        this.settings.put(key, value);
    }

    /**
     * Retrieves a {@code key} from the internal {@link Map} and attempts to
     * parse it as an int
     *
     * @param key The key
     * @param def The default value to return
     * @return The {@link Integer} value or {@code def} if there is no key by that name
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public int getInt(@NotNull(value = "key cannot be null") final String key,
                      final int def) throws IllegalArgumentException {
        return getAsNumber(key, def).intValue();
    }

    /**
     * Retrieves a {@code key} from the internal {@link Map} and attempts to
     * parse it as a long
     *
     * @param key The key
     * @param def The default value to return
     * @return The {@link Long} value or {@code def} if the given key has no value
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public long getLong(@NotNull(value = "key cannot be null") final String key,
                        final long def) throws IllegalArgumentException {
        return this.getAsNumber(key, def).longValue();
    }

    /**
     * Retrieves a {@code key} from the internal {@link Map} and attempts to
     * parse it as a float
     *
     * @param key The key
     * @param def The default value to return
     * @return The {@link Float} value or {@code def} if the given key has no value
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public float getFloat(@NotNull(value = "key cannot be null") final String key,
                          final float def) throws IllegalArgumentException {
        Objects.checkArgument(!key.isEmpty(), "key cannot be null or effectively null");
        return this.getAsNumber(key, def).floatValue();
    }

    /**
     * Retrieves a {@code key} from the internal {@link Map} and attempts to
     * parse it as a double
     *
     * @param key The key
     * @param def The default value to return
     * @return The {@link Double} value or {@code def} if the given key has no value
     * @throws IllegalArgumentException If the value for the given key is not a {@link Number}
     */
    public double getDouble(@NotNull(value = "key cannot be null") final String key,
                            final double def) throws IllegalArgumentException {
        Objects.checkArgument(!key.isEmpty(), "key cannot be null or effectively null");
        return this.getAsNumber(key, def).doubleValue();
    }

    /**
     * @param key The key
     * @return The {@link Boolean} value
     */
    public boolean getBoolean(@NotNull(value = "key cannot be null") final String key) throws IllegalArgumentException {
        return this.getBoolean(key, false);
    }

    /**
     * @param key The key
     * @param def The default value to return
     * @return The {@link Boolean} value or {@code def} if the given key has no value
     */
    public boolean getBoolean(@NotNull(value = "key cannot be null") final String key,
                              final boolean def) throws IllegalArgumentException {
        Objects.checkArgument(!key.isEmpty(), "key cannot be null or effectively null");
        return Boolean.parseBoolean(key);
    }

    /**
     * @param key The key
     * @return The {@code #toString()} value of the {@link Object} or {@code null} should there be no value
     * @throws IllegalArgumentException If the key is null or effectively null (empty)
     * @throws NullPointerException     If there is no value for the given key
     */
    public String getString(@NotNull(value = "key cannot be null") final String key) throws IllegalArgumentException, NullPointerException {
        Objects.checkArgument(!key.isEmpty(), "key cannot be null or effectively null");

        final Object result = this.get(key);

        return result == null ? null : result.toString();
    }

    /**
     * @param key The key
     * @param def The default value to return
     * @return The {@link String} value or {@code def} if the given key has no value
     * @throws IllegalArgumentException If the key is null or effectively null (empty)
     */
    public String getString(@NotNull(value = "key cannot be null") final String key,
                            @NotNull(value = "def cannot be null") final String def) throws IllegalArgumentException {
        Objects.checkArgument(!key.isEmpty(), "key cannot be null or effectively null");

        final Object result = this.get(key);

        return result == null || result.toString().isEmpty() ? def : result.toString();
    }

    private Number getAsNumber(final String key, final Number def) {
        Objects.checkArgument(!key.isEmpty(), "key cannot be null or effectively null");
        final Object result = this.get(key);
        if (result != null) {
            return new LazilyParsedNumber(result.toString());
        } else {
            return def;
        }
    }

}