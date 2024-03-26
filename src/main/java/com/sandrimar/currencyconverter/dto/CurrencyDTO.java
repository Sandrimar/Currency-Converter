package com.sandrimar.currencyconverter.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sandrimar.currencyconverter.config.StringAsNumberSerializer;
import com.sandrimar.currencyconverter.model.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class CurrencyDTO {

    private String code;
    @JsonSerialize(using = StringAsNumberSerializer.class)
    private String value;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
    private Instant lastUpdate;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean available;

    public CurrencyDTO() {
    }

    public CurrencyDTO(Currency obj) {
        code = obj.getCode();
        value = obj.getValue().stripTrailingZeros().toPlainString();
        lastUpdate = obj.getLastUpdate();
        available = obj.isAvailable();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = sanitizeCode(code);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = sanitizeValue(value);
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

    private String sanitizeCode(String code) {
        return (code == null) ? "" : code.toUpperCase();
    }

    private String sanitizeValue(String value) {
        return (value == null) ? BigDecimal.ZERO.toPlainString() : new BigDecimal(value).stripTrailingZeros().toPlainString();
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
