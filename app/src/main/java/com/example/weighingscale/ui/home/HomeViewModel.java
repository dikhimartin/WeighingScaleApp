package com.example.weighingscale.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.model.BatchDetail;
import com.example.weighingscale.data.repository.BatchRepository;
import com.example.weighingscale.data.repository.BatchDetailRepository;

import java.util.Date;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final BatchRepository batchRepository;
    private final BatchDetailRepository batchDetailRepository;
    private LiveData<Batch> activeBatch;
    private LiveData<List<BatchDetail>> batchDetails;


    public HomeViewModel(BatchRepository batchRepository, BatchDetailRepository batchDetailRepository) {
        this.batchRepository = batchRepository;
        this.batchDetailRepository = batchDetailRepository;
        this.activeBatch = batchRepository.getActiveBatch();
//        allNotes = batchDetailRepository.getAllNotes();
    }

//    public LiveData<List<BatchDetail>> getAllNotes() {
//        return allNotes;
//    }

    public LiveData<List<BatchDetail>> getBatchDetails(int batchId) {
        return batchDetailRepository.getBatchDetails(batchId);
    }

    public LiveData<Batch> getActiveBatch() {
        return activeBatch;
    }

    public void insertBatch(Batch batch) {
        batchRepository.insertBatch(batch);
    }

    public void insertBatchDetail(int batchId, double amount, double price) {
        BatchDetail batchDetail = new BatchDetail();
        batchDetail.batchId = batchId;
        batchDetail.datetime = new Date();
        batchDetail.amount = amount;
        batchDetail.price = price;
        batchDetailRepository.insert(batchDetail);
    }

    public void completeBatch(int batchId) {
        batchRepository.completeBatch(batchId);
    }
}
