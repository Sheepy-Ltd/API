package com.sheepybot.internal.caching.caches;

import com.google.common.cache.CacheLoader;
import com.sheepybot.api.entities.language.I18n;
import com.sheepybot.api.entities.language.Language;
import com.sheepybot.api.entities.settings.GuildSettings;
import com.sheepybot.internal.caching.EntityLoadingCache;
import org.jetbrains.annotations.NotNull;

public class LanguageCache extends EntityLoadingCache<Long, I18n> {

    public LanguageCache() {
        super(new LanguageCacheLoader());
    }

    private static final class LanguageCacheLoader extends CacheLoader<Long, I18n> {

        @Override
        public I18n load(@NotNull(value = "key cannot be null") final Long key) {
            final GuildSettings settings = GuildSettings.getSettingsOf(key);

            final String code = settings.getString("language", "en_GB");
            final Language language = Language.getByCode(code);

            return language == null ? I18n.getDefaultI18n() : I18n.getI18n(language);
        }

    }

}
