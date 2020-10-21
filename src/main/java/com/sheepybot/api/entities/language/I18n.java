package com.sheepybot.api.entities.language;

import com.google.common.collect.Maps;
import com.sheepybot.Bot;
import com.sheepybot.util.Objects;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Used for internationalization of the bot
 */
public class I18n {

    /**
     * The fallback {@link I18n} instance
     * <p>
     * <p>Should an {@link I18n} instance not be able
     * to handle a query, this should be queried instead.</p>
     */
    private static I18n FALLBACK_I18N = new I18n(Language.ENGLISH);

    private static final Logger LOGGER = LoggerFactory.getLogger(I18n.class);
    private static final Pattern DOUBLE_QUOTE = Pattern.compile("''");

    private static final Map<String, I18n> REGISTRY_MAP = Maps.newHashMap();

    /**
     * @return The default {@link I18n}
     */
    public static I18n getDefaultI18n() {
        return FALLBACK_I18N;
    }

    /**
     * Set the default language file to use for messages
     *
     * @param file The file name
     */
    public static void setDefaultI18n(@NotNull(value = "file cannot be null") String file) {


        try {

            final int ext = file.indexOf('.');
            if (ext != -1) {
                file = file.substring(0, ext);
            }

            LOGGER.info(String.format("Attempting to set language as %s...", file));

            Language language = Language.getByCode(file);
            if (language == null) {
                LOGGER.info("Detected custom language file, setting language as unknown...");
                language = Language.UNKNOWN;
            }

            LOGGER.info("Loading resource bundle...");

            final ResourceBundle bundle = getBundleFromURL(file, new File("/lang/" + file + ".properties").toURI().toURL());
            if (bundle == null) {
                LOGGER.info(String.format("Failed to load resource bundle %s", file));
            } else {

                LOGGER.info("Resource bundle has been loaded, importing language settings...");

                FALLBACK_I18N = new I18n(Language.UNKNOWN);
                FALLBACK_I18N.load(bundle);

                LOGGER.info(String.format("Language set to %s.", (language == Language.UNKNOWN ? "custom language file" : language.getCode())));
            }

        } catch (final MalformedURLException ex) {
            LOGGER.info("An error occurred and the language file could not be set", ex);
        }

    }

    /**
     * Retrieve an {@link I18n} instance based on the specified {@link Language} of the {@link Guild}
     * <p>
     * <p>Should the {@link Guild} have no configured language, the default returned is the english {@link I18n}</p>
     *
     * @param guild The {@link Guild}
     * @return The {@link I18n} instance
     */
    public static I18n getI18n(@NotNull(value = "guild cannot be null") final Guild guild) {
        return getI18n(guild.getIdLong());
    }

    /**
     * Retrieve an {@link I18n} instance based on the specified {@link Language} of the {@link Guild}
     * <p>
     * <p>Should the {@link Guild} have no configured language, the default returned is the english version</p>
     *
     * @param id The guild id as a {@link Long}
     *
     * @return The {@link I18n} instance
     */
    public static I18n getI18n(final long id) {
        return getDefaultI18n();
    }

    /**
     * Retrieve an {@link I18n} instance by {@link Language}
     * <p>
     * <p>If there is no {@link I18n} for the requested {@link Language}, the default returned
     * is {@link Language#ENGLISH}.</p>
     *
     * @param language The {@link Language}
     *
     * @return The {@link I18n} instance
     */
    public static I18n getI18n(@NotNull(value = "language cannot be null") final Language language) {
        return REGISTRY_MAP.getOrDefault(language.getCode(), getDefaultI18n());
    }

    /**
     * Load translation sources of a {@link Class}
     * <p>
     * <p>Should any identical translation keys already exist, they will be overwritten.</p>
     *
     * @param clazz The {@link Class}
     */
    public static void loadI18n(@NotNull(value = "clazz cannot be null") final Class clazz) {

        LOGGER.info("Loading language files...");

        for (final Language language : Language.values()) {

            if (language.isFake()) continue;

            try {
                LOGGER.info(String.format("Loading language file %s.properties...", language.getCode()));

                final ResourceBundle bundle = getBundleFromURL(language.getCode(), new File("/lang/" + language.getCode() + ".properties").toURI().toURL());
                if (bundle == null) {
                    LOGGER.info(String.format("Couldn't load language file %s.properties, this language wont be available for use.", language.getCode()));
                    continue;
                }

                final I18n i18n = REGISTRY_MAP.computeIfAbsent(language.getCode(), __ -> new I18n(language));
                i18n.load(bundle);

            } catch (final MalformedURLException ignored) {
                LOGGER.info(String.format("Couldn't load external language file %s.properties, skipping it...", language.getCode()));
            }

        }

    }

    /**
     * Extract internal language files to the running directory
     */
    public static void extractLanguageFiles() {

        final File lang = new File("lang/");

        if (!lang.exists()) {

            LOGGER.info("Attempting to extract internal language files...");

            lang.mkdirs();

            for (final Language language : Language.values()) {
                if (language.isFake()) continue;

                final String languageFileName = String.format("%s.properties", language.getCode());
                final File file = new File(lang, languageFileName);
                if (!file.exists()) {
                    LOGGER.info(String.format("Extracting internal language file %s to %s", languageFileName, file.getAbsolutePath()));
                    try {
                        FileUtils.copyURLToFile(Bot.get().getClass().getResource(String.format("/lang/%s", languageFileName)), file);
                        LOGGER.info(String.format("File created at: %s", file.getAbsolutePath()));
                    } catch (IOException e) {
                        LOGGER.info(String.format("Failed to extract internal language file %s to %s", languageFileName, file.getAbsolutePath()));
                    }
                }

            }

        }

    }

    private final Language language;
    private final Map<String, String> translations;

    private I18n(@NotNull(value = "language cannot be null") final Language language) {
        this.language = language;
        this.translations = Maps.newHashMap();
    }

    /**
     * @return The {@link Language}
     */
    public Language getLanguage() {
        return this.language;
    }

    /**
     * Retrieve the value of an associated key
     *
     * @param key The key
     *
     * @return The associated {@link String} value, or {@code null} if no translation exists
     */
    public String getString(final String key) {
        return this.translations.get(key);
    }

    /**
     * Translate the input string
     *
     * @param key  The message key
     * @param args The arguments
     *
     * @return The translated string
     */
    public String tl(@NotNull(value = "key cannot be null") final String key,
                     final Object... args) {
        Objects.checkArgument(!key.isEmpty(), "key cannot be effectively null");

        if (args.length == 0) {
            return DOUBLE_QUOTE.matcher(getMessage(key)).replaceAll("'");
        } else {
            return MessageFormat.format(getMessage(key), args);
        }
    }

    /**
     * @param key The message key
     *
     * @return The string
     */
    private String getMessage(@NotNull(value = "key cannot be null") final String key) {
        Objects.checkArgument(!key.isEmpty(), "key cannot be effectively null");

        String translation = getString(key);
        if (translation == null) {
            translation = getDefaultI18n().getString(key);
        }

        return (translation == null ? key : translation);
    }

    /**
     * Load all key/value pairs from a {@link ResourceBundle} into this {@link I18n}
     *
     * @param bundle The {@link ResourceBundle} to load from
     */
    private void load(@NotNull(value = "bundle cannot be null") final ResourceBundle bundle) {

        final Enumeration<String> iterator = bundle.getKeys();
        while (iterator.hasMoreElements()) {
            final String key = iterator.nextElement();
            this.translations.put(key, bundle.getString(key));
        }

    }

    private static ResourceBundle getBundle(final Language language, final ClassLoader loader) {
        try {
            return ResourceBundle.getBundle("lang." + language.getCode(), language.getLocale(), loader);
        } catch (final MissingResourceException ignored) {
        }
        return null;
    }

    private static ResourceBundle getBundleFromURL(final String language, final URL url) {
        try {
            return ResourceBundle.getBundle("lang." + language, Language.UNKNOWN.getLocale(), new URLClassLoader(new URL[]{url}));
        } catch (final MissingResourceException ignored) {
        }
        return null;
    }

}
