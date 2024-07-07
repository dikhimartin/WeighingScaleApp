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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSetting(Setting setting);

    @Update
    void updateSetting(Setting setting);

    @Query("UPDATE Setting SET picName = :picName, picPhoneNumber = :picPhoneNumber, ricePrice = :ricePrice WHERE id = :id")
    int updateSettingFields(int id, String picName, String picPhoneNumber, float ricePrice);
}
