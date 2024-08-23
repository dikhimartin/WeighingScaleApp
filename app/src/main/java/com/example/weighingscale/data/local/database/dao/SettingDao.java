package com.example.weighingscale.data.local.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.weighingscale.data.model.Setting;

@Dao
public interface SettingDao {

    @Query("SELECT * FROM Setting LIMIT 1")
    LiveData<Setting> getSetting();

    @Query("DELETE FROM Setting")
    void deleteAllSettings();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSetting(Setting setting);

    @Transaction
    default void insertOrUpdateSetting(Setting setting) {
        deleteAllSettings();
        insertSetting(setting);
    }
}
