package com.sandrimar.currencyconverter.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sandrimar.currencyconverter.config.StringAsNumberSerializer;

import java.time.Instant;

public class ConversionResultDTO {

    private String fromCurrencyCode;
    private String toCurrencyCode;
    private Double amount;
    @JsonSerialize(using = StringAsNumberSerializer.class)
    private String result;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
    private Instant timestamp;

    public ConversionResultDTO() {
    }

    public ConversionResultDTO(String fromCurrencyCode, String toCurrencyCode,
                               Double amount, String result, Instant timestamp) {
        this.fromCurrencyCode = fromCurrencyCode;
        this.toCurrencyCode = toCurrencyCode;
        this.amount = amount;
        this.result = result;
        this.timestamp = timestamp;
    }

    public String getFromCurrencyCode() {
        return fromCurrencyCode;
    }

    public void setFromCurrencyCode(String fromCurrencyCode) {
        this.fromCurrencyCode = fromCurrencyCode;
    }

    public String getToCurrencyCode() {
        return toCurrencyCode;
    }

    public void setToCurrencyCode(String toCurrencyCode) {
        this.toCurrencyCode = toCurrencyCode;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
