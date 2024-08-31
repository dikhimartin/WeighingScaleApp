package com.example.weighingscale.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.weighingscale.data.local.database.AppDatabase;
import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.model.BatchDetail;
import com.example.weighingscale.data.model.Province;
import com.example.weighingscale.data.model.City;
import com.example.weighingscale.data.model.Setting;
import com.example.weighingscale.data.repository.AddressRepository;
import com.example.weighingscale.data.repository.BatchRepository;
import com.example.weighingscale.data.repository.BatchDetailRepository;

import java.util.Date;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final BatchRepository batchRepository;
    private final BatchDetailRepository batchDetailRepository;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        batchRepository = new BatchRepository(application);
        batchDetailRepository = new BatchDetailRepository(application);
    }

    public LiveData<List<BatchDetail>> getBatchDetails(String batchId) {
        return batchDetailRepository.getDatasByBatchID(batchId);
    }

    public LiveData<Batch> getActiveBatch() {
        return batchRepository.getActiveBatch();
    }

    public void insertBatch(Batch batch) {
        batchRepository.insert(batch);
    }

    public void insertBatchDetail(String batchId, int amount, Setting setting) {
        BatchDetail batchDetail = new BatchDetail();
        batchDetail.batch_id = batchId;
        batchDetail.datetime = new Date();
        batchDetail.amount = amount;
        batchDetail.unit = (setting != null ? setting.unit : "kg");
        batchDetailRepository.insert(batchDetail);
    }

    public void updateBatchDetail(BatchDetail batchDetail) {
        batchDetailRepository.update(batchDetail);
    }

    public void completeBatch(String batchId) {
        batchRepository.completeBatch(batchId);
    }
}

