## 🚀 Developer Notes
Hi Ströer! I'm Tess 👋 Here's a brief summary of what I've implemented for the Exchange Rate App challenge. For further details, please check the closed pull requests as I've documented any changes made, how to test, and screenshots. 

#### 🌍 Exchange Rate Service & Metrics
* Implemented two exchange rate service integrations:
  - Fawaz API with built-in fallback logic
  - Frankfurter API

* Introduced caching of previously seen queries (same base + symbols) to optimise response times and API usage.

* Added a custom MetricsService that tracks:
  - Total queries
  - Request/response counts per API

* REST Endpoints:
  - /exchangeRates/{base}?symbols={SYM1,SYM2...} → returns average rates
  - /metrics → returns current metrics summary

#### ✅ Error Handling & Testing
* Implemented exception handling with clear error messages for:
  - Invalid base or target currencies
  - Missing query parameters
  - API failures or unavailability

* Added unit test coverage for both controllers:
  - Valid response handling
  - Missing/invalid input scenarios
  - Service layer failures
  - Edge cases (e.g. empty metrics)

All 9 tests pass successfully via mvn test.

#### 💡 Improvements & Next Steps
* User Interface: I'd love to build a lightweight frontend to make querying currencies and viewing metrics more user-friendly.
* Move away from @MockBean: I trialled other solutions but I couldn't quite get them to a working state, so I reverted to using MockBean. In the future, with a little more time and knowledge, it would be worth exploring other alternatives.

The following is the original task outline. Please let me know if you have any further questions! 🚀

# Stroer Labs Coding Challenge

## Task
We would like you to write a small application that can fetch exchange rates from different APIs. That application should provide a user with an endpoint where they can specify the base currency and the currencies they wish to see the exchange rates for.
`/exchangeRates/{baseCur}?symbols={SYM1,SYM2...}`
e.g.
`/exchangeRates/EUR?symbols=USD,NZD`

The response should include the base currency and the converted rates.

{

  "base": "EUR",
  
  "rates": {
  
    "USD": 1.078588,
    
    "NZD": 1.599893
    
  }
  
}

## APIs
The API's to query for the data are:


Free Currency Rates API
https://github.com/fawazahmed0/exchange-api
https://www.frankfurter.app/docs/


The application should query all APIs in and return the average of the results. The results for a given query should be stored in a cache. For requests matching an earlier query (same combination of base currency and requested symbols), the result from the cache should be returned.
We also want to record metrics for the exchange rate APIs request and response counts, and any other metrics you think are relevant. These metrics should be exposed on an API endpoint:`/metrics`


For the metrics collection, feel free to use either a common metrics library or write your own code to handle this.


Should you opt for writing your own metrics collection, the exposed metrics could look like this:

{

  "totalQueries": 30,
  
  "apis": [{
  
    "name": "freeCurrencyRates",
    
    "metrics": {
    
      "totalRequests": 30,
      
      "totalResponses": 30
      
    }
    
  }]
  
}

## Deliverables

An application that provides the functionality described above. The application can be written in Kotlin or Java.

A README.md that describes your approach and rationale behind your design and possible improvements you see.

Include unit tests

Please submit your code via Github.
