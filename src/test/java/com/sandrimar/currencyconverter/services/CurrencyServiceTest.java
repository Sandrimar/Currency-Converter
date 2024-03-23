package com.sandrimar.currencyconverter.services;

import com.sandrimar.currencyconverter.dto.CurrencyDTO;
import com.sandrimar.currencyconverter.model.Currency;
import com.sandrimar.currencyconverter.repositories.CurrencyRepository;
import com.sandrimar.currencyconverter.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyServiceTest {

    @Mock
    CurrencyRepository repository;

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
}
