package org.example.currencyserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Currency(String code, String name,
                       @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) boolean active) {

    public static class CurrencyValidator { // todo: check to make fit predicate
        public static boolean test(Currency currency) {
            return currency.code != null && !currency.code.isBlank();
        }
    }
}
