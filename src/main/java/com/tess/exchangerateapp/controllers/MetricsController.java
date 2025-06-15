package com.tess.exchangerateapp.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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

    /**
     * Handles unexpected errors in the metrics service.
     * Returns a 500 Internal Server Error status with a descriptive error message.
     * This is a catch-all handler for any unhandled exceptions that might occur
     * during metrics retrieval.
     *
     * @param exception The exception that occurred during metrics retrieval
     * @return ResponseEntity with 500 status and error details
     *         Example: {"error": "Failed to retrieve metrics: Unexpected error in
     *         metrics service"}
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve metrics: " + exception.getMessage()));
    }
}
