package org.example.currencyserver.integration.controller;

import static org.example.currencyserver.TestingConstants.EUR;
import static org.example.currencyserver.TestingConstants.USD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.net.URI;
import java.util.Objects;

import org.example.currencyserver.common.exception.ErrorMessages;
import org.example.currencyserver.model.Currency;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Integration tests with actual REST requests against the Swap server.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConversionControllerMappingTest {

    private static UriComponents convertUri;

    @Autowired
    TestRestTemplate restTemplate;

    @BeforeAll
    static void setUpBeforeClass() {
        convertUri = UriComponentsBuilder.fromUriString("/conversion/v1/convert/{base}/{quote}")
                .queryParam("amount", "{amount}")
                .encode()
                .build();
    }

    @Test
    public void testGetAvailableCurrencies() {
        ResponseEntity<Currency[]> response =
                restTemplate.getForEntity("/conversion/v1/currencies", Currency[].class);

        assertSame(HttpStatus.OK, response.getStatusCode());

        Currency[] body = Objects.requireNonNull(response.getBody());
        assumeTrue(body.length > 0);
        assertEquals(3, response.getBody()[0].code().length());
    }

    @Test
    public void testConvertion() {
        URI uri = convertUri.expand(EUR, USD, 100d).toUri();
        ResponseEntity<Double> response = restTemplate.getForEntity(uri, double.class);

        assertSame(HttpStatus.OK, response.getStatusCode());

        double body = Objects.requireNonNull(response.getBody());
        assumeTrue(body > 0);
    }

    @ParameterizedTest
    @ValueSource(strings = { "Euro", "E", "  ", "Dollar" })
    public void testConvertion_invalidBaseCurrency(final String baseCurrency) {
        URI uri = convertUri.expand(baseCurrency, USD, 100d).toUri();
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        assertSame(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        assertEquals(
                ErrorMessages.CLIENT_ERROR_MESSAGE_403,
                response.getBody());
    }

    @ParameterizedTest
    @ValueSource(strings = { "Euro", "E", "  ", "Dollar" })
    public void testConvertion_invalidQuoteCurrency(final String quoteCurrency) {
        URI uri = convertUri.expand(EUR, quoteCurrency, 100d).toUri();
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        assertSame(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String body = Objects.requireNonNull(response.getBody());
        assertTrue(body.contains("Not a valid ISO 4217 currency code"));
    }
}
