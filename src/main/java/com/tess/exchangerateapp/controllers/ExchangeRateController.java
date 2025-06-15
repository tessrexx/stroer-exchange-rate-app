package com.tess.exchangerateapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.List;
import com.tess.exchangerateapp.services.ExchangeService;

/**
 * REST Controller for exchange rate endpoints. Aggregates and averages rates
 * from multiple APIs.
 */
@RestController
public class ExchangeRateController {
    private final ExchangeService service;

    /** @param service Service for fetching and processing exchange rates */
    public ExchangeRateController(ExchangeService service) {
        this.service = service;
    }

    /**
     * Gets exchange rates for currencies relative to base currency.
     * Rates are averaged from multiple APIs and cached.
     *
     * @param base    Base currency code (e.g., "EUR")
     * @param symbols Target currency codes (e.g., ["USD", "NZD"])
     * @return Map with "base" currency and "rates" map of currency codes to
     *         exchange rates
     * 
     *         Example: GET /exchangeRates/EUR?symbols=USD,NZD
     *         Response: {"base": "EUR", "rates": {"USD":1.078588, "NZD":1.599893}}
     */
    @GetMapping("/exchangeRates/{base}")
    public Map<String, Object> getRates(@PathVariable String base, @RequestParam List<String> symbols) {
        Map<String, Double> rates = service.getRates(base.toUpperCase(), symbols);
        return Map.of("base", base.toUpperCase(), "rates", rates);
    }
}
