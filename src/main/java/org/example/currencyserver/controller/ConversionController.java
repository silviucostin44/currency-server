package org.example.currencyserver.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.example.currencyserver.model.Currency;
import org.example.currencyserver.service.ConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST request controller class for the currency conversion.
 */
@CrossOrigin("http://localhost:3000")
@RequiredArgsConstructor
@RestController
@RequestMapping("/conversion/v1")
public class ConversionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionController.class.getSimpleName());

    private final ConversionService conversionService;

    /**
     * Maps incoming requests for retrieving all available currencies.
     *
     * @return a list of currencies.
     */
    @GetMapping("/currencies")
    public List<Currency> getAvailableCurrencies() {
        LOGGER.info("Incoming request for: getAvailableCurrencies");
        List<Currency> availableCurrencies = conversionService.getAvailableCurrencies();
        LOGGER.debug("Retrieving a list of {} available currencies", availableCurrencies.size());
        return availableCurrencies;
    }

    /**
     * Maps incoming requests for converting an amount from a currency to another.
     *
     * @param base   the currency to convert from.
     * @param quote  the currency to convert to.
     * @param amount the amount to convert, expressed in currency.
     * @return a numeric value as the converted amount.
     */
    @GetMapping("/convert/{base}/{quote}")
    public double convert(@PathVariable() String base,
                          @PathVariable String quote,
                          @RequestParam() double amount) {  // todo: validate amount isn't too big
        LOGGER.info("Incoming request for: convert {} to {}", base, quote);
        double computedAmount = conversionService.convertCurrency(base, quote, amount);
        LOGGER.debug("Computed conversion from {}{} to {}{}", amount, base, computedAmount, quote);
        return computedAmount;
    }
}
