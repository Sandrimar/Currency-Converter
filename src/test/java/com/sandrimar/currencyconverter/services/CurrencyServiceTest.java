package com.sandrimar.currencyconverter.services;

import com.sandrimar.currencyconverter.dto.ConversionResultDTO;
import com.sandrimar.currencyconverter.dto.CurrencyDTO;
import com.sandrimar.currencyconverter.model.Currency;
import com.sandrimar.currencyconverter.repositories.CurrencyRepository;
import com.sandrimar.currencyconverter.services.exceptions.BadRequestException;
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
import java.time.temporal.ChronoUnit;
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
        dto.setValue(BigDecimal.TEN.toPlainString());

        when(repository.findById(dto.getCode())).thenThrow(new ResourceNotFoundException(dto.getCode()));
        Currency newCurrency = new Currency(dto.getCode(), new BigDecimal(dto.getValue()), Instant.now(), true);
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
        dto.setValue(BigDecimal.ONE.toPlainString());

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
        dto.setValue(BigDecimal.ZERO.toPlainString());

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
        dto.setValue(BigDecimal.TEN.toPlainString());
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
        dto.setValue(BigDecimal.TEN.toPlainString());
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
        dto.setValue(BigDecimal.ZERO.toPlainString());

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

    @Test
    @DisplayName("Should delete currency successfully")
    void deleteCase1() {
        String code = "OK";
        Currency ok = new Currency("OK", BigDecimal.TEN, Instant.now(), true);

        when(apiService.getRealCurrencies()).thenReturn(Collections.emptyList());
        when(repository.findById(code)).thenReturn(Optional.of(ok));
        service.delete(code);

        verify(apiService, times(1)).getRealCurrencies();
        verify(repository, times(1)).findById(code);
        verify(repository, times(1)).deleteById(code);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should throw exception when currency is real")
    void deleteCase2() {
        String code = "USD";

        when(apiService.getRealCurrencies()).thenReturn(List.of("USD"));
        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            service.delete(code);
        });

        verifyNoInteractions(repository);
        assertEquals("Não é permitido apagar uma moeda real", thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when code is not valid")
    void deleteCase3() {
        String code = "";

        when(apiService.getRealCurrencies()).thenReturn(Collections.emptyList());
        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            service.delete(code);
        });
        assertEquals("A moeda precisa ter um código", thrown.getMessage());

        verify(apiService, times(1)).getRealCurrencies();
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Should throw exception when currency is not found")
    void deleteCase4() {
        String code = "XYZ";

        when(apiService.getRealCurrencies()).thenReturn(Collections.emptyList());
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(code);
        });

        verify(apiService, times(1)).getRealCurrencies();
        verify(repository, times(1)).findById(code);
        verifyNoMoreInteractions(repository);
        assertEquals("Recurso não encontrado! Id: XYZ", thrown.getMessage());
    }

    @Test
    @DisplayName("Should update real currencies successfully")
    void updateRealCurrencies() {
        List<String> codes = new ArrayList<>(Arrays.asList("USD", "BRL", "BTC"));
        Currency oldUsd = new Currency("USD", BigDecimal.ONE, Instant.now().minus(24, ChronoUnit.HOURS));
        Currency oldBrl = new Currency("BRL", BigDecimal.TWO, Instant.now().minus(24, ChronoUnit.HOURS));
        List<Currency> oldCurrencies = new ArrayList<>(Arrays.asList(oldUsd, oldBrl));
        Currency newUsd = new Currency("USD", BigDecimal.TEN, Instant.now());
        Currency newBrl = new Currency("BRL", BigDecimal.ONE, Instant.now());
        List<Currency> newData = new ArrayList<>(Arrays.asList(newUsd, newBrl));

        when(apiService.getRealCurrencies()).thenReturn(codes);
        when(repository.findByCodeIn(codes)).thenReturn(oldCurrencies);
        when(apiService.getData()).thenReturn(newData);
        service.updateRealCurrencies();

        assertEquals(newUsd.getValue(), oldUsd.getValue());
        assertEquals(newBrl.getValue(), oldBrl.getValue());
        assertEquals(newUsd.getLastUpdate(), oldUsd.getLastUpdate());
        assertEquals(newBrl.getLastUpdate(), oldBrl.getLastUpdate());
        verify(apiService, times(1)).getRealCurrencies();
        verify(repository, times(1)).findByCodeIn(codes);
        verify(apiService, times(1)).getData();
        verify(repository, times(1)).saveAll(anyCollection());
        verifyNoMoreInteractions(apiService);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should convert an amount between two currencies successfully")
    void convertCase1() {
        Currency from = new Currency("EUR", new BigDecimal("0.92"), Instant.now(), true);
        Currency brl = new Currency("BRL", new BigDecimal("4.97"), Instant.now(), true);
        String amount = "230";

        when(repository.findByAvailableTrueAndCode("EUR")).thenReturn(from);
        when(repository.findByAvailableTrueAndCode("BRL")).thenReturn(brl);
        ConversionResultDTO result = service.convert("EUR", "BRL", amount);

        verify(repository, times(2)).findByAvailableTrueAndCode(anyString());
        verifyNoMoreInteractions(repository);
        assertEquals("1242.5", result.getResult());
    }

    @Test
    @DisplayName("Should throw error when code is not valid")
    void convertCase2() {
        when(repository.findByAvailableTrueAndCode("")).thenThrow(BadRequestException.class);

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            service.convert("", "", "10");
        });

        verifyNoInteractions(repository);
        assertEquals("Parâmetros inválidos na requisição", thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw error when code is not found")
    void convertCase3() {
        when(repository.findByAvailableTrueAndCode(anyString())).thenReturn(null);

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            service.convert("A", "B", "10");
        });

        verify(repository, times(1)).findByAvailableTrueAndCode(anyString());
        verifyNoMoreInteractions(repository);
        assertEquals("Recurso não encontrado! Id: A", thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw error when amount is not a number")
    void convertCase4() {
        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            service.convert("A", "B", "v");
        });

        verifyNoInteractions(repository);
        assertEquals("Parâmetros inválidos na requisição", thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw error when amount is zero")
    void convertCase5() {
        BusinessException thrown = assertThrows(BusinessException.class, () -> {
            service.convert("A", "B", "0");
        });

        verifyNoInteractions(repository);
        assertEquals("A quantidade não pode ser negativa ou 0", thrown.getMessage());
    }
}
