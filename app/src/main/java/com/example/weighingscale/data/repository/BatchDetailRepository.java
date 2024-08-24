package com.example.weighingscale.data.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import com.example.weighingscale.data.local.database.AppDatabase;
import com.example.weighingscale.data.local.database.dao.BatchDetailDao;
import com.example.weighingscale.data.model.BatchDetail;

import java.util.List;

public class BatchDetailRepository {
    private BatchDetailDao batchDetailDao;
    private LiveData<List<BatchDetail>> listBatchDetail;

    public BatchDetailRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        batchDetailDao = database.batchDetailDao();
        listBatchDetail = batchDetailDao.getDatas();
    }

    public LiveData<List<BatchDetail>> getDatas() {
        return listBatchDetail;
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
}
