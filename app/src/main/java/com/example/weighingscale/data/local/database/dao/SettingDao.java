package com.example.weighingscale.data.local.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.weighingscale.data.model.Setting;

@Dao
public interface SettingDao {
    @Query("SELECT * FROM Setting LIMIT 1")
    LiveData<Setting> getSetting();

    @Update
    void updateSetting(Setting setting);
}
