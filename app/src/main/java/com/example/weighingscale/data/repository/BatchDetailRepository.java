package com.example.weighingscale.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.weighingscale.data.local.database.AppDatabase;
import com.example.weighingscale.data.local.database.dao.BatchDetailDao;
import com.example.weighingscale.data.model.BatchDetail;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class BatchDetailRepository {

    private final BatchDetailDao batchDetailDao;
    private final ExecutorService executor;

    public BatchDetailRepository(BatchDetailDao batchDetailDao, ExecutorService executor) {
        this.batchDetailDao = batchDetailDao;
        this.executor = executor;
    }

    public static BatchDetailRepository getInstance(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        return new BatchDetailRepository(db.batchDetailDao(), AppDatabase.databaseWriteExecutor);
    }

    public void insert(BatchDetail batchDetail) {
        executor.execute(() -> batchDetailDao.insert(batchDetail));
    }

    public LiveData<List<BatchDetail>> getBatchDetailsByBatchId(int batchId) {
        return batchDetailDao.getBatchDetailsByBatchId(batchId);
    }
}
