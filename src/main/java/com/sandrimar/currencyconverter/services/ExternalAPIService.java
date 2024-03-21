package com.sandrimar.currencyconverter.services;

import com.sandrimar.currencyconverter.dto.externalapi.APIResponse;
import com.sandrimar.currencyconverter.dto.externalapi.CurrencyResponse;
import com.sandrimar.currencyconverter.dto.externalapi.MetaResponse;
import com.sandrimar.currencyconverter.model.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ExternalAPIService {

    @Autowired
    private RestTemplate restTemplate;
    private final String url = "https://api.currencyapi.com/v3/latest?apikey=cur_live_fXeSzfwHhEh3MGNHd8P4rnMewxmCB0E7tIWQDc0z";

    private List<String> realCurrencies = new ArrayList<>();

    public List<Currency> getData() {
        ResponseEntity<APIResponse> responseEntity = restTemplate.getForEntity(url, APIResponse.class);
        APIResponse body = responseEntity.getBody();

        List<Currency> list = new ArrayList<>();
        for (Map.Entry<String, CurrencyResponse> data : body.getData().entrySet()) {
            list.add(convert(data, body.getMeta()));
            realCurrencies.add(data.getKey());
        }
        return list;
    }

    private Currency convert(Map.Entry<String, CurrencyResponse> data, MetaResponse meta) {
        return new Currency(data.getKey(), new BigDecimal(data.getValue().getValue()), meta.getLastUpdate());
    }

    public List<String> getRealCurrencies() {
        return realCurrencies;
    }
}
