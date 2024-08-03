package com.example.weighingscale.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Utility class for validation and error handling in forms.
 */
public class ValidationUtil {

    private ValidationUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Checks if the provided text is empty or null.
     *
     * @param text The text to check.
     * @return true if the text is null or empty, false otherwise.
     */
    public static boolean isFieldEmpty(String text) {
        return text == null || text.trim().isEmpty();
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
}