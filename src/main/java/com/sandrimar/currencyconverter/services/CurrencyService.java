package com.sandrimar.currencyconverter.services;

import com.sandrimar.currencyconverter.dto.CurrencyDTO;
import com.sandrimar.currencyconverter.model.Currency;
import com.sandrimar.currencyconverter.repositories.CurrencyRepository;
import com.sandrimar.currencyconverter.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository repository;

    @Autowired
    private ExternalAPIService apiService;

    public Map<String, CurrencyDTO> findAvailable() {
        List<Currency> list = repository.findByAvailableTrue();
        Map<String, CurrencyDTO> map = new LinkedHashMap<>();
        for (Currency c : list) {
            map.put(c.getCode(), new CurrencyDTO(c));
        }
        return map;
    }

    public CurrencyDTO findByCode(String code) {
        Currency c = repository.findByAvailableTrueAndCode(code);
        if (c == null) {
            throw new ResourceNotFoundException(code);
        }
        return new CurrencyDTO(c);
    }
}
