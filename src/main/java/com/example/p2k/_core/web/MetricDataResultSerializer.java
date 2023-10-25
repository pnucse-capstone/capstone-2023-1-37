package com.example.p2k._core.web;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataResult;

import java.io.IOException;

public class MetricDataResultSerializer extends JsonSerializer<MetricDataResult> {
    @Override
    public void serialize(MetricDataResult value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("label", value.label());
        gen.writeStringField("statusCode", value.statusCode().toString());
        gen.writeEndObject();
    }
}
