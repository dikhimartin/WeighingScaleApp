package com.example.weighingscale.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.weighingscale.data.local.database.AppDatabase;
import com.example.weighingscale.data.local.database.dao.SettingDao;
import com.example.weighingscale.data.model.Setting;

import java.util.concurrent.ExecutorService;

public class SettingRepository {

    private final SettingDao settingDao;
    private final ExecutorService executor;

    private SettingRepository(SettingDao settingDao, ExecutorService executor) {
        this.settingDao = settingDao;
        this.executor = executor;
    }

    public static SettingRepository getInstance(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        return new SettingRepository(db.settingDao(), AppDatabase.databaseWriteExecutor);
    }

    public LiveData<Setting> getSetting() {
        return settingDao.getSetting();
    }

    public void updateSetting(Setting setting) {
        executor.execute(() -> settingDao.updateSetting(setting));
    }

    public void updateSettingFields(int id, String picName, String picPhoneNumber, float ricePrice) {
        executor.execute(() -> {
            int rowsAffected = settingDao.updateSettingFields(id, picName, picPhoneNumber, ricePrice);
            // Check if update was successful
            if (rowsAffected > 0) {
                // Success
            } else {
                // Failure
            }
        });
    }
}
