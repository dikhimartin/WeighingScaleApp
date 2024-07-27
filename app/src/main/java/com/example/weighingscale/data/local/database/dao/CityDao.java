package com.example.weighingscale.data.local.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.weighingscale.data.model.City;

import java.util.List;

@Dao
public interface CityDao {
    @Query("SELECT * FROM City")
    List<City> getAllCities();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<City> cities);
}
