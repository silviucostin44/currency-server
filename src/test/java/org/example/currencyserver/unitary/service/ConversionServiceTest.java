package org.example.currencyserver.unitary.service;

import static org.example.currencyserver.TestDummyDataUtil.createDummyCurrencyList;
import static org.example.currencyserver.TestDummyDataUtil.createDummyInvalidCurrencyList;
import static org.example.currencyserver.TestingConstants.EUR;
import static org.example.currencyserver.TestingConstants.USD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.example.currencyserver.client.SwopClient;
import org.example.currencyserver.model.Currency;
import org.example.currencyserver.model.Rate;
import org.example.currencyserver.service.ConversionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConversionServiceTest {

    @InjectMocks
    private ConversionService conversionService;

    @Mock
    private SwopClient swopClient;

    @Test
    void testGetAvailableCurrencies() {
        when(swopClient.fetchAvailableCurrencies()).thenReturn(createDummyCurrencyList());

        List<Currency> availableCurrencies = conversionService.getAvailableCurrencies();

        assertEquals(2, availableCurrencies.size());
        assertEquals(EUR, availableCurrencies.get(0).code());
        assertEquals(USD, availableCurrencies.get(1).code());
    }

    @Test
    void testGetAvailableCurrencies_emptyList() {
        when(swopClient.fetchAvailableCurrencies()).thenReturn(List.of());

        List<Currency> availableCurrencies = conversionService.getAvailableCurrencies();

        assertEquals(0, availableCurrencies.size());
    }

    @Test
    void testGetAvailableCurrencies_InvalidCurrency() {
        when(swopClient.fetchAvailableCurrencies()).thenReturn(createDummyInvalidCurrencyList());

        List<Currency> availableCurrencies = conversionService.getAvailableCurrencies();

        assertEquals(2, availableCurrencies.size());
        assertEquals("E", availableCurrencies.get(0).code());
        assertEquals("Dollar", availableCurrencies.get(1).code());
    }

    @Test
    void testConvertCurrency() {
        final double quote = 1.5d;
        final double amount = 100d;
        when(swopClient.fetchSimpleRate(EUR, USD))
                .thenReturn(new Rate(EUR, USD, quote));

        final double convertedAmount = conversionService.convertCurrency(EUR, USD, amount);

        assertEquals(150, convertedAmount);
    }

    @Test
    void testConvertCurrency_negativeAmount() {
        final double quote = 1.5d;
        final double amount = -100d;
        when(swopClient.fetchSimpleRate(EUR, USD))
                .thenReturn(new Rate(EUR, USD, quote));

        final double convertedAmount = conversionService.convertCurrency(EUR, USD, amount);

        assertEquals(-150, convertedAmount);
    }

}
