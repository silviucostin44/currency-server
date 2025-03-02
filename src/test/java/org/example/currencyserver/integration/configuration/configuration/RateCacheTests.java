package org.example.currencyserver.integration.configuration.configuration;

import static org.example.currencyserver.TestingConstants.EUR;
import static org.example.currencyserver.TestingConstants.USD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import com.github.benmanes.caffeine.cache.Cache;
import org.example.currencyserver.client.SwopClient;
import org.example.currencyserver.model.Rate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest()
@ExtendWith(SpringExtension.class)
public class RateCacheTests {

    @Autowired
    @Qualifier("rates-cache-manager")
    private CacheManager cacheManager;

    @Autowired
    private SwopClient swopClient;

    private Cache<Object, Object> nativeCache;

    @BeforeEach
    public void setup() {
        nativeCache = (Cache) Objects.requireNonNull(cacheManager.getCache("rates")).getNativeCache();
    }

    @Test
    void testCachedValue() {
        Rate rate = swopClient.fetchSimpleRate(EUR, USD);

        Rate cachedRate = (Rate) nativeCache.getIfPresent(EUR + USD);

        assertEquals(rate, cachedRate);
    }

    @Test
    void testCacheCapacityRules() {
        for (int i = 0; i < 191; i++) {
            nativeCache.put("rate" + i, new Object());
        }

        assertNotNull(nativeCache.getIfPresent("rate0"));
        assertNotNull(nativeCache.getIfPresent("rate190"));

        nativeCache.put("rate191", new Object());
        nativeCache.cleanUp();
        assertTrue(nativeCache.asMap().size() == 191);
        assertNotNull(nativeCache.getIfPresent("rate191"));

    }

}

