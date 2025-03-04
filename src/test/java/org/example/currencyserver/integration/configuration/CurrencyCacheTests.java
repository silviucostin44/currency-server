package org.example.currencyserver.integration.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Objects;

import com.github.benmanes.caffeine.cache.Cache;
import org.example.currencyserver.model.Currency;
import org.example.currencyserver.service.ConversionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Integration test for currency caching in a spring boot context.
 */
@SpringBootTest()
@ExtendWith(SpringExtension.class)
public class CurrencyCacheTests {

    @Autowired
    @Qualifier("currencies-cache-manager")
    private CacheManager cacheManager;

    @Autowired
    private ConversionService conversionService;

    private Cache<Object, Object> nativeCache;

    @BeforeEach
    public void setup() {
        nativeCache = (Cache) Objects.requireNonNull(cacheManager.getCache("currencies")).getNativeCache();
    }

    @Test
    void testCachedValue() {
        List<Currency> availableCurrencies = conversionService.getAvailableCurrencies();

        List cachedCurrencies = (List) nativeCache.getIfPresent(SimpleKey.EMPTY);

        assertEquals(availableCurrencies, cachedCurrencies);
    }

    @Test
    void testCacheCapacityRules() {
        conversionService.getAvailableCurrencies();

        assertNotNull(nativeCache.getIfPresent(SimpleKey.EMPTY));

        SimpleKey someKey = new SimpleKey("someKey");
        nativeCache.put(someKey, new Object());
        nativeCache.cleanUp();
        assertNull(nativeCache.getIfPresent(SimpleKey.EMPTY));
        assertNotNull(nativeCache.getIfPresent(someKey));

    }

}

