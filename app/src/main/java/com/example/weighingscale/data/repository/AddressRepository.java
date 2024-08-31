package com.example.weighingscale.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.weighingscale.data.dto.AddressDTO;
import com.example.weighingscale.data.local.database.AppDatabase;
import com.example.weighingscale.data.local.database.dao.CityDao;
import com.example.weighingscale.data.local.database.dao.ProvinceDao;
import com.example.weighingscale.data.model.Province;
import com.example.weighingscale.data.model.City;

import java.util.List;

public class AddressRepository {
    private final ProvinceDao provinceDao;
    private final CityDao cityDao;

    public AddressRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        provinceDao = database.provinceDao();
        cityDao = database.cityDao();
    }

    public LiveData<List<AddressDTO>> getAddress() {
        return cityDao.getDatas();
    }

    public LiveData<List<Province>> getAllProvinces() {
        return provinceDao.getAll();
    }

    public LiveData<List<City>> getAllCities() {
        return cityDao.getAll();
    }

    public LiveData<List<City>> getCitiesByProvinceID(String provinceID) {
        return cityDao.getCitiesByProvinceID(provinceID);
    }
}
