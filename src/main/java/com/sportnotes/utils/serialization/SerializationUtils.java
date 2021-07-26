/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sportnotes.utils.serialization;

import com.google.gson.Gson;

/**
 *
 * @author jaakko
 */
public class SerializationUtils {

    public static Gson getBasicSerializer() {
        return DefaultSerializer.getSerializer();
    }

    public static boolean isJSONValid(String jsonInString) {
        try {
            new Gson().fromJson(jsonInString, Object.class);
            return true;
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

}
