package com.example.weighingscale.ui.report;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weighingscale.data.dto.BatchDTO;
import com.example.weighingscale.data.dto.ReportDTO;
import com.example.weighingscale.data.repository.BatchRepository;
import com.example.weighingscale.data.repository.ReportRepository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportViewModel extends AndroidViewModel {
    private final ReportRepository reportRepository;
    private final BatchRepository batchRepository;
    private LiveData<ReportDTO> reportData;

    private final MutableLiveData<Map<String, Object>> filters = new MutableLiveData<>(new HashMap<>());

    public ReportViewModel(@NonNull Application application) {
        super(application);
        reportRepository = new ReportRepository(application);
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

    public LiveData<List<BatchDTO>> getAllBatch(@Nullable String searchQuery, @Nullable Date startDate, @Nullable Date endDate, @Nullable String sortOrder) {
        if (searchQuery != null && searchQuery.trim().isEmpty()) {
            searchQuery = null;
        }
        return batchRepository.getDatas(searchQuery, startDate, endDate, sortOrder);
    }

}
