package com.example.weighingscale.ui.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
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
    private AutoCompleteTextView actvUnit;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingViewModel =
                new ViewModelProvider(this).get(SettingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_setting, container, false);

        etPicName = root.findViewById(R.id.et_pic_name);
        etPicPhoneNumber = root.findViewById(R.id.et_pic_phone_number);
        etRicePrice = root.findViewById(R.id.et_rice_price);
        btnSave = root.findViewById(R.id.btn_save);
        actvUnit = root.findViewById(R.id.actv_unit);

        // Setup AutoCompleteTextView with adapter
        settingViewModel.getUnitOptions().observe(getViewLifecycleOwner(), units -> {
            UnitAdapter adapter = new UnitAdapter(requireContext(), units);
            actvUnit.setAdapter(adapter);
        });

        // Observe setting data
        settingViewModel.getSetting().observe(getViewLifecycleOwner(), setting -> {
            if (setting != null) {
                etPicName.setText(setting.picName);
                etPicPhoneNumber.setText(setting.picPhoneNumber);
                etRicePrice.setText(String.valueOf(setting.ricePrice));

                // Autofill unit if available
                String displayText = settingViewModel.getUnitDisplayText(setting.unit);
                actvUnit.setText(displayText, false);
            }
        });

        btnSave.setOnClickListener(view -> saveSetting());

        return root;
    }

    private void saveSetting() {
        String picName = etPicName.getText().toString().trim();
        String picPhoneNumber = etPicPhoneNumber.getText().toString().trim();
        String ricePriceStr = etRicePrice.getText().toString().trim();
        String selectedDisplayText = actvUnit.getText().toString().trim();
        String selectedValue = settingViewModel.getUnitValue(selectedDisplayText);

        if (validateFields(picName, picPhoneNumber, ricePriceStr, selectedValue)) {
            try {
                float ricePrice = Float.parseFloat(ricePriceStr);

                Setting newSetting = new Setting();
                newSetting.picName = picName;
                newSetting.picPhoneNumber = picPhoneNumber;
                newSetting.ricePrice = ricePrice;
                newSetting.unit = selectedValue;

                settingViewModel.insertOrUpdateSetting(newSetting);
                Toast.makeText(requireContext(), "Pengaturan berhasil diubah", Toast.LENGTH_SHORT).show();

                requireActivity().onBackPressed();
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Inputan harga beras harus berupa angka ", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Tolong isi semua inputan", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateFields(String picName, String picPhoneNumber, String ricePriceStr, String unit) {
        return !picName.isEmpty() && !picPhoneNumber.isEmpty() && !ricePriceStr.isEmpty() && !unit.isEmpty();
    }
}

