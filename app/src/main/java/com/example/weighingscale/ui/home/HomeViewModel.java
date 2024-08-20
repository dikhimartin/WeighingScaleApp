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
    private BatchRepository batchRepository;
    private BatchDetailRepository batchDetailRepository;
    private AddressRepository addressRepository;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        batchRepository = new BatchRepository(application);
        batchDetailRepository = new BatchDetailRepository(application);
        addressRepository = new AddressRepository(application);
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
        double price = 0.0;
        String unit = "kg";
        if (setting != null) {
            double ricePrice = setting.rice_price;
            price = amount * ricePrice;
            unit = setting.unit;
        }
        batchDetail.batch_id = batchId;
        batchDetail.datetime = new Date();
        batchDetail.amount = amount;
        batchDetail.unit = unit;
        batchDetail.price = price;
        AppDatabase.databaseWriteExecutor.execute(() -> {
            batchDetailRepository.insert(batchDetail);
        });
    }

    public void completeBatch(String batchId) {
        batchRepository.completeBatch(batchId);
    }

    // Address Methods
    public LiveData<List<Province>> getAllProvinces() {
        return addressRepository.getAllProvinces();
    }

    public LiveData<List<City>> getCitiesByProvinceId(String provinceId) {
        return addressRepository.getCitiesByProvinceID(provinceId);
    }
}

