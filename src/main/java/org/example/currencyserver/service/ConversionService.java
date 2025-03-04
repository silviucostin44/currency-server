package org.example.currencyserver.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.example.currencyserver.client.SwopClient;
import org.example.currencyserver.model.Currency;
import org.example.currencyserver.model.Rate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ConversionService {

    private final SwopClient swopClient;

    @Cacheable(value = "currencies", cacheManager = "currencies-cache-manager")
    public List<Currency> getAvailableCurrencies() {
        return swopClient.fetchAvailableCurrencies().stream()
                .filter(Currency::active)
                .filter(Currency.CurrencyValidator::test)
                .toList();
    }

    public double convertCurrency(final String base, final String quote, final double amount) {
        Rate rate = swopClient.fetchSimpleRate(base, quote);
        return rate.quote() * amount;
    }
}
