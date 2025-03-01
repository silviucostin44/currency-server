package org.example.currencyserver;

import static org.example.currencyserver.TestingConstants.EUR;
import static org.example.currencyserver.TestingConstants.USD;

import java.util.List;

import org.example.currencyserver.model.Currency;

public class TestDummyDataUtil {

    public static List<Currency> createDummyCurrencyList() {
        return List.of(
                new Currency(EUR, "", true),
                new Currency("ZON", "", false),
                new Currency(USD, "", true)
        );
    }

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
