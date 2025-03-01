package org.example.currencyserver.model;

public record Rate(String baseCurrency, String quoteCurrency, double quote) {

}
