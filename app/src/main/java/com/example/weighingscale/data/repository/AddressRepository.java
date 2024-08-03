package com.example.weighingscale.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.weighingscale.data.local.database.AppDatabase;
import com.example.weighingscale.data.local.database.dao.BatchDetailDao;
import com.example.weighingscale.data.local.database.dao.ProvinceDao;
import com.example.weighingscale.data.local.database.dao.CityDao;
import com.example.weighingscale.data.local.database.dao.SubdistrictDao;
import com.example.weighingscale.data.model.Province;
import com.example.weighingscale.data.model.City;
import com.example.weighingscale.data.model.Subdistrict;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class AddressRepository {

    private final ProvinceDao provinceDao;
    private final CityDao cityDao;
    private final SubdistrictDao subdistrictDao;
    private final ExecutorService executor;

    public AddressRepository(ProvinceDao provinceDao, CityDao cityDao, SubdistrictDao subdistrictDao, ExecutorService executor) {
        this.provinceDao = provinceDao;
        this.cityDao = cityDao;
        this.subdistrictDao = subdistrictDao;
        this.executor = executor;
    }

    public static AddressRepository getInstance(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        return new AddressRepository(db.provinceDao(), db.cityDao(), db.subdistrictDao(), AppDatabase.databaseWriteExecutor);
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
