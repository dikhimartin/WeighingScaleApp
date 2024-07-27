package com.example.weighingscale.data.local.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.weighingscale.data.model.Province;

import java.util.List;

@Dao
public interface ProvinceDao {
    @Query("SELECT * FROM Province")
    List<Province> getAllProvinces();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProvince(Province province);
}
