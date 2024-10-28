package com.example.weighingscale.data.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import com.example.weighingscale.data.local.database.AppDatabase;
import com.example.weighingscale.data.local.database.dao.BatchDetailDao;
import com.example.weighingscale.data.model.BatchDetail;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchDetailRepository {
    private final BatchDetailDao batchDetailDao;
    private final ExecutorService executorService;

    public BatchDetailRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        batchDetailDao = database.batchDetailDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void getDatas(Callback<List<BatchDetail>> callback) {
        executorService.execute(() -> {
            List<BatchDetail> batches = batchDetailDao.getDatas();
            callback.onResult(batches);
        });
    }

    public LiveData<List<BatchDetail>> getDatasByBatchID(String batchId) {
        return batchDetailDao.getDatasByBatchID(batchId);
    }

    public void insert(BatchDetail batchDetail) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            batchDetailDao.insert(batchDetail);
        });
    }

    public void update(BatchDetail batchDetail) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            batchDetailDao.update(batchDetail);
        });
    }

    public interface Callback<T> {
        void onResult(T result);
    }
}
