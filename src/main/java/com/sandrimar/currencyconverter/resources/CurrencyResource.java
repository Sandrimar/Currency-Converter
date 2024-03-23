package com.sandrimar.currencyconverter.resources;

import com.sandrimar.currencyconverter.dto.CurrencyDTO;
import com.sandrimar.currencyconverter.services.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/currencies")
public class CurrencyResource {

    @Autowired
    private CurrencyService service;

    @GetMapping("/available")
    public ResponseEntity<Map<String, CurrencyDTO>> findAvailable() {
        Map<String, CurrencyDTO> available = service.findAvailable();
        return ResponseEntity.ok().body(available);
    }
}
