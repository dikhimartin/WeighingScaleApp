package com.example.weighingscale.ui.shared;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.weighingscale.data.model.City;
import com.example.weighingscale.data.model.Province;
import com.example.weighingscale.data.repository.AddressRepository;

import java.util.List;

public class LocationViewModel extends AndroidViewModel {
    private final AddressRepository addressRepository;

    private final MutableLiveData<String> selectedProvinceId = new MutableLiveData<>();
    private final LiveData<List<Province>> provinces;
    private final LiveData<List<City>> cities;

    public LocationViewModel(@NonNull Application application) {
        super(application);
        addressRepository = new AddressRepository(application);
        provinces = addressRepository.getAllProvinces();
        cities = Transformations.switchMap(selectedProvinceId, id -> addressRepository.getCitiesByProvinceID(id));
    }

    public LiveData<List<Province>> getProvinces() {
        return provinces;
    }

    public LiveData<List<City>> getCities() {
        return cities;
    }

    public void setSelectedProvinceId(String provinceId) {
        selectedProvinceId.setValue(provinceId);
    }
}

