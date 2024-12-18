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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.weighingscale.R;
import com.example.weighingscale.data.model.Setting;
import com.example.weighingscale.ui.shared.SharedAdapter;
import com.example.weighingscale.util.InputDirectiveUtil;
import com.example.weighingscale.util.ValidationUtil;
import com.example.weighingscale.viewmodel.BatchViewModel;
import com.example.weighingscale.viewmodel.SettingViewModel;

public class SettingFragment extends Fragment {

    private SettingViewModel settingViewModel;
    private EditText etPicName, etPicPhoneNumber, etRicePrice;
    private AutoCompleteTextView actvUnit;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        settingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_setting, container, false);

        // Init instance BatchViewModel
        BatchViewModel batchViewModel = new ViewModelProvider(requireActivity()).get(BatchViewModel.class);

        // Initialize views
        etPicName = root.findViewById(R.id.et_pic_name);
        etPicPhoneNumber = root.findViewById(R.id.et_pic_phone_number);
        etRicePrice = root.findViewById(R.id.et_rice_price);
        actvUnit = root.findViewById(R.id.actv_unit);
        Button btnSave = root.findViewById(R.id.btn_save);

        // Apply currency format using InputDirectiveUtil
        InputDirectiveUtil.applyCurrencyFormat(etRicePrice);

        // Setup AutoCompleteTextView with adapter
        settingViewModel.getUnitOptions().observe(getViewLifecycleOwner(), units -> {
            SharedAdapter adapter = new SharedAdapter(requireContext(), units);
            actvUnit.setAdapter(adapter);
        });

        // Observe active batch and disable input if needed
        batchViewModel.getActiveBatch().observe(getViewLifecycleOwner(), batch -> {
            if (batch != null) {
                root.findViewById(R.id.til_unit).setVisibility(View.GONE);
                root.findViewById(R.id.til_rice_price).setVisibility(View.GONE);
                root.findViewById(R.id.tv_disabled_message).setVisibility(View.VISIBLE);
            } else {
                root.findViewById(R.id.til_unit).setVisibility(View.VISIBLE);
                root.findViewById(R.id.til_rice_price).setVisibility(View.VISIBLE);
                root.findViewById(R.id.tv_disabled_message).setVisibility(View.GONE);
            }
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
        String picName = etPicName.getText().toString().trim();
        String picPhoneNumber = etPicPhoneNumber.getText().toString().trim();
        long ricePrice = InputDirectiveUtil.getCurrencyValue(etRicePrice);
        String unitText = actvUnit.getText().toString().trim();
        String UnitValue = settingViewModel.getUnitValue(unitText);

        // Validate and process input
        if (validateInputs(picName, picPhoneNumber, ricePrice, UnitValue)) {
            Setting newSetting = createSetting(picName, picPhoneNumber, ricePrice, UnitValue);
            settingViewModel.insertOrUpdateSetting(newSetting);
            showToast("Pengaturan berhasil diubah");

             // Navigate back to the previous fragment
            NavController navController = NavHostFragment.findNavController(this);
            navController.popBackStack();
        } else {
            showToast("Tolong isi semua inputan");
        }
    }

    private boolean validateInputs(String picName, String picPhoneNumber, long ricePrice, String unit) {
        boolean isValid = true;

        if (ValidationUtil.isValueEmpty(picName)) {
            ValidationUtil.setFieldError(etPicName, requireContext(), R.string.is_required);
            isValid = false;
        } else {
            etPicName.setError(null);
        }

        if (ValidationUtil.isValueEmpty(picPhoneNumber)) {
            ValidationUtil.setFieldError(etPicPhoneNumber, requireContext(), R.string.is_required);
            isValid = false;
        } else {
            etPicPhoneNumber.setError(null);
        }

        if (ricePrice <= 0) {
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

    private Setting createSetting(String picName, String picPhoneNumber, long ricePrice, String unit) {
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

