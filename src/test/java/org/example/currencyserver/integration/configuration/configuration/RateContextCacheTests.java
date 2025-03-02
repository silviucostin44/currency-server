package org.example.currencyserver.integration.configuration.configuration;

import static org.example.currencyserver.TestingConstants.EUR;
import static org.example.currencyserver.TestingConstants.USD;
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
import org.example.currencyserver.client.SwopClient;
import org.example.currencyserver.model.Rate;
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

@ContextConfiguration
@ExtendWith(SpringExtension.class)
class RateContextCacheTests {

    @Autowired
    private SwopClient swopClient;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private FakeTicker ticker;

    private SwopClient mockClient;

    private Rate dummyRate;

    @BeforeEach
    void setUp() {
        mockClient = AopTestUtils.getTargetObject(swopClient);

        reset(mockClient);
        Objects.requireNonNull(cacheManager.getCache("currencies")).invalidate();

        dummyRate = new Rate(EUR, USD, 1.5d);
        when(mockClient.fetchSimpleRate(EUR, USD)).thenReturn(dummyRate);
    }

    @Test
    void testGetAvailableCurrencies() {
        assertEquals(dummyRate, swopClient.fetchSimpleRate(EUR, USD));
        verify(mockClient).fetchSimpleRate(EUR, USD);

        assertEquals(dummyRate, swopClient.fetchSimpleRate(EUR, USD));
        assertEquals(dummyRate, swopClient.fetchSimpleRate(EUR, USD));

        verifyNoMoreInteractions(mockClient);
    }

    @Test
    void testGetAvailableCurrencies_cacheTTL() {
        assertEquals(dummyRate, swopClient.fetchSimpleRate(EUR, USD));
        verify(mockClient).fetchSimpleRate(EUR, USD);

        assertEquals(dummyRate, swopClient.fetchSimpleRate(EUR, USD));
        verifyNoMoreInteractions(mockClient);

        ticker.advance(Duration.ofHours(1));
        assertEquals(dummyRate, swopClient.fetchSimpleRate(EUR, USD));
        verify(mockClient, times(2)).fetchSimpleRate(EUR, USD);
    }

    @EnableCaching
    @Configuration
    public static class CachingTestConfiguration {

        @Bean
        SwopClient swopClientMockImplementation() {
            return mock(SwopClient.class);
        }

        @Bean("rates-cache-manager")
        public CacheManager cacheManager(FakeTicker ticker) {
            Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                    .maximumSize(191)
                    .expireAfterAccess(Duration.ofMinutes(30))
                    .ticker(ticker::read);

            CaffeineCacheManager cacheManager = new CaffeineCacheManager();
            cacheManager.setCaffeine(caffeine);
            cacheManager.setCacheNames(List.of("rates"));
            return cacheManager;
        }

        @Bean
        FakeTicker ticker() {
            return new FakeTicker();
        }
    }
}
