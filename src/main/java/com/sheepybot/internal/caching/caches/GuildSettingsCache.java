package com.sheepybot.internal.caching.caches;

import com.google.common.cache.CacheLoader;
import com.google.common.collect.Maps;
import com.sheepybot.api.entities.settings.GuildSettings;
import com.sheepybot.internal.caching.EntityLoadingCache;
import org.jetbrains.annotations.NotNull;

public class GuildSettingsCache extends EntityLoadingCache<Long, GuildSettings> {

    public GuildSettingsCache() {
        super(new GuildSettingsCacheLoader());
    }

    private static final class GuildSettingsCacheLoader extends CacheLoader<Long, GuildSettings> {

        @Override
        public GuildSettings load(@NotNull(value = "key cannot be null") final Long key) {
            return new GuildSettings(key, Maps.newHashMap());
        }

    }

}
