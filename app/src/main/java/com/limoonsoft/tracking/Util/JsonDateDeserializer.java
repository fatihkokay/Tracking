package com.limoonsoft.tracking.Util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.sql.Date;

public class JsonDateDeserializer implements JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Date date = new Date(0);
        try {
            String value = json.getAsJsonPrimitive().getAsString();
            if (!value.isEmpty()) {
                long longDate = Long.parseLong(value.substring(6, value.length() - 2));
                date = new Date(longDate);
            }
            return date;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return date;
        }
    }
}