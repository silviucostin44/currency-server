package org.example.currencyserver.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Component
@PropertySource("classpath:/properties/client.properties")
public class ClientProperties {

    @Value("${swop.url}")
    private String baseUrl;

    @Value("${swop.apiKey}")
    private String apiKey;
}


