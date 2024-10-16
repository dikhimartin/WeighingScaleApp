package com.example.weighingscale.util;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class FormatterUtil {

     /**
     * Converts the sanitized input string to a double for precision.
     *
     * @param input The input string to sanitize and convert.
     * @return The double value of the sanitized input.
     * @throws NumberFormatException if the input cannot be converted to a valid number.
     */
    public static double sanitizeAndConvertToDouble(String input) throws NumberFormatException {
        // Remove all characters except digits, decimal points, and commas
        String sanitizedText = input.replaceAll("[^\\d,\\.]", "");

        // Replace commas with dots for standard decimal format
        sanitizedText = sanitizedText.replace(",", ".");

        // Check if the sanitized string is valid
        if (sanitizedText.isEmpty() || sanitizedText.equals(".") || sanitizedText.equals(",")) {
            throw new NumberFormatException("Invalid input: " + input);
        }

        // Parse the sanitized string to a double
        return Double.parseDouble(sanitizedText);
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
