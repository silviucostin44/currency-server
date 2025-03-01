package org.example.currencyserver.unitary.integration;

import static org.example.currencyserver.TestDummyDataUtil.createDummyCurrencyList;
import static org.example.currencyserver.TestingConstants.EUR;
import static org.example.currencyserver.TestingConstants.USD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withForbiddenRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.URI;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.currencyserver.integration.SwopClient;
import org.example.currencyserver.model.Currency;
import org.example.currencyserver.model.Rate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RestClientTest(SwopClient.class)
public class SwopClientTest {

    private static UriComponents convertUri;

    @Autowired
    private SwopClient swopClient;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUpBeforeClass() {
        convertUri = UriComponentsBuilder.fromUriString("/rates/{baseCurrency}/{quoteCurrency}")
                .encode()
                .build();
    }

    @Test
    public void testFetchAvailableCurrencies() throws JsonProcessingException {
        String body = objectMapper.writeValueAsString(createDummyCurrencyList());

        server.expect(requestTo("/currencies"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        List<Currency> response = swopClient.fetchAvailableCurrencies();
        server.verify();
        assertEquals(3, response.size());
    }

    @Test
    public void testFetchSimpleRate() throws JsonProcessingException {
        URI uri = convertUri.expand(EUR, USD).toUri();
        String body = objectMapper.writeValueAsString(new Rate(EUR, USD, 100d));

        server.expect(requestTo(uri))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        Rate response = swopClient.fetchSimpleRate(EUR, USD);
        server.verify();
        assertEquals(EUR, response.baseCurrency());
        assertEquals(USD, response.quoteCurrency());
        assertTrue(response.quote() > 0);
    }

    @Test
    public void testFetchSimpleRate_errorStatus403() throws JsonProcessingException {
        final String Euro = "Euro";
        URI uri = convertUri.expand(Euro, USD).toUri();

        server.expect(requestTo(uri))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withForbiddenRequest());

        RestClientException restClientException =
                assertThrows(RestClientException.class, () -> swopClient.fetchSimpleRate(Euro, USD));
        server.verify();
        assertEquals(
                "Request to SWOP for simple rate failed with 403 error status. Please check the format of the currencies or the ApiKey",
                restClientException.getMessage());
    }

}
