package com.in28minutes.microservices.currencyconversionservice;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Create a proxy for Feign REST client
// Name of the client is the spring.application.name of another Spring Boot project. It will be important to naming server and ribbon server
// Url is the http address of the client app when it's alive (Removed for Ribbon setup)
// @FeignClient(name = "currency-exchange-service") // Commented due to change call to Zuul API Gateway server(netflix-zuul-api-gateway-server)
@FeignClient(name = "netflix-zuul-api-gateway-server")
// Enable @RibbonClient to make it load balanced, tell the name
@RibbonClient(name = "currency-exchange-service")
public interface CurrencyExchangeServiceProxy {
    // Take the target's Spring Boot project's Controller method name with mapping
    // CurrencyConversionBean becomes the output since it is similar to the output of ExchangeValue object
    // You may need to specify in @PathVariable, or else you may come through an error: IllegalStateException: PathVariable annotation was empty on param 0.
    // You need to append currency-exchange-service into the mappings of your proxy urls
    @GetMapping("/currency-exchange-service/currency-exchange/from/{from}/to/{to}")
    public CurrencyConversionBean retrieveExchangeValue(@PathVariable("from") String from, @PathVariable("to") String to);
}

// After point the proxy url from currency-exchange-service directly to eureka naming server,
// and point feign to netflix-zuul-api-gateway-server API gateway, all same request logs will be shown in netflix-zuul-api-gateway-server project

// This one is go to currency-conversion-service directly,
// but the method calls proxy which is this file,
// then this file will use Feign to go to Eureka API gateway,
// and Ribbon will find currency-exchange-service,
// then reach currency-exchange-service to find that method.
// http://localhost:8100/currency-converter-feign/from/USD/to/INR/quantity/564 (Same old URL, but pointed differently)

// This one is go through Eureka API gateway,
// then find the name of the service you mentioned (currency-exchange-service),
// then reach the URL of the endpoint.
// http://localhost:8765/currency-exchange-service/currency-exchange/from/AUD/to/INR

// So based on my thinking, if I call the following request below,
// It will go to server find currency-conversion-service (Trigger API gateway once)
// Then go into currency-conversion-service and find currency-converter-feign
// And within that method call the proxy, proxy will go through API gateway (Trigger API gateway twice)
// And call the currency-exchange-service. (And it worked)
// http://localhost:8765/currency-conversion-service/currency-converter-feign/from/USD/to/INR/quantity/564
