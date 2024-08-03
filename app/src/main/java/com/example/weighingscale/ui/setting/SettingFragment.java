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
import com.example.weighingscale.ui.shared.SharedAdapter;
import com.example.weighingscale.util.ValidationUtil;

public class SettingFragment extends Fragment {

    private SettingViewModel settingViewModel;
    private EditText etPicName, etPicPhoneNumber, etRicePrice;
    private AutoCompleteTextView actvUnit;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingViewModel =
                new ViewModelProvider(this).get(SettingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_setting, container, false);

        etPicName = root.findViewById(R.id.et_pic_name);
        etPicPhoneNumber = root.findViewById(R.id.et_pic_phone_number);
        etRicePrice = root.findViewById(R.id.et_rice_price);
        Button btnSave = root.findViewById(R.id.btn_save);
        actvUnit = root.findViewById(R.id.actv_unit);

        // Setup AutoCompleteTextView with adapter
        settingViewModel.getUnitOptions().observe(getViewLifecycleOwner(), units -> {
            SharedAdapter adapter = new SharedAdapter(requireContext(), units);
            actvUnit.setAdapter(adapter);
        });

        // Observe setting data
        settingViewModel.getSetting().observe(getViewLifecycleOwner(), setting -> {
            if (setting != null) {
                etPicName.setText(setting.pic_name);
                etPicPhoneNumber.setText(setting.pic_phone_number);
                etRicePrice.setText(String.valueOf(setting.rice_price));

                // Autofill unit if available
                String displayText = settingViewModel.getUnitDisplayText(setting.unit);
                actvUnit.setText(displayText, false);
            }
        });

        btnSave.setOnClickListener(view -> saveSetting());

        return root;
    }

    private void saveSetting() {
        // Get input values
        String picName = etPicName.getText().toString().trim();
        String picPhoneNumber = etPicPhoneNumber.getText().toString().trim();
        String ricePriceStr = etRicePrice.getText().toString().trim();
        String selectedDisplayText = actvUnit.getText().toString().trim();
        String selectedValue = settingViewModel.getUnitValue(selectedDisplayText);

        // Validate and process input
        if (validateInputs(picName, picPhoneNumber, ricePriceStr, selectedValue)) {
            try {
                float ricePrice = Float.parseFloat(ricePriceStr);
                Setting newSetting = createSetting(picName, picPhoneNumber, ricePrice, selectedValue);
                settingViewModel.insertOrUpdateSetting(newSetting);
                showToast("Pengaturan berhasil diubah");
                requireActivity().onBackPressed();
            } catch (NumberFormatException e) {
                showToast("Inputan harga beras harus berupa angka");
            }
        } else {
            showToast("Tolong isi semua inputan");
        }
    }

    private boolean validateInputs(String picName, String picPhoneNumber, String ricePriceStr, String unit) {
        boolean isValid = true;

        if (ValidationUtil.isFieldEmpty(picName)) {
            ValidationUtil.setFieldError(etPicName, requireContext(), R.string.is_required);
            isValid = false;
        } else {
            etPicName.setError(null);
        }

        if (ValidationUtil.isFieldEmpty(picPhoneNumber)) {
            ValidationUtil.setFieldError(etPicPhoneNumber, requireContext(), R.string.is_required);
            isValid = false;
        } else {
            etPicPhoneNumber.setError(null);
        }

        if (ValidationUtil.isFieldEmpty(ricePriceStr) || !ValidationUtil.isNumeric(ricePriceStr)) {
            ValidationUtil.setFieldError(etRicePrice, requireContext(), R.string.is_required);
            isValid = false;
        } else {
            etRicePrice.setError(null);
        }

        if (!settingViewModel.isValidUnit(unit)) {
            ValidationUtil.setFieldError(actvUnit, requireContext(), R.string.is_required);
            isValid = false;
        } else {
            actvUnit.setError(null);
        }

        return isValid;
    }

    private Setting createSetting(String picName, String picPhoneNumber, float ricePrice, String unit) {
        Setting setting = new Setting();
        setting.pic_name = picName;
        setting.pic_phone_number = picPhoneNumber;
        setting.rice_price = ricePrice;
        setting.unit = unit;
        return setting;
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

}

