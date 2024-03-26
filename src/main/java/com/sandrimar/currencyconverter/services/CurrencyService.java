package com.sandrimar.currencyconverter.services;

import com.sandrimar.currencyconverter.dto.ConversionResultDTO;
import com.sandrimar.currencyconverter.dto.CurrencyDTO;
import com.sandrimar.currencyconverter.model.Currency;
import com.sandrimar.currencyconverter.repositories.CurrencyRepository;
import com.sandrimar.currencyconverter.services.exceptions.BadRequestException;
import com.sandrimar.currencyconverter.services.exceptions.BusinessException;
import com.sandrimar.currencyconverter.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashMap;
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
        if (dto.getValue() == null || new BigDecimal(dto.getValue()).equals(BigDecimal.ZERO)) {
            throw new BusinessException("O valor não pode ser 0 ou nulo");
        }
        try {
            findAnyByCode(dto.getCode());
            throw new BusinessException("Essa moeda já existe");
        } catch (ResourceNotFoundException e) {
            Currency c = new Currency(dto.getCode(), new BigDecimal(dto.getValue()), Instant.now(), true);
            repository.save(c);
            return new CurrencyDTO(c);
        }
    }

    private Currency findAnyByCode(String code) {
        if (code == null || code.isEmpty()) {
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

    public CurrencyDTO updateValue(String code, CurrencyDTO dto) {
        for (String real : apiService.getRealCurrencies()) {
            if (code.toUpperCase().equals(real)) {
                throw new BusinessException("Não é permitido alterar uma moeda real");
            }
        }
        if (dto.getValue() == null || new BigDecimal(dto.getValue()).equals(BigDecimal.ZERO)) {
            throw new BusinessException("O valor deve ser maior que 0");
        }

        Currency update = findAnyByCode(code.toUpperCase());
        update.setValue(new BigDecimal(dto.getValue()));
        update.setLastUpdate(Instant.now());
        update.setAvailable(true);
        repository.save(update);
        return new CurrencyDTO(update);
    }

    public void delete(String code) {
        for (String real : apiService.getRealCurrencies()) {
            if (code.toUpperCase().equals(real)) {
                throw new BusinessException("Não é permitido apagar uma moeda real");
            }
        }
        findAnyByCode(code.toUpperCase());
        repository.deleteById(code.toUpperCase());
    }

    public void updateRealCurrencies() {
        List<String> realCurrenciesCode = apiService.getRealCurrencies();
        List<Currency> realCurrencies = repository.findByCodeIn(realCurrenciesCode);
        List<Currency> newData = apiService.getData();

        Map<String, Currency> oldCurrencies = new HashMap<>();
        for (Currency old : realCurrencies) {
            oldCurrencies.put(old.getCode(), old);
        }

        for (Currency newCurrency : newData) {
            Currency update = oldCurrencies.get(newCurrency.getCode());
            update.setValue(newCurrency.getValue());
            update.setLastUpdate(newCurrency.getLastUpdate());
        }
        repository.saveAll(oldCurrencies.values());
    }

    public ConversionResultDTO convert(String fromCurrency, String toCurrency, String amount) {
        Currency from = findAvailableCurrencyByCode(fromCurrency);
        Currency to = findAvailableCurrencyByCode(toCurrency);
        double doubleAmount;
        try {
            doubleAmount = Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Parâmetros inválidos na requisição");
        }
        if (doubleAmount < 0) {
            throw new BusinessException("A quantidade não pode ser negativa");
        }

        MathContext mc = new MathContext(10, RoundingMode.HALF_UP);
        BigDecimal result = new BigDecimal(amount, mc).divide(from.getValue(), mc).multiply(to.getValue());
        return new ConversionResultDTO(from.getCode(), to.getCode(), doubleAmount, result.stripTrailingZeros().toPlainString(), Instant.now());
    }

    private Currency findAvailableCurrencyByCode(String code) {
        if (code.isEmpty()) {
            throw new BadRequestException("Parâmetros inválidos na requisição");
        }
        Currency c = repository.findByAvailableTrueAndCode(code);
        if (c == null) {
            throw new ResourceNotFoundException(code);
        }
        return c;
    }
}
