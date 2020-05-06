package com.sheepybot.internal.caching.caches;

import com.google.common.cache.CacheLoader;
import com.sheepybot.api.entities.economy.account.Account;
import com.sheepybot.internal.caching.EntityLoadingCache;

public class EconomyCache extends EntityLoadingCache<String, Account> {

    EconomyCache() {
        super(new EconomyCacheLoader());
    }

    private static final class EconomyCacheLoader extends CacheLoader<String, Account> {

        @Override
        public Account load(final String key) throws Exception {
            return null;
        }

    }

}
