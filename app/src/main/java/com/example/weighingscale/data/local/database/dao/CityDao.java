package com.example.weighingscale.data.local.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.weighingscale.data.model.BatchDetail;
import com.example.weighingscale.data.model.City;
import com.example.weighingscale.data.model.Province;

import java.util.List;

@Dao
public interface CityDao {
    @Query("SELECT * FROM City WHERE province_id = :provinceId")
    LiveData<List<City>> getCitiesByProvinceId(String provinceId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<City> cities);
}
