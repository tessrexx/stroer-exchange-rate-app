package com.tess.exchangerateapp.services;

import java.util.List;
import java.util.Map;

public interface ExchangeApiService {
    /**
     * Returns the name of the exchange rate API service
     */
    String getName();

    /**
     * Fetches exchange rates for the given base currency and target symbols
     * 
     * @param base    the base currency code (e.g. "EUR")
     * @param symbols list of target currency codes to get rates for (e.g. ["USD",
     *                "NZD"])
     * @return a map of currency codes to their exchange rates relative to the base
     *         currency
     */
    Map<String, Double> getRates(String base, List<String> symbols);
}