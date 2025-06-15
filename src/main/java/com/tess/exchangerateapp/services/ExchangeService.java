package com.tess.exchangerateapp.services;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;

/**
 * Service that orchestrates exchange rate fetching from multiple APIs.
 * Aggregates results, handles caching, and records metrics for API usage.
 */
@Service
public class ExchangeService {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeService.class);
    private final List<ExchangeApiService> apis;
    private final Map<String, Map<String, Double>> cache = new ConcurrentHashMap<>();
    private final MetricsService metrics;

    /**
     * Constructs the exchange service with available APIs and metrics service.
     * 
     * @param apis    List of exchange rate API implementations
     * @param metrics Service for recording API usage metrics
     */
    public ExchangeService(List<ExchangeApiService> apis, MetricsService metrics) {
        this.apis = apis;
        this.metrics = metrics;
    }

    /**
     * Gets exchange rates for the specified currencies.
     * Attempts to fetch from cache first, then queries all APIs if needed.
     * Results are averaged across successful API responses.
     *
     * @param base    Base currency code (e.g., "EUR")
     * @param symbols List of target currency codes (e.g., ["USD", "NZD"])
     * @return Map of currency codes to their exchange rates, or empty map if no
     *         results
     */
    public Map<String, Double> getRates(String base, List<String> symbols) {
        String key = base + ":" + String.join(",", symbols);
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        List<Map<String, Double>> results = apis.stream().map(api -> {
            try {
                metrics.recordRequest(api.getName());
                Map<String, Double> response = api.getRates(base, symbols);

                if (!response.isEmpty()) {
                    metrics.recordResponse(api.getName());
                }

                return response;
            } catch (Exception error) {
                logger.error("Error calling {}: {}", api.getName(), error.getMessage(), error);
                return Map.<String, Double>of();
            }
        }).filter(rates -> !rates.isEmpty()).toList();

        if (results.isEmpty()) {
            logger.warn("No results obtained from any API for base={}, symbols={}", base, symbols);
            return Map.of();
        }

        Map<String, Double> averaged = averageRates(results);
        cache.put(key, averaged);
        return averaged;
    }

    /**
     * Averages exchange rates from multiple API responses.
     * 
     * @param results List of rate maps from different APIs
     * @return Map of currency codes to averaged exchange rates
     */
    private Map<String, Double> averageRates(List<Map<String, Double>> results) {
        Map<String, Double> average = new HashMap<>();
        for (String symbol : results.get(0).keySet()) {
            double sum = results.stream().mapToDouble(result -> result.get(symbol)).sum();
            average.put(symbol, sum / results.size());
        }
        return average;
    }
}
