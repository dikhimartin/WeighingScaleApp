package com.example.weighingscale.data.repository;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;

import com.example.weighingscale.data.dto.BatchDTO;
import com.example.weighingscale.data.local.database.AppDatabase;
import com.example.weighingscale.data.local.database.dao.BatchDao;
import com.example.weighingscale.data.local.database.dao.BatchDetailDao;
import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.model.BatchDetail;

import java.util.Date;
import java.util.List;

public class BatchRepository {
    private final BatchDao batchDao;
    private final BatchDetailDao batchDetailDao;
    private final LiveData<List<BatchDTO>> listBatch;

    public BatchRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        batchDao = database.batchDao();
        batchDetailDao = database.batchDetailDao();
        listBatch = batchDao.getDatas();
    }

    public LiveData<List<BatchDTO>> getDatas() {
        return listBatch;
    }

    public LiveData<BatchDTO> getDataByID(String id) {
        return batchDao.getDataByID(id);
    }

    public LiveData<Batch> getActiveBatch() {
        return batchDao.getActiveBatch();
    }

    public void insert(Batch batch) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            batchDao.forceCompleteBatch(); // Complete any existing active batch
            batch.status = 1; // Set the new batch as active
            batchDao.insert(batch);
        });
    }

    public void completeBatch(String batchID) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Fetch batch and its details in a single transaction
            Batch batch = batchDao.getBatchByID(batchID);
            if (batch != null) {
                // Fetch batch details
                List<BatchDetail> details = batchDetailDao.getBatchDetailsByBatchID(batchID);

                // Calculate duration
                long durationMillis = 0;
                if (batch.start_date != null) {
                    batch.end_date = new Date();
                    durationMillis = batch.end_date.getTime() - batch.start_date.getTime();
                }

                // Update batch with calculated values
                batch.duration = durationMillis;
                batch.status = 0; // Mark as completed

                // Update batch in database
                batchDao.update(batch);
            }
        });
    }

    public void deleteByID(String id) {
        new DeleteByIdAsyncTask(batchDao).execute(id);
    }

    public void delete(Batch batch) {
        new DeleteAsyncTask(batchDao).execute(batch);
    }

    public void deleteByIds(List<String> ids) {
        new DeleteByIdsAsyncTask(batchDao).execute(ids);
    }

    public void deleteAll() {
        new DeleteAllAsyncTask(batchDao).execute();
    }

    private static class DeleteAsyncTask extends AsyncTask<Batch,Void,Void> {
        private BatchDao batchDao;
        private DeleteAsyncTask(BatchDao batchDao){
            this.batchDao=batchDao;
        }
        @Override
        protected Void doInBackground(Batch... batches) {
            batchDao.delete(batches[0]);
            return null;
        }
    }

    private static class DeleteAllAsyncTask extends AsyncTask<Batch,Void,Void>{
        private BatchDao batchDao;
        private DeleteAllAsyncTask(BatchDao batchDao){
            this.batchDao= this.batchDao;
        }
        @Override
        protected Void doInBackground(Batch... batches) {
            batchDao.deleteAll();
            return null;
        }
    }

    private static class DeleteByIdsAsyncTask extends AsyncTask<List<String>, Void, Void> {
        private BatchDao batchDao;
        private DeleteByIdsAsyncTask(BatchDao batchDao) {
            this.batchDao = batchDao;
        }
        @Override
        protected Void doInBackground(List<String>... lists) {
            batchDao.deleteByIDs(lists[0]);
            return null;
        }
    }

    private static class DeleteByIdAsyncTask extends AsyncTask<String, Void, Void> {
        private BatchDao batchDao;
        private DeleteByIdAsyncTask(BatchDao batchDao) {
            this.batchDao = batchDao;
        }
        @Override
        protected Void doInBackground(String... ids) {
            batchDao.deleteByID(ids[0]);
            return null;
        }
    }
}
