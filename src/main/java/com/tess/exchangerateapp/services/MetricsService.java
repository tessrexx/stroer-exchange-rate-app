package com.tess.exchangerateapp.services;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Tracks API usage metrics including request counts, response counts, and total
 * successful queries.
 */
@Service
public class MetricsService {
    private final Map<String, ApiStats> stats = new ConcurrentHashMap<>();
    private final AtomicInteger totalQueries = new AtomicInteger();

    /**
     * Records an API request attempt.
     * Creates new stats entry if API hasn't been called before.
     */
    public void recordRequest(String api) {
        stats.computeIfAbsent(api, _ -> new ApiStats()).totalRequests++;
    }

    /**
     * Records a successful API response.
     * Increments both API-specific and total response counters.
     */
    public void recordResponse(String api) {
        stats.get(api).totalResponses++;
        totalQueries.incrementAndGet();
    }

    /**
     * Returns current metrics for all APIs.
     * 
     * @return Map with total queries and per-API metrics
     *         Example: {
     *         "totalQueries": 30, "apis": [{ "name": "fawazApi", "metrics": {
     *         "totalRequests": 30, "totalResponses": 30}}]}
     */
    public Map<String, Object> getMetrics() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalQueries", totalQueries.get());

        List<Map<String, Object>> apiList = stats.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> apiMap = new LinkedHashMap<>();
                    apiMap.put("name", entry.getKey());

                    Map<String, Integer> metricsMap = new LinkedHashMap<>();
                    metricsMap.put("totalRequests", entry.getValue().totalRequests);
                    metricsMap.put("totalResponses", entry.getValue().totalResponses);

                    apiMap.put("metrics", metricsMap);
                    return apiMap;
                })
                .toList();

        result.put("apis", apiList);
        return result;
    }

    /**
     * Holds request and response counts for a single API.
     */
    public static class ApiStats {
        private int totalRequests = 0;
        private int totalResponses = 0;

        public int getTotalRequests() {
            return totalRequests;
        }

        public int getTotalResponses() {
            return totalResponses;
        }
    }
}
