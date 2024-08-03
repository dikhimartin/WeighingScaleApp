package com.example.weighingscale.util;

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
}
