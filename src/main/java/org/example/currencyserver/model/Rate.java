package org.example.currencyserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Rate(@JsonProperty("base_currency") String baseCurrency,
                   @JsonProperty("quote_currency") String quoteCurrency, double quote) {

}
