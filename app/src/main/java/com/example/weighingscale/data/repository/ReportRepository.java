package com.example.weighingscale.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.weighingscale.data.dto.BatchDTO;
import com.example.weighingscale.data.dto.ReportDTO;
import com.example.weighingscale.data.local.database.AppDatabase;
import com.example.weighingscale.data.local.database.dao.BatchDao;
import com.example.weighingscale.util.LogModelUtils;
import com.example.weighingscale.util.SafeValueUtil;
import com.example.weighingscale.util.WeighingUtils;

import java.util.Calendar;
import java.util.Date;

public class ReportRepository {
    private final BatchDao batchDao;

    public ReportRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        this.batchDao = database.batchDao();
    }

    public LiveData<ReportDTO> getAverageWeighingSpeed(Date startDate, Date endDate) {
        return Transformations.map(batchDao.getDatas(null, startDate, endDate), batches -> {
            if (batches == null || batches.isEmpty()) {
                return new ReportDTO(0, 0);
            }

            double totalDuration = 0;
            double totalWeight = 0;

            for (BatchDTO batch : batches) {
                double duration = WeighingUtils.calculateWeighingDuration(startDate, endDate);
                totalDuration += duration;
                totalWeight += batch.total_amount;
            }

            double avgSpeed = WeighingUtils.calculateAverageSpeed(totalWeight, totalDuration);

            return new ReportDTO(totalDuration / batches.size(), avgSpeed);
        });
    }


//    public LiveData<ReportDTO> getAverageWeighingSpeed(Date startDate, Date endDate) {
//        Log.e("MODULE_REPORT_REPO", String.valueOf(startDate));
//        Log.e("MODULE_REPORT_REPO", String.valueOf(endDate));
//        // Cek null sebelum meneruskan ke DAO atau memproses
//        if (startDate == null || endDate == null) {
//            Log.e("MODULE_REPORT_REPO", "Start date or end date is null");
//            return new MutableLiveData<>(null);  // Mengembalikan nilai default jika ada yang null
//        }
//
//        // Pastikan DAO hanya menerima nilai yang valid
//        return Transformations.map(batchDao.getDatas(null, startDate, endDate), batches -> {
//            if (batches == null || batches.isEmpty()) {
//                Log.e("MODULE_REPORT_REPO", "No batch data found for the given period");
//                return new ReportDTO(0, 0);  // Mengembalikan data default jika tidak ada batch
//            }
//
//            double totalDuration = 0;
//            double totalWeight = 0;
//
//            for (BatchDTO batch : batches) {
//                // Hitung durasi hanya jika kedua tanggal tidak null
//                double duration = (endDate.getTime() - startDate.getTime()) / (1000.0 * 60 * 60); // dalam jam
//                totalDuration += duration;
//                totalWeight += batch.total_amount; // diasumsikan ini dalam kg
//            }
//
//            // Pastikan totalDuration tidak nol untuk menghindari pembagian nol
//            double avgSpeed = (totalDuration > 0) ? totalWeight / totalDuration : 0;
//            Log.d("MODULE_REPORT_REPO", String.valueOf(avgSpeed));
//            Log.d("MODULE_REPORT_REPO", String.valueOf(batches.size()));
//            Log.d("MODULE_REPORT_REPO", String.valueOf(totalDuration));
//
//            // Mengembalikan nilai rata-rata durasi dan kecepatan
//            return new ReportDTO(
//                (batches.size() > 0 && totalDuration > 0) ? totalDuration / batches.size() : 0,
//                avgSpeed
//            );
//        });
//    }

}
