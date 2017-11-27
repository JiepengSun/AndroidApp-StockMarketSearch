package com.example.sunji.stockmarketsearch.util;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by sunji on 2017/11/26.
 */

public class SharedPreferences {

    private static Gson gsonForSerialization = new GsonBuilder()
            .registerTypeAdapter(Uri.class, new UriSerializer())
            .create();

    private static Gson gsonForDeserialization = new GsonBuilder()
            .registerTypeAdapter(Uri.class, new UriDeserializer())
            .create();

    private static String PREF_NAME = "SharedPreferences";

    public static void save(Context context, String key, Object object) {
        android.content.SharedPreferences sp = context.getApplicationContext().getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);

        String jsonString = gsonForSerialization.toJson(object);
        sp.edit().putString(key, jsonString).apply();
    }

    public static <T> T read(Context context, String key, TypeToken<T> typeToken) {
        android.content.SharedPreferences sp = context.getApplicationContext().getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);
        try {
            return gsonForDeserialization.fromJson(sp.getString(key, ""), typeToken.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void clear(Context context) {
        android.content.SharedPreferences sp = context.getApplicationContext().getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }

    private static class UriSerializer implements JsonSerializer<Uri> {
        @Override
        public JsonElement serialize(Uri src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    private static class UriDeserializer implements JsonDeserializer<Uri> {
        @Override
        public Uri deserialize(JsonElement src, Type srcType, JsonDeserializationContext context) throws JsonParseException {
            return Uri.parse(src.getAsString());
        }
    }

}
