package com.tess.exchangerateapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main entry point for the Exchange Rate application.
 * Bootstraps Spring Boot and enables caching for exchange rate results.
 */
@SpringBootApplication
@EnableCaching
public class ExchangeRateApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExchangeRateApplication.class, args);
	}

}
