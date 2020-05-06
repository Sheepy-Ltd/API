package com.sheepybot.internal.caching.caches;

import com.google.common.cache.CacheLoader;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import com.sheepybot.api.entities.configuration.GuildSettings;
import com.sheepybot.internal.caching.EntityLoadingCache;

import java.util.Map;

public class GuildConfigCache extends EntityLoadingCache<String, GuildSettings> {

    public GuildConfigCache(){
        super(new GuildConfigCacheLoader());
    }

    private static final class GuildConfigCacheLoader extends CacheLoader<String, GuildSettings> {

        @Override
        public GuildSettings load(@NotNull(value = "key cannot be null") final String key) {
            final Map<String, Object> map = Maps.newHashMap();
            map.put("server_id", key);

            return GuildSettings.fromMap(map);
        }

    }

}
