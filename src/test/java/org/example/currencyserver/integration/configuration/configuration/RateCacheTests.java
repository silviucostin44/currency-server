package org.example.currencyserver.integration.configuration.configuration;

import static org.example.currencyserver.TestingConstants.EUR;
import static org.example.currencyserver.TestingConstants.USD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Objects;

import org.example.currencyserver.client.SwopClient;
import org.example.currencyserver.model.Rate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
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

    private Cache cache;

    @BeforeEach
    public void setup() {
        cache = Objects.requireNonNull(cacheManager.getCache("rates"));
    }

    @Test
    void testCachedValue() {
        Rate rate = swopClient.fetchSimpleRate(EUR, USD);

        Rate cachedRate = cache.get(EUR + USD, Rate.class);

        assertEquals(rate, cachedRate);
    }

    @Test
    void testCacheCapacityRules() {
        for (int i = 0; i <= 191; i++) {
            cache.put("rate" + i, new Object());
        }

        assertNotNull(cache.get("rate190"));

        cache.put("rate191", new Object());
        assertNull(cache.get("rate0"));
        assertNotNull(cache.get("rate191"));

    }

}

