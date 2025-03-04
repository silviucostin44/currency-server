package org.example.currencyserver.integration.configuration;

import static org.example.currencyserver.TestDummyDataUtil.createDummyCurrencyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.testing.FakeTicker;
import org.example.currencyserver.service.ConversionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.AopTestUtils;

/**
 * Integration test for currency caching in a simulated context with the mocking of the containing class.
 */
@ContextConfiguration
@ExtendWith(SpringExtension.class)
class CurrencyContextCacheTests {

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private FakeTicker ticker;

    private ConversionService mockService;

    @BeforeEach
    void setUp() {
        mockService = AopTestUtils.getTargetObject(conversionService);

        reset(mockService);
        Objects.requireNonNull(cacheManager.getCache("currencies")).invalidate();

        when(mockService.getAvailableCurrencies()).thenReturn(createDummyCurrencyList());
    }

    @Test
    void testGetAvailableCurrencies() {
        assertEquals(createDummyCurrencyList(), conversionService.getAvailableCurrencies());
        verify(mockService).getAvailableCurrencies();

        assertEquals(createDummyCurrencyList(), conversionService.getAvailableCurrencies());
        assertEquals(createDummyCurrencyList(), conversionService.getAvailableCurrencies());

        verifyNoMoreInteractions(mockService);
    }

    @Test
    void testGetAvailableCurrencies_cacheTTL() {
        assertEquals(createDummyCurrencyList(), conversionService.getAvailableCurrencies());
        verify(mockService).getAvailableCurrencies();

        assertEquals(createDummyCurrencyList(), conversionService.getAvailableCurrencies());
        verifyNoMoreInteractions(mockService);

        ticker.advance(Duration.ofHours(4));
        assertEquals(createDummyCurrencyList(), conversionService.getAvailableCurrencies());
        verify(mockService, times(2)).getAvailableCurrencies();
    }

    /**
     * Test caching configuration, mimicking the original one for the currency cache manager.
     */
    @EnableCaching
    @Configuration
    public static class CachingTestConfiguration {

        @Bean
        ConversionService conversionServiceMockImplementation() {
            return mock(ConversionService.class);
        }

        @Bean("currencies-cache-manager")
        public CacheManager cacheManager(FakeTicker ticker) {
            Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                    .initialCapacity(1)
                    .maximumSize(1)
                    .expireAfterAccess(Duration.ofHours(3))
                    .ticker(ticker::read);

            CaffeineCacheManager cacheManager = new CaffeineCacheManager();
            cacheManager.setCaffeine(caffeine);
            cacheManager.setCacheNames(List.of("currencies"));
            return cacheManager;
        }

        @Bean
        FakeTicker ticker() {
            return new FakeTicker();
        }
    }
}
