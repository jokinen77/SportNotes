/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sportnotes.utils.serialization;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.OffsetDateTime;

/**
 *
 * @author jaakko
 */

public class DefaultSerializer {
    
    public static Gson getSerializer() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeTypeAdapter())
                .addSerializationExclusionStrategy(new SerializationExclusionStrategy())
                .addDeserializationExclusionStrategy(new DeserializationExclusionStrategy())
                .create();
        return gson;
    }
    
    
    public static class SerializationExclusionStrategy implements ExclusionStrategy {

        @Override
        public boolean shouldSkipField(FieldAttributes field) {
            if (field.getAnnotation(ExcludeSerialization.class) != null && field.getAnnotation(ExcludeSerialization.class).serialization()) {
                return true;
            }
            return false;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }
    
    public static class DeserializationExclusionStrategy implements ExclusionStrategy {

        @Override
        public boolean shouldSkipField(FieldAttributes field) {
            if (field.getAnnotation(ExcludeSerialization.class) != null && field.getAnnotation(ExcludeSerialization.class).deserialization()) {
                return true;
            }
            return false;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }
}
