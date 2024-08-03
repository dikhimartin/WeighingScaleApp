package com.example.weighingscale.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.weighingscale.data.repository.AddressRepository;
import com.example.weighingscale.data.repository.BatchRepository;
import com.example.weighingscale.data.repository.BatchDetailRepository;


public class HomeViewModelFactory implements ViewModelProvider.Factory {
    private final BatchRepository batchRepository;
    private final BatchDetailRepository batchDetailRepository;
    private final AddressRepository addressRepository;

    public HomeViewModelFactory(BatchRepository batchRepository, BatchDetailRepository batchDetailRepository, AddressRepository addressRepository) {
        this.batchRepository = batchRepository;
        this.batchDetailRepository = batchDetailRepository;
        this.addressRepository = addressRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(batchRepository, batchDetailRepository, addressRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
