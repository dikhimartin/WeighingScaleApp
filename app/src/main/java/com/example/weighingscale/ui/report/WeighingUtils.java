package com.example.weighingscale.ui.report;


import android.annotation.SuppressLint;
import android.util.Log;

import com.example.weighingscale.util.FormatterUtil;

public class WeighingUtils {

    public static float convertDurationToBarChartFormat(long durationInMillis) {
        // Calculate hours and minutes from the given duration
        long hours = durationInMillis / (1000 * 60 * 60);
        long minutes = (durationInMillis / (1000 * 60)) % 60;
        // Return the time in float format (hours.fractionalMinutes)
        return hours + (minutes / 60f);
    }

    // Menghitung durasi penimbangan dalam jam
//    public static double calculateDurationInHours(Date startDate, Date endDate) {
//        if (startDate == null || endDate == null) {
//            // Jika startDate atau endDate null, kembalikan durasi 0 atau sesuai logika yang diinginkan
//            return 0;
//        }
//        long diffInMillis = endDate.getTime() - startDate.getTime();
//        return (double) diffInMillis / (1000 * 60 * 60); // Konversi dari milidetik ke jam
//    }

    // Menghitung kecepatan penimbangan rata-rata (kg/jam)
//    public static double calculateAverageSpeed(double totalWeight, double totalDurationInHours) {
//        if (totalDurationInHours == 0) return 0; // Hindari pembagian dengan nol
//        return totalWeight / totalDurationInHours;
//    }

    // Menghitung rata-rata durasi penimbangan dari daftar batch
//    public static double calculateAverageDuration(List<Double> durations) {
//        if (durations.isEmpty()) return 0;
//        double totalDuration = 0;
//        for (double duration : durations) {
//            totalDuration += duration;
//        }
//        return totalDuration / durations.size();
//    }
}