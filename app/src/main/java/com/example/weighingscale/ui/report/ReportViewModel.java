package com.example.weighingscale.ui.report;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.weighingscale.data.repository.BatchRepository;
import java.util.HashMap;
import java.util.Map;

public class ReportViewModel extends AndroidViewModel {
    private final BatchRepository batchRepository;
    private final MutableLiveData<Map<String, Object>> filters = new MutableLiveData<>(new HashMap<>());

    public ReportViewModel(@NonNull Application application) {
        super(application);
        batchRepository = new BatchRepository(application);
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
