package com.in28minutes.microservices.currencyconversionservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Create a proxy for Feign REST client
// Name of the client is the spring.application.name of another Spring Boot project. It will be important to naming server and ribbon server
// Url is the http address of the client app when it's alive
@FeignClient(name = "currency-exchange-service", url = "localhost:8000")
public interface CurrencyExchangeServiceProxy {
    // Take the target's Spring Boot project's Controller method name with mapping
    // CurrencyConversionBean becomes the output since it is similar to the output of ExchangeValue object
    // You may need to specify in @PathVariable, or else you may come through an error: IllegalStateException: PathVariable annotation was empty on param 0.
    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public CurrencyConversionBean retrieveExchangeValue(@PathVariable("from") String from, @PathVariable("to") String to);
}
