package com.example.weighingscale.util;

import java.text.NumberFormat;
import java.util.Locale;

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
        // Remove non-digit characters except the decimal point
        String sanitizedText = input.replaceAll("[^\\d.]", "");

        // Check if the sanitized string is empty or only contains a decimal point
        if (sanitizedText.isEmpty() || sanitizedText.equals(".")) {
            throw new NumberFormatException("Invalid input: " + input);
        }

        // Parse the sanitized string to double, then convert to integer by flooring
        return (int) Math.floor(Double.parseDouble(sanitizedText));
    }

     /**
     * Formats a double value as a currency string with a specified prefix.
     *
     * @param prefix The prefix for the currency, e.g., "Rp".
     * @param amount The double value to format.
     * @return A string representing the formatted currency.
     */
    public static String formatCurrency(String prefix, double amount) {
        // Create a NumberFormat instance for currency formatting in the Indonesian locale
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        // Format the amount and remove the default currency symbol
        String formattedAmount = currencyFormatter.format(amount).replace(NumberFormat.getCurrencyInstance().getCurrency().getSymbol(), "").trim();

        // Combine the prefix and the formatted amount
        return prefix + " " + formattedAmount;
    }

     /**
     * Formats the given duration in milliseconds into a human-readable format.
     * Converts the duration into hours and minutes if the duration is greater than or equal to 60 minutes.
     * If less than 60 minutes, it only shows the minutes.
     *
     * @param durationMillis The duration in milliseconds.
     * @return A human-readable string representing the duration.
     */
    public static String formatDuration(long durationMillis) {
        if (durationMillis <= 0) {
            return "-";
        }

        // Calculate the duration in total minutes and seconds
        long totalSeconds = durationMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        long hours = minutes / 60;
        minutes = minutes % 60;

        // Format the duration string based on hours and minutes
        if (hours > 0) {
            return hours + " Jam " + minutes + " Menit" + (seconds > 0 ? " " + seconds + " Detik" : "");
        } else {
            return minutes + " Menit" + (seconds > 0 ? " " + seconds + " Detik" : "");
        }
    }

}
