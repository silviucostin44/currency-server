package org.example.currencyserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Rate model class to handle rate data from Swop server.
 *
 * @param baseCurrency  the source currency.
 * @param quoteCurrency the target currency.
 * @param quote         the quote in the relation: 1*baseCurrency=quote*quoteCurrency.
 */
public record Rate(@JsonProperty("base_currency") String baseCurrency,
                   @JsonProperty("quote_currency") String quoteCurrency, double quote) {

}
