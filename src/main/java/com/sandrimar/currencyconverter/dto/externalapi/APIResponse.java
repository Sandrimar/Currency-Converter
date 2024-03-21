package com.sandrimar.currencyconverter.dto.externalapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class APIResponse {

    @JsonProperty("meta")
    private MetaResponse meta;
    @JsonProperty("data")
    private Map<String, CurrencyResponse> data;

    public APIResponse() {
    }

    public MetaResponse getMeta() {
        return meta;
    }

    public Map<String, CurrencyResponse> getData() {
        return data;
    }
}
