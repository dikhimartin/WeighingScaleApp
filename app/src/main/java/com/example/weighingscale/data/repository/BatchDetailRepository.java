package com.example.weighingscale.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.weighingscale.data.local.database.AppDatabase;
import com.example.weighingscale.data.local.database.dao.BatchDetailDao;
import com.example.weighingscale.data.model.BatchDetail;
import com.example.weighingscale.data.model.Note;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class BatchDetailRepository {

    private final BatchDetailDao batchDetailDao;
    private final ExecutorService executor;
    private LiveData<List<BatchDetail>> allNotes;

    public BatchDetailRepository(BatchDetailDao batchDetailDao, ExecutorService executor) {
        this.batchDetailDao = batchDetailDao;
        this.executor = executor;
        allNotes = batchDetailDao.getAllNotes();
    }

    public static BatchDetailRepository getInstance(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        return new BatchDetailRepository(db.batchDetailDao(), AppDatabase.databaseWriteExecutor);
    }

    public void insert(BatchDetail batchDetail) {
        executor.execute(() -> batchDetailDao.insert(batchDetail));
    }

    public LiveData<List<BatchDetail>> getAllNotes() {
        return allNotes;
    }

    public LiveData<List<BatchDetail>> getBatchDetails(int batchId) {
        return batchDetailDao.getBatchDetails(batchId);
    }
}
