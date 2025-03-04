package org.example.currencyserver.client;

import java.util.List;

import org.example.currencyserver.common.exception.ErrorMessages;
import org.example.currencyserver.model.Currency;
import org.example.currencyserver.model.Rate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Acts as rest web client for communication with SWOP server. Retrieves currency and conversion rates data.
 */
@Component
public class SwopClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwopClient.class.getSimpleName());

    private final RestClient restClient;

    @Autowired
    public SwopClient(final RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    /**
     * Fetches all the available currencies.
     *
     * @return a list of currencies.
     */
    public List<Currency> fetchAvailableCurrencies() {
        return restClient.get()
                .uri("/currencies")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    /**
     * Fetches a conversion rate between two currency.
     *
     * @param baseCurrency  the currency to convert from.
     * @param quoteCurrency the currency to convert to.
     * @return the conversion rate.
     */
    @Cacheable(cacheNames = "rates", key = "#baseCurrency + #quoteCurrency", cacheManager = "rates-cache-manager")
    public Rate fetchSimpleRate(final String baseCurrency, final String quoteCurrency) {
        return restClient.get()
                .uri("/rates/{baseCurrency}/{quoteCurrency}", baseCurrency, quoteCurrency)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatusCode.valueOf(403)),
                          (request, response) -> {
                              LOGGER.error(ErrorMessages.CLIENT_ERROR_MESSAGE_403);
                              throw new RestClientException(ErrorMessages.CLIENT_ERROR_MESSAGE_403);
                          })
                .body(Rate.class);
    }
}
