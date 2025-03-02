package org.example.currencyserver.configuration.cache;

import java.time.Duration;
import java.util.List;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean("currencies-caffeine")
    Caffeine currenciesCaffeineConfiguration() {
        return Caffeine.newBuilder()
                .initialCapacity(1)
                .maximumSize(1)
                .expireAfterAccess(Duration.ofHours(3));
    }

    @Bean("rates-caffeine")
    Caffeine ratesCaffeineConfiguration() {
        return Caffeine.newBuilder()
                .maximumSize(191)
                .expireAfterAccess(Duration.ofMinutes(30));
    }

    @Bean("currencies-cache-manager")
    CacheManager currenciesCacheManager(@Qualifier("currencies-caffeine") Caffeine caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeine);
        cacheManager.setCacheNames(List.of("currencies"));
        return cacheManager;
    }

    @Primary
    @Bean("rates-cache-manager")
    CacheManager ratesCacheManager(@Qualifier("rates-caffeine") Caffeine caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeine);
        cacheManager.setCacheNames(List.of("rates"));
        return cacheManager;
    }
}
