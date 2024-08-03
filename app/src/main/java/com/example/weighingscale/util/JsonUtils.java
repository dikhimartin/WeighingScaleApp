package com.example.weighingscale.util;

import android.content.Context;
import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class JsonUtils {

    public static <T> List<T> parseJson(Context context, int jsonResId, Class<T[]> clazz) {
        Gson gson = new Gson();
        InputStream inputStream = context.getResources().openRawResource(jsonResId);
        InputStreamReader reader = new InputStreamReader(inputStream);
        T[] array = gson.fromJson(reader, clazz);
        return Arrays.asList(array);
    }
}
