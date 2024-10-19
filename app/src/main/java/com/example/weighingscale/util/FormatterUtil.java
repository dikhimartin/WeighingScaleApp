package com.example.weighingscale.util;

import android.util.Log;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class FormatterUtil {

  /**
     * Removes non-digit characters except the decimal point and converts the sanitized string to an integer.
     * The result is floored to the nearest integer.
     *
     * @param input The input string to sanitize and convert.
     * @return The floored integer value of the sanitized input.
     * @throws NumberFormatException if the input cannot be converted to a valid number.
     */
    public static int sanitizeAndConvertToInteger(String input) throws NumberFormatException {
       Log.d("log-scale", "Before : " + input);

        // Remove non-digit characters except the decimal point
        String sanitizedText = input.replaceAll("[^\\d.]", "");

        // Check if the sanitized string is empty or only contains a decimal point
        if (sanitizedText.isEmpty() || sanitizedText.equals(".")) {
            throw new NumberFormatException("Invalid input: " + input);
        }

        Log.d("log-scale ", "After : " + (int) Math.floor(Double.parseDouble(sanitizedText)));

        // Parse the sanitized string to double, then convert to integer by flooring
        return (int) Math.floor(Double.parseDouble(sanitizedText));
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
}
