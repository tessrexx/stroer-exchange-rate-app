package com.tess.exchangerateapp.controllers;

import com.tess.exchangerateapp.services.MetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for MetricsController endpoints.
 * Uses @WebMvcTest to test only the web layer, mocking the MetricsService.
 * 
 * Test Structure
 * Arrange:
 * Set up mock behavior and test data (e.g. when(metricsService.getMetrics()))
 * Act:
 * Perform the HTTP request using MockMvc (e.g. mockMvc.perform(get()))
 * Assert:
 * Verify response status and content (e.g. andExpect(status().isOk()))
 */
@WebMvcTest(MetricsController.class)
class MetricsControllerTest {

    @MockBean
    private MetricsService metricsService;

    @Autowired
    private MockMvc mockMvc;

    /**
     * Reset mock behavior before each test to ensure clean state.
     * Sets up empty metrics as default response.
     */
    @BeforeEach
    void setUp() {
        Map<String, Object> emptyMetrics = new HashMap<>();
        emptyMetrics.put("totalQueries", 0);
        emptyMetrics.put("apis", new HashMap<>());
        when(metricsService.getMetrics()).thenReturn(emptyMetrics);
    }

    /**
     * Test successful metrics retrieval with multiple APIs.
     * Verifies correct structure and values for total queries and per-API stats.
     */
    @Test
    void getMetrics_Success() throws Exception {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalQueries", 42);

        Map<String, Object> apis = new HashMap<>();
        Map<String, Integer> fawazStats = new HashMap<>();
        fawazStats.put("requests", 45);
        fawazStats.put("responses", 42);
        apis.put("fawaz", fawazStats);

        Map<String, Integer> frankfurterStats = new HashMap<>();
        frankfurterStats.put("requests", 3);
        frankfurterStats.put("responses", 3);
        apis.put("frankfurter", frankfurterStats);

        metrics.put("apis", apis);

        when(metricsService.getMetrics()).thenReturn(metrics);

        mockMvc.perform(get("/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalQueries").value(42))
                .andExpect(jsonPath("$.apis.fawaz.requests").value(45))
                .andExpect(jsonPath("$.apis.fawaz.responses").value(42))
                .andExpect(jsonPath("$.apis.frankfurter.requests").value(3))
                .andExpect(jsonPath("$.apis.frankfurter.responses").value(3));
    }

    /**
     * Test metrics retrieval when no API calls have been made.
     * Verifies empty metrics structure with zero counts.
     */
    @Test
    void getMetrics_EmptyMetrics() throws Exception {
        Map<String, Object> emptyMetrics = new HashMap<>();
        emptyMetrics.put("totalQueries", 0);
        emptyMetrics.put("apis", new HashMap<>());

        when(metricsService.getMetrics()).thenReturn(emptyMetrics);

        mockMvc.perform(get("/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalQueries").value(0))
                .andExpect(jsonPath("$.apis").isEmpty());
    }

    /**
     * Test handling of service errors.
     * Verifies 500 Internal Server Error response with error message.
     */
    @Test
    void getMetrics_ServiceError() throws Exception {
        when(metricsService.getMetrics())
                .thenThrow(new RuntimeException("Unexpected error in metrics service"));

        mockMvc.perform(get("/metrics"))
                .andExpect(status().isInternalServerError())
                .andExpect(
                        jsonPath("$.error").value("Failed to retrieve metrics: Unexpected error in metrics service"));
    }
}