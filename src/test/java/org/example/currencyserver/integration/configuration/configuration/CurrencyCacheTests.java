package org.example.currencyserver.integration.configuration.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Objects;

import org.example.currencyserver.model.Currency;
import org.example.currencyserver.service.ConversionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest()
@ExtendWith(SpringExtension.class)
public class CurrencyCacheTests {

    @Autowired
    @Qualifier("currencies-cache-manager")
    private CacheManager cacheManager;

    @Autowired
    private ConversionService conversionService;

    private Cache cache;

    @BeforeEach
    public void setup() {
        cache = Objects.requireNonNull(cacheManager.getCache("currencies"));
    }

    @Test
    void testCachedValue() {
        List<Currency> availableCurrencies = conversionService.getAvailableCurrencies();

        List cachedCurrencies = cache.get(SimpleKey.EMPTY, List.class);

        assertEquals(availableCurrencies, cachedCurrencies);
    }

    @Test
    void testCacheCapacityRules() {
        conversionService.getAvailableCurrencies();

        assertNotNull(cache.get(SimpleKey.EMPTY));

        SimpleKey someKey = new SimpleKey("someKey");
        cache.put(someKey, new Object());
        assertNull(cache.get(SimpleKey.EMPTY));
        assertNotNull(cache.get(someKey));

    }

}

