package com.tess.exchangerateapp.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Service implementation for fetching exchange rates from the Frankfurter API.
 * Uses the free API at api.frankfurter.app to fetch exchange rates.
 */
@Service
public class FrankfurterApiService implements ExchangeApiService {
    private static final Logger logger = LoggerFactory.getLogger(FrankfurterApiService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String API_URL = "https://api.frankfurter.app/latest?from=%s&to=%s";

    /**
     * Data transfer object that matches the Frankfurter API response structure.
     * 
     * Example response:
     * {"base": "EUR", "date": "2025-06-14", "rates": {"USD": 1.1512, "NZD":
     * 1.9167}}
     * 
     * @param amount The amount being converted (always 1.0 in our case)
     * @param base   The base currency code
     * @param date   The date of the exchange rates
     * @param rates  Map of currency codes to their exchange rates
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record FrankfurterResponse(double amount, String base, String date, Map<String, Double> rates) {
    }

    @Override
    public String getName() {
        return "frankfurterApi";
    }

    /**
     * Fetches exchange rates for the specified base currency and target symbols.
     * 
     * @param base    The base currency code (e.g., "EUR")
     * @param symbols List of target currency codes to get rates for (e.g., ["USD",
     *                "NZD"])
     * @return Map of currency codes to their exchange rates relative to the base
     *         currency
     */
    @Override
    public Map<String, Double> getRates(String base, List<String> symbols) {
        String joinedSymbols = String.join(",", symbols).toUpperCase();
        String url = String.format(API_URL, base.toUpperCase(), joinedSymbols);

        try {
            FrankfurterResponse response = restTemplate
                    .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<FrankfurterResponse>() {
                    }).getBody();

            if (response != null && response.rates() != null) {
                return response.rates();
            }
        } catch (Exception error) {
            logger.error("Frankfurter API failed for base={}, symbols={}: {}", base, symbols, error.getMessage(),
                    error);
        }

        return new HashMap<>();
    }
}
