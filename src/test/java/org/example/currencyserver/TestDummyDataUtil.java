package org.example.currencyserver;

import static org.example.currencyserver.TestingConstants.EUR;
import static org.example.currencyserver.TestingConstants.USD;

import java.util.List;

import org.example.currencyserver.model.Currency;

/**
 * Utility class that provides method to create dummy data.
 */
public class TestDummyDataUtil {

    /**
     * Creates a list of valid dummy currencies.
     *
     * @return a list of currencies.
     */
    public static List<Currency> createDummyCurrencyList() {
        return List.of(
                new Currency(EUR, "", true),
                new Currency("ZON", "", false),
                new Currency(USD, "", true)
        );
    }

    /**
     * Creates a list of invalid dummy currencies.
     *
     * @return a list of currencies.
     */
    public static List<Currency> createDummyInvalidCurrencyList() {
        return List.of(
                new Currency("", "", true),
                new Currency("     ", "", true),
                new Currency(null, "", true),
                new Currency("E", "", true),
                new Currency("Dollar", "", true)
        );
    }
}
