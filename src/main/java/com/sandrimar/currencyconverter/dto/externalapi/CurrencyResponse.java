package com.sandrimar.currencyconverter.dto.externalapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CurrencyResponse {

    @JsonProperty("code")
    private String code;
    @JsonProperty("value")
    private String value;

    public CurrencyResponse() {
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
