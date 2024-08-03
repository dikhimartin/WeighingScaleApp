package com.example.weighingscale.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import com.example.weighingscale.data.local.database.AppDatabase;
import com.example.weighingscale.data.local.database.dao.CityDao;
import com.example.weighingscale.data.local.database.dao.ProvinceDao;
import com.example.weighingscale.data.local.database.dao.SubdistrictDao;
import com.example.weighingscale.data.model.Province;
import com.example.weighingscale.data.model.City;
import com.example.weighingscale.data.model.Subdistrict;

import java.util.List;

public class AddressRepository {
    private ProvinceDao provinceDao;
    private CityDao cityDao;
    private SubdistrictDao subdistrictDao;

    public AddressRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        provinceDao = database.provinceDao();
        cityDao = database.cityDao();
        subdistrictDao = database.subdistrictDao();
    }

    public LiveData<List<Province>> getAllProvinces() {
        return provinceDao.getAll();
    }

    public LiveData<List<City>> getCitiesByProvinceId(String provinceId) {
        return cityDao.getCitiesByProvinceId(provinceId);
    }

    public LiveData<List<Subdistrict>> getSubdistrictsByCityId(String cityId) {
        return subdistrictDao.getSubdistrictsByCityId(cityId);
    }

}
