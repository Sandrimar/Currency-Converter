package com.sandrimar.currencyconverter.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

public class StringAsNumberSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        BigDecimal bigDecimal = new BigDecimal(s);
        jsonGenerator.writeNumber(bigDecimal);
    }
}
