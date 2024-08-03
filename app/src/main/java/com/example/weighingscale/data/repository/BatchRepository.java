package com.example.weighingscale.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import com.example.weighingscale.data.local.database.AppDatabase;
import com.example.weighingscale.data.local.database.dao.BatchDao;
import com.example.weighingscale.data.model.Batch;

import java.util.List;

public class BatchRepository {
    private BatchDao batchDao;
    private LiveData<List<Batch>> listBatch;

    public BatchRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        batchDao = database.batchDao();
        listBatch = batchDao.getDatas();
    }

    public LiveData<List<Batch>> getDatas() {
        return listBatch;
    }

    public LiveData<Batch> getActiveBatch() {
        return batchDao.getActiveBatch();
    }

    public void insertBatch(Batch batch) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            batchDao.completeBatch(); // Complete any existing active batch
            batch.status = 1; // Set the new batch as active
            batchDao.insertBatch(batch);
        });
    }

    public void completeBatch(String batchId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            batchDao.completeBatch(batchId);
        });
    }
}
