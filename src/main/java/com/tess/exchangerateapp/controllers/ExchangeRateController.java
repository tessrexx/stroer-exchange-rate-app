package com.tess.exchangerateapp.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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
    public Map<String, Object> getRates(@PathVariable String base,
            @RequestParam(required = true) List<String> symbols) {
        if (symbols == null || symbols.isEmpty()) {
            throw new IllegalArgumentException("Symbols parameter cannot be empty");
        }

        Map<String, Double> rates = service.getRates(base.toUpperCase(), symbols);

        if (rates.isEmpty()) {
            throw new RuntimeException("No exchange rates available");
        }
        return Map.of("base", base.toUpperCase(), "rates", rates);
    }

    /**
     * Handles validation errors and invalid input parameters.
     *
     * @param exception The validation exception containing the error message
     * @return ResponseEntity with 400 status and error details
     *         Example: {"error": "Symbols parameter cannot be empty"}
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }

    /**
     * Handles service unavailability and rate retrieval failures.
     *
     * @param exception The runtime exception containing the error message
     * @return ResponseEntity with 503 status and error details
     *         Example: {"error": "No exchange rates available"}
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("error", exception.getMessage()));
    }
}
