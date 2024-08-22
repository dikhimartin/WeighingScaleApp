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
        batchDetailDao.insert(batchDetail);
    }

    public void update(BatchDetail batchDetail) {
        new UpdateNoteAsyncTask(batchDetailDao).execute(batchDetail);
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<BatchDetail,Void,Void> {
        private BatchDetailDao batchDetailDao;
        private UpdateNoteAsyncTask(BatchDetailDao batchDetailDao){
            this.batchDetailDao=batchDetailDao;
        }
        @Override
        protected Void doInBackground(BatchDetail... batchDetails) {
            batchDetailDao.update(batchDetails[0]);
            return null;
        }
    }
}
