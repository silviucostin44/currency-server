package org.example.currencyserver.configuration;

import java.io.IOException;

import org.example.currencyserver.integration.SwopClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class SwopRestClientBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwopClient.class.getSimpleName());

    private static final String AUTH_HEADER = "Authorization";
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
                .defaultHeader(AUTH_HEADER, AUTH_TYPE + " " + apiKey);
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
