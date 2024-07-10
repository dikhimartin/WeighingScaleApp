package com.example.weighingscale.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.repository.BatchRepository;

public class HomeViewModel extends ViewModel {

    private final BatchRepository batchRepository;
    private LiveData<Batch> activeBatch;

    public HomeViewModel(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
        activeBatch = batchRepository.getActiveBatch();
    }

    public LiveData<Batch> getActiveBatch() {
        return activeBatch;
    }

    public void insertBatch(Batch batch) {
        batchRepository.insertBatch(batch);
    }

    public void completeBatch(int batchId) {
        batchRepository.completeBatch(batchId);
    }
}
