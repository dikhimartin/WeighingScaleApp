package com.example.weighingscale.ui.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.weighingscale.data.dto.BatchDTO;
import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.model.BatchDetail;
import com.example.weighingscale.data.repository.BatchRepository;
import com.example.weighingscale.data.repository.BatchDetailRepository;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel {
    private final BatchRepository batchRepository;
    private final BatchDetailRepository batchDetailRepository;
    private final LiveData<List<BatchDTO>> allBatch;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        batchRepository = new BatchRepository(application);
        batchDetailRepository = new BatchDetailRepository(application);
        allBatch = batchRepository.getDatas();
    }

    public LiveData<List<BatchDTO>> getAllBatch() {
        return allBatch;
    }

    public LiveData<BatchDTO> getBatchByID(String id) {
        return batchRepository.getDataByID(id);
    }

    public LiveData<List<BatchDetail>> getBatchDetails(String id) {
        return batchDetailRepository.getDatasByBatchID(id);
    }

    public void deleteBatch(Batch batch) {
        batchRepository.delete(batch);
    }

    public void deleteBatchByID(String id) {
        batchRepository.deleteByID(id);
    }

    public void deleteBatchByIds(List<String> ids) {
        batchRepository.deleteByIds(ids);
    }

}
