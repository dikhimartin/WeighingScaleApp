package com.example.weighingscale.ui.shared;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.weighingscale.data.dto.AddressDTO;
import com.example.weighingscale.data.model.City;
import com.example.weighingscale.data.model.Province;
import com.example.weighingscale.data.repository.AddressRepository;

import java.util.List;

public class LocationViewModel extends AndroidViewModel {
    private static AddressRepository addressRepository = null;

    public LocationViewModel(@NonNull Application application) {
        super(application);
        addressRepository = new AddressRepository(application);
    }

    // Address Methods
    public LiveData<List<AddressDTO>> getAddress() {
        return addressRepository.getAddress();
    }    

    public LiveData<List<Province>> getAllProvinces() {
        return addressRepository.getAllProvinces();
    }

    public LiveData<List<City>> getAllCities() {
        return addressRepository.getAllCities();
    }

    public static LiveData<List<City>> getCitiesByProvinceID(String provinceID) {
        return addressRepository.getCitiesByProvinceID(provinceID);
    }
}

