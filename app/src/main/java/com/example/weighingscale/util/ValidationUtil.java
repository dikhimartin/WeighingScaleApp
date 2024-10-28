package com.example.weighingscale.util;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Collection;
import java.util.Map;

/**
 * Utility class for validation and error handling in forms.
 */
public class ValidationUtil {

    private ValidationUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Sets an error message on a given view if it is an instance of EditText.
     *
     * @param view The view to set the error on.
     * @param errorMessageResId The resource ID of the error message string.
     */
    public static void setFieldError(View view, Context context, int errorMessageResId) {
        if (view instanceof EditText) {
            EditText editText = (EditText) view;
            editText.setError(context.getString(errorMessageResId));
            editText.requestFocus();
        } else if (view instanceof TextView) {
            // Handle other types of TextViews if needed
            TextView textView = (TextView) view;
            textView.setError(context.getString(errorMessageResId));
            textView.requestFocus();
        }
    }

    /**
     * Checks if the provided string can be parsed into a numeric value.
     *
     * @param str The string to check.
     * @return true if the string is numeric, false otherwise.
     */
    public static boolean isNumeric(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

     /**
     * Checks if the provided text is empty or null.
     *
     * @param text The text to check.
     * @return true if the text is null or empty, false otherwise.
     */
    public static boolean isValueEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    /**
     * Checks if the provided collection (e.g., List, Set) is empty or null.
     *
     * @param collection The collection to check.
     * @return true if the collection is null or empty, false otherwise.
     */
    public static boolean isValueEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Checks if the provided map is empty or null.
     *
     * @param map The map to check.
     * @return true if the map is null or empty, false otherwise.
     */
    public static boolean isValueEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Checks if the provided array is empty or null.
     *
     * @param array The array to check.
     * @return true if the array is null or has zero length, false otherwise.
     */
    public static boolean isValueEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Checks if the provided integer is null or zero.
     *
     * @param number The integer to check.
     * @return true if the integer is null or zero, false otherwise.
     */
    public static boolean isValueEmpty(Integer number) {
        return number == null || number == 0;
    }

    /**
     * Checks if the provided long is null or zero.
     *
     * @param number The long to check.
     * @return true if the long is null or zero, false otherwise.
     */
    public static boolean isValueEmpty(Long number) {
        return number == null || number == 0L;
    }
}