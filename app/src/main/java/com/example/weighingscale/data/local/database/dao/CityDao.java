package com.example.weighingscale.data.local.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.weighingscale.data.dto.AddressDTO;
import com.example.weighingscale.data.model.BatchDetail;
import com.example.weighingscale.data.model.City;
import com.example.weighingscale.data.model.Province;

import java.util.List;

@Dao
public interface CityDao {

    @Query(
        "SELECT " +
        "    c.id AS id, " +
        "    c.province_id AS province_id, " +
        "    c.type AS city_type, " +
        "    c.name AS city_name, " +
        "    p.name AS province_name " +
        "FROM City c " +
        "LEFT JOIN Province p ON c.province_id = p.id"
    )
    LiveData<List<AddressDTO>> getDatas();

    @Query("SELECT * FROM City ORDER BY id DESC")
    LiveData<List<City>> getAll();

    @Query("SELECT * FROM City WHERE province_id = :provinceID")
    LiveData<List<City>> getCitiesByProvinceID(String provinceID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<City> cities);
}
