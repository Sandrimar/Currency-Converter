package com.sandrimar.currencyconverter.services;

import com.sandrimar.currencyconverter.dto.CurrencyDTO;
import com.sandrimar.currencyconverter.model.Currency;
import com.sandrimar.currencyconverter.repositories.CurrencyRepository;
import com.sandrimar.currencyconverter.services.exceptions.BusinessException;
import com.sandrimar.currencyconverter.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyServiceTest {

    @Mock
    CurrencyRepository repository;

    @Mock
    ExternalAPIService apiService;

    @InjectMocks
    CurrencyService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return all data successfully")
    void findAvailable() {
        Currency usd = new Currency("USD", BigDecimal.ONE, Instant.now());
        Currency brl = new Currency("BRL", BigDecimal.TWO, Instant.now());
        List<Currency> currencies = Arrays.asList(usd, brl);

        when(repository.findByAvailableTrue()).thenReturn(currencies);
        Map<String, CurrencyDTO> result = service.findAvailable();

        verify(repository, times(1)).findByAvailableTrue();
        verifyNoMoreInteractions(repository);
        assertEquals(2, result.size());
        assertEquals(new CurrencyDTO(usd), result.get("USD"));
        assertEquals(new CurrencyDTO(brl), result.get("BRL"));
    }

    @Test
    @DisplayName("Should return successfully when code is valid")
    void findByCodeCase1() {
        Currency usd = new Currency("USD", BigDecimal.ONE, Instant.now());
        when(repository.findByAvailableTrueAndCode("USD")).thenReturn(usd);
        CurrencyDTO result = service.findByCode("USD");

        verify(repository, times(1)).findByAvailableTrueAndCode("USD");
        verifyNoMoreInteractions(repository);
        assertEquals(new CurrencyDTO(usd), result);
    }

    @Test
    @DisplayName("Should throw exception when code is not valid")
    void findByCodeCase2() {
        when(repository.findByAvailableTrueAndCode("abc")).thenReturn(null);
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            service.findByCode("abc");
        });
        verify(repository, times(1)).findByAvailableTrueAndCode("abc");
        verifyNoMoreInteractions(repository);
        assertEquals("Recurso não encontrado! Id: abc", thrown.getMessage());
    }

    @Test
    @DisplayName("Should insert new currency successfully")
    void insertCase1() {
        CurrencyDTO dto = new CurrencyDTO();
        dto.setCode("DTO");
        dto.setValue(BigDecimal.TEN);

        when(repository.findById(dto.getCode())).thenThrow(new ResourceNotFoundException(dto.getCode()));
        Currency newCurrency = new Currency(dto.getCode(), dto.getValue(), Instant.now(), true);
        CurrencyDTO result = service.insert(dto);

        verify(repository, times(1)).findById(dto.getCode());
        verify(repository, times(1)).save(newCurrency);
        verifyNoMoreInteractions(repository);
        assertEquals(dto, result);
    }

    @Test
    @DisplayName("Should throw exception when code already exists")
    void insertCase2() {
        Currency usd = new Currency("USD", BigDecimal.ONE, Instant.now());
        CurrencyDTO dto = new CurrencyDTO(usd);
        when(repository.findById("USD")).thenReturn(Optional.of(usd));

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            service.insert(dto);
        });
        assertEquals("Essa moeda já existe", thrown.getMessage());
        verify(repository, times(1)).findById(dto.getCode());
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should throw exception when code is not valid")
    void insertCase3() {
        CurrencyDTO dto = new CurrencyDTO();
        dto.setCode("");
        dto.setValue(BigDecimal.ONE);

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            service.insert(dto);
        });
        assertEquals("A moeda precisa ter um código", thrown.getMessage());

        dto.setCode(null);
        thrown = assertThrows(BusinessException.class, () -> {
            service.insert(dto);
        });
        assertEquals("A moeda precisa ter um código", thrown.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Should throw exception when value is not valid")
    void insertCase4() {
        CurrencyDTO dto = new CurrencyDTO();
        dto.setCode("ok");
        dto.setValue(BigDecimal.ZERO);

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            service.insert(dto);
        });
        assertEquals("O valor não pode ser 0 ou nulo", thrown.getMessage());

        dto.setValue(null);
        thrown = assertThrows(BusinessException.class, () -> {
            service.insert(dto);
        });
        assertEquals("O valor não pode ser 0 ou nulo", thrown.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Should update availability to true successfully")
    void setAvailabilityCase1() {
        CurrencyDTO dto = new CurrencyDTO();
        dto.setCode("USD");
        dto.setAvailable(true);
        Currency usd = new Currency("USD", BigDecimal.ONE, Instant.now(), false);

        when(repository.findById(dto.getCode())).thenReturn(Optional.of(usd));
        CurrencyDTO result = service.setAvailability(dto);

        verify(repository, times(1)).findById(dto.getCode());
        verify(repository, times(1)).save(usd);
        verifyNoMoreInteractions(repository);
        assertEquals(dto, result);
    }

    @Test
    @DisplayName("Should update availability to false and return null")
    void setAvailabilityCase2() {
        CurrencyDTO dto = new CurrencyDTO();
        dto.setCode("USD");
        dto.setAvailable(false);
        Currency usd = new Currency("USD", BigDecimal.ONE, Instant.now(), true);

        when(repository.findById(dto.getCode())).thenReturn(Optional.of(usd));
        CurrencyDTO result = service.setAvailability(dto);

        verify(repository, times(1)).findById(dto.getCode());
        verify(repository, times(1)).save(usd);
        verifyNoMoreInteractions(repository);
        assertNull(result);
    }

    @Test
    @DisplayName("Should throw exception when code is not valid")
    void setAvailabilityCase3() {
        CurrencyDTO dto = new CurrencyDTO();
        dto.setCode("");
        dto.setAvailable(false);

        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            service.setAvailability(dto);
        });
        assertEquals("A moeda precisa ter um código", thrown.getMessage());

        dto.setCode(null);
        thrown = assertThrows(BusinessException.class, () -> {
            service.setAvailability(dto);
        });
        assertEquals("A moeda precisa ter um código", thrown.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Should update currency value successfully")
    void updateValueCase1() {
        String code = "OK";
        CurrencyDTO dto = new CurrencyDTO();
        dto.setValue(BigDecimal.TEN);
        Currency ok = new Currency("OK", BigDecimal.ONE, Instant.now(), false);

        when(apiService.getRealCurrencies()).thenReturn(Collections.emptyList());
        when(repository.findById(code)).thenReturn(Optional.of(ok));
        CurrencyDTO result = service.updateValue(code, dto);

        verify(repository, times(1)).findById(code);
        verify(repository, times(1)).save(ok);
        verifyNoMoreInteractions(repository);
        assertEquals(dto.getValue(), result.getValue());
    }

    @Test
    @DisplayName("Should throw exception when currency is real")
    void updateValueCase2() {
        String code = "USD";
        CurrencyDTO dto = new CurrencyDTO();
        dto.setValue(BigDecimal.TEN);
        Currency usd = new Currency("USD", BigDecimal.ONE, Instant.now(), false);

        when(apiService.getRealCurrencies()).thenReturn(List.of("USD"));
        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            service.updateValue(code, dto);
        });

        verifyNoInteractions(repository);
        assertEquals("Não é permitido alterar uma moeda real", thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when value is not valid")
    void updateValueCase3() {
        String code = "OK";
        CurrencyDTO dto = new CurrencyDTO();
        dto.setValue(BigDecimal.ZERO);

        when(apiService.getRealCurrencies()).thenReturn(Collections.emptyList());
        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            service.updateValue(code, dto);
        });
        assertEquals("O valor deve ser maior que 0", thrown.getMessage());

        dto.setValue(null);
        when(apiService.getRealCurrencies()).thenReturn(Collections.emptyList());
        thrown = assertThrows(BusinessException.class, () -> {
            service.updateValue(code, dto);
        });
        assertEquals("O valor deve ser maior que 0", thrown.getMessage());
        verifyNoInteractions(repository);

    }
}
