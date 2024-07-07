package com.example.weighingscale.ui.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.weighingscale.R;
import com.example.weighingscale.data.model.Setting;

public class SettingFragment extends Fragment {

    private SettingViewModel settingViewModel;
    private EditText etPicName, etPicPhoneNumber, etRicePrice;
    private Button btnSave;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingViewModel =
                new ViewModelProvider(this).get(SettingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_setting, container, false);

        etPicName = root.findViewById(R.id.et_pic_name);
        etPicPhoneNumber = root.findViewById(R.id.et_pic_phone_number);
        etRicePrice = root.findViewById(R.id.et_rice_price);
        btnSave = root.findViewById(R.id.btn_save);

        settingViewModel.getSetting().observe(getViewLifecycleOwner(), setting -> {
            if (setting != null) {
                etPicName.setText(setting.picName);
                etPicPhoneNumber.setText(setting.picPhoneNumber);
                etRicePrice.setText(String.valueOf(setting.ricePrice));
            }
        });

        btnSave.setOnClickListener(view -> saveSetting());

        return root;
    }

    private void saveSetting() {
        String picName = etPicName.getText().toString().trim();
        String picPhoneNumber = etPicPhoneNumber.getText().toString().trim();
        float ricePrice = Float.parseFloat(etRicePrice.getText().toString().trim());

        if (validateFields(picName, picPhoneNumber)) {
            Setting newSetting = new Setting();
            newSetting.id = 1; // Assuming there's only one row in Setting table
            newSetting.picName = picName;
            newSetting.picPhoneNumber = picPhoneNumber;
            newSetting.ricePrice = ricePrice;

            settingViewModel.updateSetting(newSetting);
            Toast.makeText(requireContext(), "Settings updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateFields(String picName, String picPhoneNumber) {
        return !picName.isEmpty() && !picPhoneNumber.isEmpty();
    }
}
