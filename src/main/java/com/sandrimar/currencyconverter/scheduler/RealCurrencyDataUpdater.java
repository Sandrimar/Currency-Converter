package com.sandrimar.currencyconverter.scheduler;

import com.sandrimar.currencyconverter.services.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RealCurrencyDataUpdater {

    @Autowired
    private CurrencyService service;

    @Scheduled(cron = "5 0 0 * * *", zone = "UTC")
    public void updateData() {
        service.updateRealCurrencies();
    }
}
