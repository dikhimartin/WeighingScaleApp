package com.example.weighingscale.util;

import android.util.Log;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class FormatterUtil {

    /**
     * Removes non-digit characters except the decimal point and converts the sanitized string to an integer.
     * The result is floored to the nearest integer, with decimal parts ignored.
     *
     * @param input The input string to sanitize and convert.
     * @return The integer value of the sanitized input, or 0 if the input is invalid.
     */
    public static int sanitizeAndConvertToInteger(String input) {
        Log.d("log-scale", "Before : " + input);

        // Ganti koma dengan titik untuk parsing yang benar
        String sanitizedText = input.replaceAll(",", ".").replaceAll("[^\\d.]", "");

        // Cek apakah string yang disanitasi kosong atau hanya berisi titik desimal
        if (sanitizedText.isEmpty() || sanitizedText.equals(".")) {
            Log.d("log-scale", "Invalid input: " + input + ". Returning 0.");
            return 0;
        }

        // Ambil bagian integer dengan parsing menjadi double dan casting ke integer
        int result = (int) Double.parseDouble(sanitizedText);

        Log.d("log-scale", "After : " + result);

        return result;
    }

    /**
     * Converts the sanitized input string to a double for precision.
     *
     * @param input The input string to sanitize and convert.
     * @return The double value of the sanitized input, or 0.0 if the input is invalid.
     */
    public static double sanitizeAndConvertToDouble(String input) {
       Log.d("log-scale", "Before : " + input);

        // Remove all characters except digits, decimal points, and commas
        String sanitizedText = input.replaceAll("[^\\d,\\.]", "")
                                    .replace(",", ".");

        // Check for multiple decimal points
        if (sanitizedText.indexOf('.') != sanitizedText.lastIndexOf('.')) {
            // Log the error or handle it as needed
            System.err.println("Invalid input: " + input + " (multiple decimal points)");
            return 0.0;
        }

        // Check if the sanitized string is valid
        if (sanitizedText.isEmpty() || sanitizedText.equals(".")) {
            // Log the error or handle it as needed
            System.err.println("Invalid input: " + input);
            return 0.0;
        }

        Log.d("log-scale ", "After : " + Double.parseDouble(sanitizedText));

        // Parse the sanitized string to a double
        try {
            return Double.parseDouble(sanitizedText);
        } catch (NumberFormatException e) {
            // Log the error or handle it as needed
            System.err.println("Error parsing input: " + sanitizedText);
            return 0.0; // Return a default value
        }
    }

     /**
     * Formats a double value as a currency string with a specified prefix.
     *
     * @param prefix The prefix for the currency, e.g., "Rp".
     * @param amount The long value to format.
     * @return A string representing the formatted currency.
     */
    public static String formatCurrency(String prefix, long amount) {
        // Create a NumberFormat instance for currency formatting in the Indonesian locale
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        // Format the amount and remove the default currency symbol
        String formattedAmount = currencyFormatter.format(amount).replace(Objects.requireNonNull(NumberFormat.getCurrencyInstance().getCurrency()).getSymbol(), "").trim();

        // Combine the prefix and the formatted amount
        return prefix + " " + formattedAmount;
    }

    /**
     * Parses a string value into a Date object.
     *
     * @param value The string representation of the date (timestamp).
     * @return The parsed Date object, or null if the input is empty.
     */
    public static Date parseDate(String value) {
        return value.isEmpty() ? null : new Date(Long.parseLong(value));
    }

    /**
     * Parses a string value into a long (duration).
     *
     * @param value The string representation of the duration.
     * @return The parsed duration as a long, or 0 if the input is empty.
     */
    public static long parseLong(String value) {
        return value.isEmpty() ? 0 : Long.parseLong(value);
    }
}
