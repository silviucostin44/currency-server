package org.example.currencyserver.client;

import java.util.List;
import java.util.Objects;

import org.example.currencyserver.model.Currency;
import org.example.currencyserver.model.Rate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component // todo: scope?
public class SwopClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwopClient.class.getSimpleName());

    private final RestClient restClient;

    private final CacheManager cacheManager;

    @Autowired
    public SwopClient(final RestClient.Builder restClientBuilder, final CacheManager cacheManager) {
        this.restClient = restClientBuilder.build();
        this.cacheManager = cacheManager;
    }

    public List<Currency> fetchAvailableCurrencies() {
        return restClient.get()
                .uri("/currencies")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    @Cacheable(cacheNames = "rates", key = "#baseCurrency + #quoteCurrency", cacheManager = "rates-cache-manager")
    public Rate fetchSimpleRate(final String baseCurrency, final String quoteCurrency) {
        final String key = baseCurrency + quoteCurrency;
        Cache cache = Objects.requireNonNull(cacheManager.getCache("rates"));
        Rate rate = cache.get(key, Rate.class);
        if (rate != null) {
            return rate;
        }

        Rate fetchedRate = restClient.get()
                .uri("/rates/{baseCurrency}/{quoteCurrency}", baseCurrency, quoteCurrency)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatusCode.valueOf(403)),
                          (request, response) -> {
                              final String errorMessage =
                                      "Request to SWOP for simple rate failed with 403 error status. Please check the format of the currencies or the ApiKey";
                              LOGGER.error(errorMessage);
                              throw new RestClientException(errorMessage);
                          })
                .body(Rate.class);
        cache.put(key, fetchedRate);
        return fetchedRate;
    }
}
