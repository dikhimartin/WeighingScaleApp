package com.example.weighingscale.data.local.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.weighingscale.data.model.Province;

import java.util.List;

@Dao
public interface ProvinceDao {
    @Query("SELECT * FROM Province ORDER BY id DESC")
    LiveData<List<Province>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Province> provinces);
}
