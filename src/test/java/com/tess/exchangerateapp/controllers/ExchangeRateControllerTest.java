package com.tess.exchangerateapp.controllers;

import com.tess.exchangerateapp.services.ExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for ExchangeRateController endpoints.
 * Uses @WebMvcTest to test only the web layer, mocking the ExchangeService.
 * 
 * Test Structure
 * Arrange:
 * Set up mock behavior and test data (e.g. when(exchangeService.getRates()))
 * Act:
 * Perform the HTTP request using MockMvc (e.g. mockMvc.perform(get()))
 * Assert:
 * Verify response status and content (e.g. andExpect(status().isOk()))
 */
@WebMvcTest(ExchangeRateController.class)
class ExchangeRateControllerTest {
    @MockBean
    private ExchangeService exchangeService;

    @Autowired
    private MockMvc mockMvc;

    /**
     * Reset mock behavior before each test to ensure clean state
     */
    @BeforeEach
    void setUp() {
        when(exchangeService.getRates(any(), anyList())).thenReturn(new HashMap<>());
    }

    /**
     * Test successful exchange rate retrieval
     * Verifies correct response structure and values
     */
    @Test
    void getRates_Success() throws Exception {
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.0856);
        rates.put("NZD", 1.7856);
        when(exchangeService.getRates(eq("EUR"), anyList())).thenReturn(rates);

        mockMvc.perform(get("/exchangeRates/EUR")
                .param("symbols", "USD,NZD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.base").value("EUR"))
                .andExpect(jsonPath("$.rates.USD").value(1.0856))
                .andExpect(jsonPath("$.rates.NZD").value(1.7856));
    }

    /**
     * Test handling of invalid base currency
     * Verifies 400 Bad Request response
     */
    @Test
    void getRates_InvalidBaseCurrency() throws Exception {
        when(exchangeService.getRates(eq("INVALID"), anyList()))
                .thenThrow(new IllegalArgumentException("Invalid base currency"));

        mockMvc.perform(get("/exchangeRates/INVALID")
                .param("symbols", "USD"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    /**
     * Test handling of invalid target currency
     * Verifies 400 Bad Request response
     */
    @Test
    void getRates_InvalidTargetCurrency() throws Exception {
        when(exchangeService.getRates(eq("EUR"), anyList()))
                .thenThrow(new IllegalArgumentException("Invalid target currency"));

        mockMvc.perform(get("/exchangeRates/EUR")
                .param("symbols", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    /**
     * Test handling of service unavailability
     * Verifies 503 Service Unavailable response
     */
    @Test
    void getRates_NoApisAvailable() throws Exception {
        when(exchangeService.getRates(eq("EUR"), anyList()))
                .thenThrow(new RuntimeException("No APIs available"));

        mockMvc.perform(get("/exchangeRates/EUR")
                .param("symbols", "USD"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").exists());
    }

    /**
     * Test handling of missing required parameters
     * Verifies 400 Bad Request response
     */
    @Test
    void getRates_MissingParameters() throws Exception {
        mockMvc.perform(get("/exchangeRates/EUR"))
                .andExpect(status().isBadRequest());
    }
}