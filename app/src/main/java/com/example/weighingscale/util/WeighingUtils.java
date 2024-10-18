package com.example.weighingscale.util;

import android.util.Log;

import com.example.weighingscale.data.dto.BatchDTO;

import java.util.List;
import java.util.Locale;

public class WeighingUtils {
    /**
     * Converts a duration from milliseconds to a float representing hours and fractional minutes.
     *
     * @param durationInMillis - The duration in milliseconds to be converted.
     * @return A float value where the integer part represents hours and the fractional part represents minutes.
     *
     * Example:
     * - If durationInMillis is 7200000 (2 hours), the method returns 2.0.
     * - If durationInMillis is 7500000 (2 hours 5 minutes), the method returns 2.0833.
     */
    public static float convertDuration(long durationInMillis) {
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
            return String.format(Locale.getDefault(), "%.2f Ton (%.2f Kg)", tons, amountInKg);
        } else if (amountInKg >= 100) {
            // Convert to Kuintal if weight is 100 Kg or more but less than 1000 Kg
            double kuintal = amountInKg / 100;
            return String.format(Locale.getDefault(), "%.2f Kuintal (%.2f Kg)", kuintal, amountInKg);
        } else {
            // No conversion, just show the weight in Kg
            return String.format(Locale.getDefault(), "%.2f Kg", amountInKg);
        }
    }

      /**
         * Converts the given weight based on its unit ("kg", "kuintal", "ton") to kilograms (Kg).
         * It supports inputs in kilograms (Kg), Kuintal, and Tons.
         *
         * @param weight - the weight of the batch
         * @param unit - the unit of the weight (can be "kg", "kuintal", "ton")
         * @return - the weight converted to kilograms (Kg)
         */
        public static double convertWeightToKilograms(double weight, String unit) {
            if (unit == null || unit.isEmpty()) {
                return 0;
            }

            // Convert based on the unit
            switch (unit.toLowerCase()) {
                case "kg":
                    return weight;
                case "kuintal":
                    return weight * 100; // 1 Kuintal = 100 Kg
                case "ton":
                    return weight * 1000; // 1 Ton = 1000 Kg
                default:
                    return 0;
            }
        }

    /**
     * Calculates the average duration of weighing from a list of BatchDTO objects.
     * This method computes the total duration from all batches, then averages it.
     * The result is returned in milliseconds.
     *
     * @param batchList - List of BatchDTO objects containing duration data
     * @return Average duration in milliseconds
     */
    public static long calculateAverageDuration(List<BatchDTO> batchList) {
        if (batchList == null || batchList.isEmpty()) return 0;

        long totalDuration = 0;
        int count = batchList.size();

        for (BatchDTO batch : batchList) {
            totalDuration += batch.duration; // Duration is in milliseconds
        }

        return totalDuration / count;
    }

    /**
     * Calculates the average weighing speed from a list of BatchDTO objects.
     * This method converts all weights to kilograms before calculating the average speed.
     * It computes the total weight and total duration, then calculates the average speed in kg/hour.
     *
     * @param batchList - List of BatchDTO objects containing weight and duration data
     * @return Average weighing speed in kilograms per hour
     */
    public static float calculateAverageSpeed(List<BatchDTO> batchList) {
        if (batchList == null || batchList.isEmpty()) return 0;

        double totalWeightInKg = 0;
        long totalDuration = 0;

        for (BatchDTO batch : batchList) {
            // Convert each batch's weight to kilograms
            totalWeightInKg += convertWeightToKilograms(batch.total_amount, batch.unit);
            totalDuration += batch.duration; // Duration in milliseconds
        }

        // Convert total duration to hours (accurate to seconds)
        float totalDurationInHours = convertDuration(totalDuration);

        // Calculate the average speed in kg/hour
        return totalDurationInHours == 0 ? 0 : (float) totalWeightInKg / totalDurationInHours;
    }

}