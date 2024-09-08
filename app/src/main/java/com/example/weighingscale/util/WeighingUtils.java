package com.example.weighingscale.util;

import java.util.Date;

public class WeighingUtils {
    // Hitung durasi penimbangan (dalam jam)
    public static double calculateWeighingDuration(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        // Menghitung durasi dalam jam
        long durationMillis = endDate.getTime() - startDate.getTime();
        return durationMillis / (1000.0 * 60 * 60); // Konversi ke jam
    }

    // Hitung kecepatan penimbangan (dalam kg/jam)
    public static double calculateWeighingSpeed(double totalWeight, double duration) {
        if (duration == 0) {
            return 0;  // Menghindari pembagian dengan nol
        }
        return totalWeight / duration;  // Kecepatan = Berat / Durasi
    }

    // Menghitung kecepatan penimbangan rata-rata berdasarkan total berat dan total durasi
    public static double calculateAverageSpeed(double totalWeight, double totalDuration) {
        return calculateWeighingSpeed(totalWeight, totalDuration);
    }
}
