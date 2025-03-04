package org.example.currencyserver.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.example.currencyserver.client.SwopClient;
import org.example.currencyserver.model.Currency;
import org.example.currencyserver.model.Rate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Service class to handle the business logic between controller and Swop client.
 */
@RequiredArgsConstructor
@Service
public class ConversionService {

    private final SwopClient swopClient;

    /**
     * Filters the active and valid currencies.
     *
     * @return a list of currencies.
     */
    @Cacheable(value = "currencies", cacheManager = "currencies-cache-manager")
    public List<Currency> getAvailableCurrencies() {
        return swopClient.fetchAvailableCurrencies().stream()
                .filter(Currency::active)
                .filter(Currency.CurrencyValidator::test)
                .toList();
    }

    /**
     * Computes the amount in the target currency.
     *
     * @param base   the source currency.
     * @param quote  the target currency.
     * @param amount the amount to convert.
     * @return the converted amount.
     */
    public double convertCurrency(final String base, final String quote, final double amount) {
        Rate rate = swopClient.fetchSimpleRate(base, quote);
        return rate.quote() * amount;
    }
}
