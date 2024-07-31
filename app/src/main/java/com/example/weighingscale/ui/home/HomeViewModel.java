package com.example.weighingscale.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.model.BatchDetail;
import com.example.weighingscale.data.model.Province;
import com.example.weighingscale.data.model.City;
import com.example.weighingscale.data.model.Setting;
import com.example.weighingscale.data.model.Subdistrict;
import com.example.weighingscale.data.repository.AddressRepository;
import com.example.weighingscale.data.repository.BatchRepository;
import com.example.weighingscale.data.repository.BatchDetailRepository;

import java.util.Date;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final BatchRepository batchRepository;
    private final BatchDetailRepository batchDetailRepository;
    private final AddressRepository addressRepository;
    private LiveData<Batch> activeBatch;

    public HomeViewModel(BatchRepository batchRepository, BatchDetailRepository batchDetailRepository, AddressRepository addressRepository) {
        this.batchRepository = batchRepository;
        this.batchDetailRepository = batchDetailRepository;
        this.addressRepository = addressRepository;
        this.activeBatch = batchRepository.getActiveBatch();
    }

    public LiveData<List<BatchDetail>> getBatchDetails(String batchId) {
        return batchDetailRepository.getBatchDetails(batchId);
    }

    public LiveData<Batch> getActiveBatch() {
        return activeBatch;
    }

    public void insertBatch(Batch batch) {
        batchRepository.insertBatch(batch);
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
        batchDetailRepository.insert(batchDetail);
    }

    public void completeBatch(String batchId) {
        batchRepository.completeBatch(batchId);
    }

    // Address Methods
    public LiveData<List<Province>> getAllProvinces() {
        return addressRepository.getAllProvinces();
    }

    public LiveData<List<City>> getCitiesByProvinceId(String provinceId) {
        return addressRepository.getCitiesByProvinceId(provinceId);
    }

    public LiveData<List<Subdistrict>> getSubdistrictsByCityId(String cityId) {
        return addressRepository.getSubdistrictsByCityId(cityId);
    }

//    public void setSelectedProvinceId(String provinceId) {
//        selectedProvinceId.setValue(provinceId);
//    }
//
//    public void setSelectedCityId(String cityId) {
//        selectedCityId.setValue(cityId);
//    }

}
