package com.tess.exchangerateapp.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Service implementation for fetching exchange rates from the Fawaz API.
 * Primary: cdn.jsdelivr.net
 * Fallback: currency-api.pages.dev
 */
@Service
public class FawazApiService implements ExchangeApiService {
    private static final Logger logger = LoggerFactory.getLogger(FawazApiService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String PRIMARY_URL = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/%s.json";
    private static final String FALLBACK_URL = "https://currency-api.pages.dev/v1/currencies/%s.json";

    /**
     * Data transfer object that matches the Fawaz API response structure.
     * Example response: {"date": "2025-06-14", "eur": {"usd": 1.15493719, "nzd":
     * 1.92122917}}
     * 
     * @param date  The date of the exchange rates
     * @param rates Map where the key is the base currency (e.g., "eur") and the
     *              value is a map of currency pairs to rates
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record FawazResponse(String date, @JsonAnySetter Map<String, Map<String, Double>> rates) {
    }

    @Override
    public String getName() {
        return "fawazApi";
    }

    /**
     * Fetches exchange rates for the specified base currency and target symbols.
     * Attempts to fetch from primary URL first, falls back to alternative URL if
     * primary fails.
     * 
     * @param base    The base currency code (e.g., "EUR")
     * @param symbols List of target currency codes to get rates for (e.g., ["USD",
     *                "NZD"])
     * @return Map of currency codes to their exchange rates relative to the base
     *         currency
     */
    @Override
    public Map<String, Double> getRates(String base, List<String> symbols) {
        Map<String, Double> rates = new HashMap<>();
        String baseLower = base.toLowerCase();

        // Try primary URL first
        try {
            rates = fetchRates(String.format(PRIMARY_URL, baseLower), base, symbols);
            if (!rates.isEmpty()) {
                return rates;
            }
        } catch (Exception error) {
            logger.warn("Primary Fawaz API failed, trying fallback URL. Error: {}", error.getMessage());
        }

        // Try fallback URL if primary failed or returned no rates
        try {
            rates = fetchRates(String.format(FALLBACK_URL, baseLower), base, symbols);
        } catch (Exception error) {
            logger.error("Both primary and fallback Fawaz API failed for base={}, symbols={}: {}", base, symbols,
                    error.getMessage(), error);
        }

        return rates;
    }

    /**
     * Helper method to fetch rates from a specific URL.
     * 
     * @param url     The URL to fetch rates from
     * @param base    The base currency code
     * @param symbols List of target currency codes
     * @return Map of currency codes to their exchange rates
     */
    private Map<String, Double> fetchRates(String url, String base, List<String> symbols) {
        Map<String, Double> rates = new HashMap<>();

        FawazResponse response = restTemplate.exchange(url,
                HttpMethod.GET, null, new ParameterizedTypeReference<FawazResponse>() {
                }).getBody();

        if (response != null && response.rates().containsKey(base.toLowerCase())) {
            Map<String, Double> baseRates = response.rates().get(base.toLowerCase());

            for (String symbol : symbols) {
                Double rate = baseRates.get(symbol.toLowerCase());
                if (rate != null) {
                    rates.put(symbol.toUpperCase(), rate);
                }
            }
        }

        return rates;
    }
}
