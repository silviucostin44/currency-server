package org.example.currencyserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Currency model class to handle currency data from Swop server.
 *
 * @param code   the currency code composed of three capital letters.
 * @param name   the currency name.
 * @param active tells if the currency is still active or it is a historical one.
 */
public record Currency(String code, String name,
                       @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) boolean active) {

    public static class CurrencyValidator { // todo: check to make fit predicate
        public static boolean test(Currency currency) {
            return currency.code != null && !currency.code.isBlank();
        }
    }
}
