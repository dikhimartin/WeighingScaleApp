package com.example.weighingscale.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.text.NumberFormat;

/**
 * Utility class for safely handling common data types in Room database models.
 * This class provides methods to handle null and empty values gracefully,
 * ensuring that your application doesn't crash due to null pointers or unexpected data.
 */
public class SafeValueUtil {

    /**
     * Safely retrieves a non-null String value.
     *
     * @param value the input String, which may be null.
     * @param defaultValue the default value to return if the input is null or empty.
     * @return the input String if it is not null or empty, otherwise returns the default value.
     */
    public static String getString(String value, String defaultValue) {
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    /**
     * Safely formats a Date object to a String based on the provided format.
     *
     * @param date the Date object to be formatted, which may be null.
     * @param format the desired date format (e.g., "dd MMMM yyyy").
     * @return the formatted date string, or an empty string if the date is null.
     */
    public static String getFormattedDate(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Safely retrieves a non-null Integer value.
     *
     * @param value the input Integer, which may be null.
     * @param defaultValue the default value to return if the input is null.
     * @return the input Integer if it is not null, otherwise returns the default value.
     */
    public static int getInt(Integer value, int defaultValue) {
        return (value != null) ? value : defaultValue;
    }

    /**
     * Safely retrieves a non-null Double value.
     *
     * @param value the input Double, which may be null.
     * @param defaultValue the default value to return if the input is null.
     * @return the input Double if it is not null, otherwise returns the default value.
     */
    public static double getDouble(Double value, double defaultValue) {
        return (value != null) ? value : defaultValue;
    }

    /**
     * Safely formats a long value as a currency string.
     *
     * @param currencySymbol the symbol to prefix the formatted value (e.g., "Rp").
     * @param value the long value to format.
     * @return the formatted currency string.
     */
    public static String formatCurrency(String currencySymbol, long value) {
        String formattedNumber = NumberFormat.getNumberInstance(Locale.getDefault()).format(value);
        return String.format("%s %s", currencySymbol, formattedNumber);
    }

    /**
     * Safely retrieves a non-null Long value.
     *
     * @param value the input Long, which may be null.
     * @param defaultValue the default value to return if the input is null.
     * @return the input Long if it is not null, otherwise returns the default value.
     */
    public static long getLong(Long value, long defaultValue) {
        return (value != null) ? value : defaultValue;
    }

    /**
     * Safely retrieves a non-null Boolean value.
     *
     * @param value the input Boolean, which may be null.
     * @param defaultValue the default value to return if the input is null.
     * @return the input Boolean if it is not null, otherwise returns the default value.
     */
    public static boolean getBoolean(Boolean value, boolean defaultValue) {
        return (value != null) ? value : defaultValue;
    }
}
