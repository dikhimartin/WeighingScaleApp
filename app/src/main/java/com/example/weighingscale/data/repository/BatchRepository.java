package com.example.weighingscale.data.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import com.example.weighingscale.data.local.database.AppDatabase;
import com.example.weighingscale.data.local.database.dao.BatchDao;
import com.example.weighingscale.data.local.database.dao.BatchDetailDao;
import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.model.BatchDetail;

import java.util.List;

public class BatchRepository {
    private BatchDao batchDao;
    private BatchDetailDao batchDetailDao;
    private LiveData<List<Batch>> listBatch;

    public BatchRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        batchDao = database.batchDao();
        batchDetailDao = database.batchDetailDao();
        listBatch = batchDao.getDatas();
    }

    public LiveData<List<Batch>> getDatas() {
        return listBatch;
    }

    public LiveData<Batch> getDataById(String id) {
        return batchDao.getDataById(id);
    }

    public LiveData<Batch> getActiveBatch() {
        return batchDao.getActiveBatch();
    }

    public void insertBatch(Batch batch) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            batchDao.forceCompleteBatch(); // Complete any existing active batch
            batch.status = 1; // Set the new batch as active
            batchDao.insertBatch(batch);
        });
    }

    public void completeBatch(String batchId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Batch batch = batchDao.getBatchById(batchId);
            if (batch != null) {
                List<BatchDetail> details = batchDetailDao.getBatchDetailsByBatchId(batchId);
                int totalAmount = 0;
                double totalPrice = 0.0;
                if (details != null) {
                    for (BatchDetail detail : details) {
                        totalAmount += detail.amount;
                        totalPrice += detail.price;
                    }
                }

                // Calculate duration
                long durationMillis = 0;
                if (batch.start_date != null && batch.end_date != null) {
                    durationMillis = batch.end_date.getTime() - batch.start_date.getTime();
                }

                // Update batch with calculated values
                batch.total_amount = totalAmount;
                batch.total_price = totalPrice;
                batch.duration = durationMillis;
                batch.status = 0; // Mark as completed

                // Update batch in database
                batchDao.updateBatch(batch);
            }
        });
    }

    public void delete(Batch batch) {
        new DeleteAsyncTask(batchDao).execute(batch);
    }

    public void deleteAll() {
        new DeleteAllAsyncTask(batchDao).execute();
    }

    public void deleteByIds(List<String> ids) {
        new DeleteByIdsAsyncTask(batchDao).execute(ids);
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
            batchDao.deleteByIds(lists[0]);
            return null;
        }
    }
}
