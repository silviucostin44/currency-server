package org.example.currencyserver.configuration.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Schedules a method to evict all caches entries at midnight every day.
 */
public class CacheDailyEvictScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheDailyEvictScheduler.class);

    @Scheduled(cron = "@midnight")
    @CacheEvict(value = { "currencies", "rates" }, allEntries = true)
    public void evictAllCacheEntries() {
        LOGGER.trace("Nightly Evict Scheduled Job will start.");
    }
}
