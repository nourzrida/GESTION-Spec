package com.example.mobileapp.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class BooleanTypeAdapter implements JsonSerializer<Boolean>, JsonDeserializer<Boolean> {
    @Override
    public JsonElement serialize(Boolean src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src);
    }

    @Override
    public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            if (json.getAsJsonPrimitive().isBoolean()) {
                return json.getAsBoolean();
            } else if (json.getAsJsonPrimitive().isNumber()) {
                return json.getAsInt() == 1;
            } else if (json.getAsJsonPrimitive().isString()) {
                String stringValue = json.getAsString().toLowerCase();
                return "true".equals(stringValue) || "1".equals(stringValue) || "yes".equals(stringValue);
            }
        }
        return false;
    }
}
