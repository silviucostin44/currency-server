package org.example.currencyserver.controller;

import java.util.List;

import org.example.currencyserver.model.Currency;
import org.example.currencyserver.service.ConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conversion")
public class ConversionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionController.class.getSimpleName());

    private final ConversionService conversionService;

    @Autowired
    public ConversionController(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @GetMapping("/currencies")
    public List<Currency> getAvailableCurrencies() {
        LOGGER.info("Incoming request for: getAvailableCurrencies");
        List<Currency> availableCurrencies = conversionService.getAvailableCurrencies();
        LOGGER.debug("Retrieving a list of {} available currencies", availableCurrencies.size());
        return availableCurrencies;
    }

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
