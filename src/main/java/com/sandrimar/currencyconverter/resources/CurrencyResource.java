package com.sandrimar.currencyconverter.resources;

import com.sandrimar.currencyconverter.dto.CurrencyDTO;
import com.sandrimar.currencyconverter.services.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    @GetMapping("/{code}")
    public ResponseEntity<CurrencyDTO> findByCode(@PathVariable String code) {
        CurrencyDTO c = service.findByCode(code);
        return ResponseEntity.ok().body(c);
    }

    @PostMapping
    public ResponseEntity<CurrencyDTO> insert(@RequestBody CurrencyDTO dto) {
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{code}").buildAndExpand(dto.getCode()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping("/available")
    public ResponseEntity<CurrencyDTO> setAvailability(@RequestBody CurrencyDTO dto) {
        CurrencyDTO updated = service.setAvailability(dto);
        return ResponseEntity.ok().body(updated);
    }

    @PutMapping("/{code}")
    public ResponseEntity<CurrencyDTO> updateValue(@PathVariable String code, @RequestBody CurrencyDTO dto) {
        dto = service.updateValue(code, dto);
        return ResponseEntity.ok().body(dto);
    }
}
