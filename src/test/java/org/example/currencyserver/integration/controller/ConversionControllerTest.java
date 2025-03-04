package org.example.currencyserver.integration.controller;

import static org.example.currencyserver.TestingConstants.EUR;
import static org.example.currencyserver.TestingConstants.USD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.util.List;

import org.example.currencyserver.common.exception.ErrorMessages;
import org.example.currencyserver.controller.ConversionController;
import org.example.currencyserver.model.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

/**
 * Integration tests with the controller method calls against the Swap server.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ConversionControllerTest {

    @Autowired
    private ConversionController conversionController;

    @Test
    public void testGetAvailableCurrencies() {
        List<Currency> availableCurrencies = conversionController.getAvailableCurrencies();

        assumeFalse(availableCurrencies.isEmpty());
        assertEquals(3, availableCurrencies.get(0).code().length());
    }

    @Test
    public void testConvertion() {
        double convertedAmount = conversionController.convert(EUR, USD, 100d);
        assertTrue(convertedAmount > 0);
        convertedAmount = conversionController.convert(EUR, USD, -100d);
        assertTrue(convertedAmount < 0);
    }

    @Test
    public void testConvertion_negativeAmount() {
        double convertedAmount = conversionController.convert(EUR, USD, 100d);
        assertTrue(convertedAmount > 0);
        convertedAmount = conversionController.convert(EUR, USD, -100d);
        assertTrue(convertedAmount < 0);
    }

    @ParameterizedTest
    @ValueSource(strings = { "Euro", "E", "  ", "Dollar" })
    public void testConvertion_invalidBaseCurrency_validStringValue(final String baseCurrency) {
        RestClientException restClientException =
                assertThrows(RestClientException.class, () -> conversionController.convert(baseCurrency, USD, 100d));
        assertEquals(
                ErrorMessages.CLIENT_ERROR_MESSAGE_403,
                restClientException.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testConvertion_invalidBaseCurrency_invalidStringValue(final String baseCurrency) {
        HttpClientErrorException restClientException =
                assertThrows(HttpClientErrorException.BadRequest.class,
                             () -> conversionController.convert(baseCurrency, USD, 100d));
        assertEquals(HttpStatus.BAD_REQUEST, restClientException.getStatusCode());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "Euro", "E", "  ", "Dollar" })
    public void testConvertion_invalidQuoteCurrency(final String quoteCurrency) {
        HttpClientErrorException restClientException =
                assertThrows(HttpClientErrorException.class,
                             () -> conversionController.convert(EUR, quoteCurrency, 100d));
        assertEquals(HttpStatus.BAD_REQUEST, restClientException.getStatusCode());
        assertTrue(restClientException.getMessage().contains("Not a valid ISO 4217 currency code"));
    }

}
