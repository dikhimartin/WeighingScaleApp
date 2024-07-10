package com.example.weighingscale.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.weighingscale.data.local.database.AppDatabase;
import com.example.weighingscale.data.local.database.dao.BatchDao;
import com.example.weighingscale.data.model.Batch;

import java.util.concurrent.ExecutorService;

public class BatchRepository {

    private final BatchDao batchDao;
    private final ExecutorService executor;

    public BatchRepository(BatchDao batchDao, ExecutorService executor) {
        this.batchDao = batchDao;
        this.executor = executor;
    }

    public static BatchRepository getInstance(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        return new BatchRepository(db.batchDao(), AppDatabase.databaseWriteExecutor);
    }

    public LiveData<Batch> getActiveBatch() {
        return batchDao.getActiveBatch();
    }

    public void insertBatch(Batch batch) {
        executor.execute(() -> {
            batchDao.completeAllBatches(); // Complete any existing active batch
            batch.status = 1; // Set the new batch as active
            batchDao.insertBatch(batch);
        });
    }

    public void completeBatch(int batchId) {
        executor.execute(() -> batchDao.completeBatch(batchId));
    }
}
