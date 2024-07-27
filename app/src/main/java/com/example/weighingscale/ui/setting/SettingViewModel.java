package com.example.weighingscale.ui.setting;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weighingscale.data.model.Setting;
import com.example.weighingscale.data.repository.SettingRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingViewModel extends AndroidViewModel {

    private final SettingRepository settingRepository;
    private final LiveData<Setting> setting;
    private final MutableLiveData<List<String>> unitOptions = new MutableLiveData<>();
    private final Map<String, String> unitMap = new HashMap<>();

    public SettingViewModel(Application application) {
        super(application);
        settingRepository = SettingRepository.getInstance(application);
        setting = settingRepository.getSetting();
        loadUnitOptions(); // Initialize unit options
    }

    public LiveData<Setting> getSetting() {
        return setting;
    }

    public void insertOrUpdateSetting(Setting setting) {
        settingRepository.insertOrUpdateSetting(setting);
    }

    public LiveData<List<String>> getUnitOptions() {
        return unitOptions;
    }

    public String getUnitValue(String displayText) {
        return unitMap.get(displayText);
    }

    public String getUnitDisplayText(String value) {
        for (Map.Entry<String, String> entry : unitMap.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return "";
    }

    private void loadUnitOptions() {
        // Define mapping between display text and database values
        unitMap.put("Kilogram (Kg)", "kg");
        unitMap.put("Gram (g)", "g");
        unitMap.put("Milligram (mg)", "mg");

        // Convert keys to a list for the adapter
        List<String> units = Arrays.asList("Kilogram (Kg)", "Gram (g)", "Milligram (mg)");
        unitOptions.setValue(units);
    }
}
