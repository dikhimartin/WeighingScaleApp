package com.example.weighingscale.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.weighingscale.data.local.database.AppDatabase;
import com.example.weighingscale.data.local.database.dao.SettingDao;
import com.example.weighingscale.data.model.Setting;

public class SettingRepository {

    private final SettingDao settingDao;

    public SettingRepository(Application application){
        AppDatabase database = AppDatabase.getInstance(application);
        settingDao = database.settingDao();
    }

    public LiveData<Setting> getSetting(){
        return settingDao.getSetting();
    }

    public void insertOrUpdateSetting(Setting setting){
        AppDatabase.databaseWriteExecutor.execute(() -> {
            settingDao.insertOrUpdateSetting(setting);
        });
    }
}
