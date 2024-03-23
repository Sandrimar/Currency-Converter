package com.sandrimar.currencyconverter.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sandrimar.currencyconverter.model.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class CurrencyDTO {

    private String code;
    private BigDecimal value;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
    private Instant lastUpdate;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean available;

    public CurrencyDTO() {
    }

    public CurrencyDTO(Currency obj) {
        code = obj.getCode();
        value = obj.getValue().stripTrailingZeros();
        lastUpdate = obj.getLastUpdate();
        available = obj.isAvailable();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value.stripTrailingZeros();
    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyDTO that = (CurrencyDTO) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
