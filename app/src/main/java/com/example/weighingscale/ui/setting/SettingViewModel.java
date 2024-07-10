package com.example.weighingscale.ui.setting;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.weighingscale.data.model.Setting;
import com.example.weighingscale.data.repository.SettingRepository;

public class SettingViewModel extends AndroidViewModel {

    private final SettingRepository settingRepository;
    private final LiveData<Setting> setting;

    public SettingViewModel(Application application) {
        super(application);
        settingRepository = SettingRepository.getInstance(application);
        setting = settingRepository.getSetting();
    }

    public LiveData<Setting> getSetting() {
        return setting;
    }

    public void insertOrUpdateSetting(Setting setting) {
        settingRepository.insertOrUpdateSetting(setting);
    }
}
