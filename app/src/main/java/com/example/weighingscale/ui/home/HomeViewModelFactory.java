package com.example.weighingscale.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.weighingscale.data.repository.BatchRepository;
import com.example.weighingscale.data.repository.BatchDetailRepository;


public class HomeViewModelFactory implements ViewModelProvider.Factory {
    private final BatchRepository batchRepository;
    private final BatchDetailRepository batchDetailRepository;

    public HomeViewModelFactory(BatchRepository batchRepository, BatchDetailRepository batchDetailRepository) {
        this.batchRepository = batchRepository;
        this.batchDetailRepository = batchDetailRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(batchRepository, batchDetailRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
