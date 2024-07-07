package com.example.weighingscale.util;

import android.util.Log;

import java.lang.reflect.Field;

// How to use
// LogModelUtils.printObjectFields(setting);
public class LogModelUtils {

    private static final String TAG = LogModelUtils.class.getSimpleName();

    public static void printObjectFields(Object object) {
        Class<?> objectClass = object.getClass();
        Field[] fields = objectClass.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true); // Memastikan akses ke field yang private atau protected
                Object value = field.get(object);
                Log.d(TAG, field.getName() + ": " + value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
