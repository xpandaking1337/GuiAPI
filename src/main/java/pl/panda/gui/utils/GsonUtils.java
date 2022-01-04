package pl.panda.gui.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtils {

    private static final Gson GSON = new GsonBuilder().create();

    public static String parseToJson(final Object object) {
        return GSON.toJson(object);
    }

    public static <T> T parseToObject(final String json, final Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

}
