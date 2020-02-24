package com.in28minutes.microservices.currencyconversionservice;

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
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);

        ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversionBean.class, uriVariables);

        CurrencyConversionBean response = responseEntity.getBody();

        // quantity.multiply(response.getConversionMultiple()) => totalCalculatedAmount = quantity * conversionMultiple;
        return new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(), quantity, quantity.multiply(response.getConversionMultiple()), response.getPort());
    }
}
