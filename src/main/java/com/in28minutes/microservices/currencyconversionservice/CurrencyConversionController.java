package com.in28minutes.microservices.currencyconversionservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CurrencyConversionController {

    private CurrencyExchangeServiceProxy currencyExchangeServiceProxy;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CurrencyConversionController(CurrencyExchangeServiceProxy currencyExchangeServiceProxy) {
        this.currencyExchangeServiceProxy = currencyExchangeServiceProxy;
    }

    // http://localhost:8100/currency-converter/from/USD/to/INR/quantity/1000
    @GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean convertCurrency(@PathVariable String from,
                                                  @PathVariable String to,
                                                  @PathVariable BigDecimal quantity) {
        // String from, String to, BigDecimal conversionMultiple, BigDecimal quantity, BigDecimal totalCalculatedAmount, int port

        // The REST Template is used to call other microservice functions
        // microservice calls microservice
        // Now is Currency Conversion Service microservice calling Currency Exchange Service microservice bean
        // The return type we will have to create a bean for it.
        // But as we can see, CurrencyConversionBean is similar to Exchange Value in currency-exchange-service project.
        // So we use CurrencyConversionBean here as response.
        // You can put variables in the URL, create variables' value in Map and put it
        // But problem is, we still need to write a lot of code just to call other services (like this method)
        // So, to make it simpler, we use Feign REST client
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);

        ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversionBean.class, uriVariables);

        CurrencyConversionBean response = responseEntity.getBody();

        // quantity.multiply(response.getConversionMultiple()) => totalCalculatedAmount = quantity * conversionMultiple;
        return new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(), quantity, quantity.multiply(response.getConversionMultiple()), response.getPort());
    }

    // Example of using Feign Client
    // http://localhost:8100/currency-converter-feign/from/USD/to/INR/quantity/1000
    // http://localhost:8100/currency-converter-feign/from/EUR/to/INR/quantity/1000
    // http://localhost:8100/currency-converter-feign/from/AUD/to/INR/quantity/1000
    @GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean convertCurrencyFeign(@PathVariable String from,
                                                       @PathVariable String to,
                                                       @PathVariable BigDecimal quantity) {
        CurrencyConversionBean response = currencyExchangeServiceProxy.retrieveExchangeValue(from, to);
        logger.info("{}", response);
        return new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(), quantity, quantity.multiply(response.getConversionMultiple()), response.getPort());
    }
}
