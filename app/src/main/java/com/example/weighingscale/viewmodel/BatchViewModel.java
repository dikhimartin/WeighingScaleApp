package com.example.weighingscale.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.weighingscale.data.dto.BatchDTO;
import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.repository.BatchRepository;

import java.util.Date;
import java.util.List;

public class BatchViewModel extends AndroidViewModel {
    private final BatchRepository batchRepository;

    public BatchViewModel (@NonNull Application application){
        super(application);
        batchRepository =  new BatchRepository(application);
    }

    public void getAllBatch(BatchRepository.Callback<List<Batch>> callback) {
        batchRepository.getAllBatch(callback);
    }

    public LiveData<List<BatchDTO>> getBatch(@Nullable String searchQuery, @Nullable Date startDate, @Nullable Date endDate, @Nullable String sortOrder) {
        if (searchQuery != null && searchQuery.trim().isEmpty()) {
            searchQuery = null;
        }
        return batchRepository.getDatas(searchQuery, startDate, endDate, sortOrder);
    }

   public LiveData<BatchDTO> getBatchByID(String id) {
        return batchRepository.getDataByID(id);
    }

    public LiveData<Batch> getActiveBatch() {
        return batchRepository.getActiveBatch();
    }

    public void insert(Batch batch) {
        batchRepository.insert(batch);
    }

    public void importData(Batch batch) {
        batchRepository.insert(batch);
    }
    
    public void update(Batch batch) {
        batchRepository.update(batch);
    }

    public void delete(Batch batch) {
        batchRepository.delete(batch);
    }

    public void deleteByIds(List<String> ids) {
        batchRepository.deleteByIds(ids);
    }

    public void completeBatch(String batchId) {
        batchRepository.completeBatch(batchId);
    }
}
