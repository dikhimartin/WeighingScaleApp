package com.example.weighingscale.data.local.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.weighingscale.R;
import com.example.weighingscale.data.model.City;
import com.example.weighingscale.data.model.Province;
import com.example.weighingscale.data.model.Subdistrict;
import com.example.weighingscale.data.local.database.dao.CityDao;
import com.example.weighingscale.data.local.database.dao.ProvinceDao;
import com.example.weighingscale.data.local.database.dao.SubdistrictDao;
import com.example.weighingscale.util.JsonUtils;

import java.util.List;
import java.util.concurrent.Executors;

public class Seeder {
    private final Context context;
    private final ProvinceDao provinceDao;
    private final CityDao cityDao;
    private final SubdistrictDao subdistrictDao;

    public Seeder(@NonNull Context context) {
        this.context = context;
        AppDatabase database = AppDatabase.getInstance(context);
        this.provinceDao = database.provinceDao();
        this.cityDao = database.cityDao();
        this.subdistrictDao = database.subdistrictDao();
    }

    public void seedDatabase() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Parse JSON data
            List<Province> provinces = JsonUtils.parseJson(context, R.raw.province, Province[].class);
            List<City> cities = JsonUtils.parseJson(context, R.raw.city, City[].class);
            List<Subdistrict> subdistricts = JsonUtils.parseJson(context, R.raw.subdistrict, Subdistrict[].class);

            // Log the size of lists to verify data parsing
            Log.d("Seeder", "Provinces count: " + (provinces != null ? provinces.size() : 0));
            Log.d("Seeder", "Cities count: " + (cities != null ? cities.size() : 0));
            Log.d("Seeder", "Subdistricts count: " + (subdistricts != null ? subdistricts.size() : 0));

            // Insert data with duplicate check
            insertWithCheck(provinces, provinceDao::insertAll);
            insertWithCheck(cities, cityDao::insertAll);
            insertWithCheck(subdistricts, subdistrictDao::insertAll);

            // Log completion of seeding
            Log.d("Seeder", "Database seeding completed.");
        });
    }

    private <T> void insertWithCheck(List<T> items, DaoInsertFunction<T> insertFunction) {
        // Perform duplicate check and insert
        for (T item : items) {
            insertFunction.insertAll(List.of(item));
        }
    }

    @FunctionalInterface
    private interface DaoInsertFunction<T> {
        void insertAll(List<T> items);
    }
}
