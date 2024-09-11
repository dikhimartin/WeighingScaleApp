package com.example.weighingscale.ui.report;


import java.util.Locale;

public class WeighingUtils {
    public static float convertDurationToBarChartFormat(long durationInMillis) {
        // Calculate hours and minutes from the given duration
        long hours = durationInMillis / (1000 * 60 * 60);
        long minutes = (durationInMillis / (1000 * 60)) % 60;
        // Return the time in float format (hours.fractionalMinutes)
        return hours + (minutes / 60f);
    }

    /**
     * Converts the given weight based on its unit ("kg", "kuintal", "ton") to a human-readable format.
     * It supports inputs in kilograms (Kg), Kuintal, and Tons, and formats the output in a higher unit if applicable.
     * If the unit is unsupported, it will return a default message indicating invalid input.
     *
     * @param totalAmount - the weight of the rice in the original unit
     * @param unit - the unit of the weight (can be "kg", "kuintal", "ton")
     * @return - a formatted string displaying the weight in a higher unit like "Kuintal" or "Ton",
     *           followed by the original weight in kilograms (Kg), or an error message for unsupported units.
     *
     * Example outputs:
     * - "1 Kuintal (100 Kg)" if totalAmount is 100 and unit is "kg".
     * - "1.5 Kuintal (150 Kg)" if totalAmount is 150 and unit is "kg".
     * - "1 Ton (1000 Kg)" if totalAmount is 1000 and unit is "kg".
     * - "Invalid unit" for unsupported units.
     */
    public static String convertWeight(double totalAmount, String unit) {
        if (unit == null || unit.isEmpty()) {
            return "Invalid unit"; // Handle empty or null unit input gracefully
        }

        // Normalize all input to kilograms
        double amountInKg;
        switch (unit.toLowerCase()) {
            case "kg":
                amountInKg = totalAmount;
                break;
            case "kuintal":
                amountInKg = totalAmount * 100; // 1 Kuintal = 100 Kg
                break;
            case "ton":
                amountInKg = totalAmount * 1000; // 1 Ton = 1000 Kg
                break;
            default:
                return "Invalid unit"; // Gracefully handle unsupported units
        }

        // Convert and format based on the amount in Kg
        if (amountInKg >= 1000) {
            // Convert to Tons if weight is 1000 Kg or more
            double tons = amountInKg / 1000;
            return String.format(Locale.getDefault(), "%.2f Ton (%.0f Kg)", tons, amountInKg);
        } else if (amountInKg >= 100) {
            // Convert to Kuintal if weight is 100 Kg or more but less than 1000 Kg
            double kuintal = amountInKg / 100;
            return String.format(Locale.getDefault(), "%.2f Kuintal (%.0f Kg)", kuintal, amountInKg);
        } else {
            // No conversion, just show the weight in Kg
            return String.format(Locale.getDefault(), "%.0f Kg", amountInKg);
        }
    }

}