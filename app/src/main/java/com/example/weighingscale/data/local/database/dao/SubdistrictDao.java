package com.example.weighingscale.data.local.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.example.weighingscale.data.model.Subdistrict;

import java.util.List;

@Dao
public interface SubdistrictDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Subdistrict> subdistricts);
}
