package org.example.currencyserver.integration.client;

import static org.example.currencyserver.TestingConstants.EUR;
import static org.example.currencyserver.TestingConstants.USD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.example.currencyserver.client.SwopClient;
import org.example.currencyserver.model.Rate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class SwopClientTest {

    @Autowired
    private SwopClient swopClient;

    @Test
    void testFetchSimpleRate() {
        Rate rate = swopClient.fetchSimpleRate(EUR, USD);
        assertEquals(EUR, rate.baseCurrency());
        assertEquals(USD, rate.quoteCurrency());
        assertTrue(rate.quote() > 0);
    }
}
