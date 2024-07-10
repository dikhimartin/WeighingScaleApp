package com.example.weighingscale.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.weighingscale.data.repository.BatchRepository;

public class HomeViewModelFactory implements ViewModelProvider.Factory {
    private final BatchRepository batchRepository;

    public HomeViewModelFactory(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(batchRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
