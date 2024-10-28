package com.example.weighingscale.ui.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weighingscale.data.dto.BatchDTO;
import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.model.BatchDetail;
import com.example.weighingscale.data.repository.BatchRepository;
import com.example.weighingscale.data.repository.BatchDetailRepository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryViewModel extends AndroidViewModel {
    private final MutableLiveData<Map<String, Object>> filters = new MutableLiveData<>(new HashMap<>());

    public HistoryViewModel(@NonNull Application application) {
        super(application);
    }

    public void setFilter(String key, Object value) {
        Map<String, Object> currentFilters = filters.getValue();
        if (currentFilters != null) {
            currentFilters.put(key, value);
            filters.setValue(currentFilters);
        }
    }

    public Object getFilter(String key) {
        Map<String, Object> currentFilters = filters.getValue();
        return (currentFilters != null) ? currentFilters.get(key) : null;
    }
}
