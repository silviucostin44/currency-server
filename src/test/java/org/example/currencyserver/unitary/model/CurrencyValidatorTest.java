package org.example.currencyserver.unitary.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.example.currencyserver.model.Currency;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CurrencyValidatorTest {

    @ParameterizedTest
    @CsvSource({ "EUR,true", "'',false", "E,true", "Dollar,true", "'    ',false" })
    void testConvertCurrency(final String currencyCode, final boolean expectedResult) {
        Currency currency = new Currency(currencyCode, "", true);

        boolean testResult = Currency.CurrencyValidator.test(currency);

        assertEquals(expectedResult, testResult);
    }
}
