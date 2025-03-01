package org.example.currencyserver;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class TestingConstants {

    public static final String EUR = "EUR";
    public static final String USD = "USD";

    private static UriComponents swopConvertionUri; // todo: remove if not needed

    private TestingConstants() {
    }

    public static UriComponents convertUri() {
        if (swopConvertionUri == null) {
            swopConvertionUri = UriComponentsBuilder.fromUriString("/conversion/convert/{base}/{quote}")
                    .queryParam("amount", "{amount}")
                    .encode()
                    .build();
        }
        return swopConvertionUri;
    }
}
