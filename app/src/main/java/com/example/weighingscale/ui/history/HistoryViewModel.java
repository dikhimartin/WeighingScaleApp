package com.example.weighingscale.ui.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.model.Note;
import com.example.weighingscale.data.repository.BatchRepository;
import com.example.weighingscale.data.repository.NoteRepository;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel {
    private BatchRepository batchRepository;
    private LiveData<List<Batch>> allBatch;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        batchRepository = new BatchRepository(application);
        allBatch = batchRepository.getDatas();
    }

    public LiveData<List<Batch>> getAllBatch() {
        return allBatch;
    }

    public LiveData<Batch> getBatchById(String id) {
        return batchRepository.getDataById(id);
    }

    public void deleteBatch(Batch batch) {
        batchRepository.delete(batch);
    }

    public void deleteAllBatch() {
        batchRepository.deleteAll();
    }

    public void deleteBatchByIds(List<String> ids) {
        batchRepository.deleteByIds(ids);
    }

}
