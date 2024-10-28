package com.example.weighingscale.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.weighingscale.data.model.BatchDetail;
import com.example.weighingscale.data.model.Setting;
import com.example.weighingscale.data.repository.BatchDetailRepository;

import java.util.Date;
import java.util.List;

public class BatchDetailViewModel extends AndroidViewModel {
    private final BatchDetailRepository batchDetailRepository;

    public BatchDetailViewModel (@NonNull Application application){
        super(application);
        batchDetailRepository = new BatchDetailRepository(application);
    }

    public void getAllBatchDetail(BatchDetailRepository.Callback<List<BatchDetail>> callback) {
        batchDetailRepository.getDatas(callback);
    }

    public LiveData<List<BatchDetail>> getBatchDetails(String batchId) {
        return batchDetailRepository.getDatasByBatchID(batchId);
    }

    public void insert(String batchId, int amount, Setting setting) {
        BatchDetail batchDetail = new BatchDetail();
        batchDetail.batch_id = batchId;
        batchDetail.datetime = new Date();
        batchDetail.amount = amount;
        batchDetailRepository.insert(batchDetail);
    }

    public void update(BatchDetail batchDetail) {
        batchDetailRepository.update(batchDetail);
    }
}