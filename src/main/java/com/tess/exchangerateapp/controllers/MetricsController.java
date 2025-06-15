package com.tess.exchangerateapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tess.exchangerateapp.services.MetricsService;
import java.util.Map;

/**
 * REST Controller for API usage metrics. Tracks request/response counts for
 * exchange rate APIs.
 */
@RestController
public class MetricsController {
    private final MetricsService metricsService;

    /** @param metricsService Service for collecting and retrieving API metrics */
    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    /**
     * Gets current metrics for all exchange rate APIs.
     * 
     * @return Map containing total queries and per-API request/response counts
     * 
     *         Example response:
     *         {"totalQueries": 30, "apis": [{ "name": "fawazApi", "metrics":
     *         {"totalRequests": 30, "totalResponses": 30}}]}
     */
    @GetMapping("/metrics")
    public Map<String, Object> getMetrics() {
        return metricsService.getMetrics();
    }
}
