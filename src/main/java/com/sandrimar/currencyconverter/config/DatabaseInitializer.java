package com.sandrimar.currencyconverter.config;

import com.sandrimar.currencyconverter.model.Currency;
import com.sandrimar.currencyconverter.repositories.CurrencyRepository;
import com.sandrimar.currencyconverter.services.ExternalAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private CurrencyRepository repository;
    @Autowired
    private ExternalAPIService apiService;

    @Override
    public void run(String... args) throws Exception {
        List<Currency> currencies = apiService.getData();
        for (Currency c : currencies) {
            if (c.getCode().equals("BRL") || c.getCode().equals("BTC") ||
                    c.getCode().equals("ETH") || c.getCode().equals("EUR") || c.getCode().equals("USD")) {
                c.setAvailable(true);
            }
        }
        repository.saveAll(currencies);
    }
}
