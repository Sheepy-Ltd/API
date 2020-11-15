package com.sheepybot.api.entities.language;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Represents a list of supported languages used for localization of the bot
 * <p>
 * <p>New languages should only be added to this ever growing list once
 * their associated resource bundles have been completed.</p>
 * <p>
 * <p>If a lang doesn't have a translated value for its key,
 * the default value should be english.</p>
 */
public enum Language {

    /**
     * British English
     */
    ENGLISH("en", "GB", "English", "English", false),

    /**
     * Used whenever we don't know what language we're dealing with
     */
    UNKNOWN("en", "GB", "Unknown", "Unknown", true);

    /**
     * Retrieve a lang by its country code
     *
     * @param code The lang code
     * @return The {@link Language}
     */
    public static Language getByCode(@NotNull("id cannot be null") final String code) {
        for (final Language language : Language.values()) {
            if (language.getCode().equalsIgnoreCase(code) && !language.isFake()) {
                return language;
            }
        }
        return null;
    }

    private final String code;
    private final Locale locale;
    private final String nativeName;
    private final String englishName;
    private final boolean isFake;

    Language(final String language,
             final String country,
             final String nativeName,
             final String englishName,
             final boolean isFake) {
        this.code = language.toLowerCase() + "_" + country.toUpperCase();
        this.locale = new Locale(language, country);
        this.nativeName = nativeName;
        this.englishName = englishName;
        this.isFake = isFake;
    }

    /**
     * @return The language code
     */
    public String getCode() {
        return this.code;
    }

    /**
     * @return The {@link Locale}
     */
    public Locale getLocale() {
        return this.locale;
    }

    /**
     * @return The way this {@link Language} is natively spelt
     */
    public String getNativeName() {
        return this.nativeName;
    }

    /**
     * @return The english spelling
     */
    public String getEnglishName() {
        return this.englishName;
    }

    /**
     * Used to tell whether this language isn't what it identified as but is instead a default value
     * <p>This is primary used for {@link Language#UNKNOWN}</p>
     *
     * @return {@code true} if this language is fake, {@code false} otherwise
     */
    public boolean isFake() {
        return this.isFake;
    }
}
