package com.sandrimar.currencyconverter.services;

import com.sandrimar.currencyconverter.dto.externalapi.APIResponse;
import com.sandrimar.currencyconverter.dto.externalapi.CurrencyResponse;
import com.sandrimar.currencyconverter.dto.externalapi.MetaResponse;
import com.sandrimar.currencyconverter.model.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExternalAPIServiceTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    ExternalAPIService apiService;

    MetaResponse meta;
    Map<String, CurrencyResponse> data = new HashMap<>();
    APIResponse apiResponse;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        meta = mock(MetaResponse.class);
        when(meta.getLastUpdate()).thenReturn(Instant.now());

        CurrencyResponse usd = mock(CurrencyResponse.class);
        when(usd.getCode()).thenReturn("USD");
        when(usd.getValue()).thenReturn("1");
        data.put("USD", usd);

        CurrencyResponse brl = mock(CurrencyResponse.class);
        when(brl.getCode()).thenReturn("BRL");
        when(brl.getValue()).thenReturn("4.9");
        data.put("BRL", brl);

        apiResponse = mock(APIResponse.class);
        when(apiResponse.getMeta()).thenReturn(meta);
        when(apiResponse.getData()).thenReturn(data);
    }

    @Test
    @DisplayName("Should return all data successfully")
    void getData() {
        when(restTemplate.getForEntity(anyString(), eq(APIResponse.class))).thenReturn(ResponseEntity.ok(apiResponse));
        List<Currency> currencies = apiService.getData();

        assertEquals(2, currencies.size());
        assertEquals("USD", currencies.getFirst().getCode());
        assertEquals(BigDecimal.ONE, currencies.getFirst().getValue());
        assertEquals(meta.getLastUpdate(), currencies.getFirst().getLastUpdate());

        assertEquals("BRL", currencies.get(1).getCode());
        assertEquals(new BigDecimal("4.9"), currencies.get(1).getValue());
        assertEquals(meta.getLastUpdate(), currencies.get(1).getLastUpdate());
    }

    @Test
    @DisplayName("Should have two currencies (USD and BRL)")
    void getRealCurrencies() {
        when(restTemplate.getForEntity(anyString(), eq(APIResponse.class))).thenReturn(ResponseEntity.ok(apiResponse));
        apiService.getData();

        List<String> codes = apiService.getRealCurrencies();
        assertEquals(2, codes.size());
        assertEquals("USD", codes.getFirst());
        assertEquals("BRL", codes.get(1));
    }
}
