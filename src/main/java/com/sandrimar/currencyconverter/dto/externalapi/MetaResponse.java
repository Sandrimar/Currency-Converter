package com.sandrimar.currencyconverter.dto.externalapi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class MetaResponse {

    @JsonProperty("last_updated_at")
    private Instant lastUpdate;

    public MetaResponse() {
    }

    public Instant getLastUpdate() {
        return lastUpdate;
    }
}
