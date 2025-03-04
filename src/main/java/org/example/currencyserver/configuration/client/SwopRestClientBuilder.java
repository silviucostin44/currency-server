package org.example.currencyserver.configuration.client;

import java.io.IOException;

import org.apache.hc.core5.http.HttpHeaders;
import org.example.currencyserver.client.SwopClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Configuration class for the RestClient builder bean.
 */
@Configuration
public class SwopRestClientBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwopClient.class.getSimpleName());

    private static final String AUTH_TYPE = "ApiKey";

    @Value("${swop.url}")
    private String baseUrl;

    @Value("${swop.apiKey}")
    private String apiKey;

    @Bean
    public RestClient.Builder restClient() {
        return RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .requestInterceptor(((request, body, execution) -> {
                    logRequest(request);
                    ClientHttpResponse response = execution.execute(request, body);
                    return logResponse(request.getURI().getPath(), response);
                }))
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, AUTH_TYPE + " " + apiKey)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    }

    private void logRequest(final HttpRequest request) {
        LOGGER.debug("Request: {} {}", request.getMethod(), request.getURI());
    }

    private ClientHttpResponse logResponse(final String path, final ClientHttpResponse response) throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Response status: {}", response.getStatusCode());
        } else {
            LOGGER.info("Request to {} completed with status: {}", path, response.getStatusCode());
        }
        return response;
    }
}
