/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sportnotes.utils.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author jaakko
 */
public class OffsetDateTimeTypeAdapter implements JsonSerializer<OffsetDateTime>, JsonDeserializer<OffsetDateTime> {

    @Override
    public JsonElement serialize(OffsetDateTime t, Type type, JsonSerializationContext jsc) {
        return new JsonPrimitive(t.format(DateTimeFormatter.ISO_DATE_TIME));
    }

    @Override
    public OffsetDateTime deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        return OffsetDateTime.parse(je.getAsString());
    }
    
}
