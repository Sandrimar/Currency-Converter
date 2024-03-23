package com.sandrimar.currencyconverter.services;

import com.sandrimar.currencyconverter.dto.CurrencyDTO;
import com.sandrimar.currencyconverter.model.Currency;
import com.sandrimar.currencyconverter.repositories.CurrencyRepository;
import com.sandrimar.currencyconverter.services.exceptions.BusinessException;
import com.sandrimar.currencyconverter.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
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

    public CurrencyDTO insert(CurrencyDTO dto) {
        if (dto.getValue() == null || dto.getValue().equals(BigDecimal.ZERO)) {
            throw new BusinessException("O valor não pode ser 0 ou nulo");
        }
        try {
            findAnyByCode(dto.getCode());
            throw new BusinessException("Essa moeda já existe");
        } catch (ResourceNotFoundException e) {
            Currency c = new Currency(dto.getCode(), dto.getValue(), Instant.now(), true);
            repository.save(c);
            return new CurrencyDTO(c);
        }
    }

    protected Currency findAnyByCode(String code) {
        if (code.isEmpty()) {
            throw new BusinessException("A moeda precisa ter um código");
        }
        return repository.findById(code).orElseThrow(() -> new ResourceNotFoundException(code));
    }

    public CurrencyDTO setAvailability(CurrencyDTO dto) {
        Currency update = findAnyByCode(dto.getCode());
        update.setAvailable(dto.isAvailable());
        repository.save(update);
        if (!update.isAvailable()) {
            return null;
        }
        return new CurrencyDTO(update);
    }
}
