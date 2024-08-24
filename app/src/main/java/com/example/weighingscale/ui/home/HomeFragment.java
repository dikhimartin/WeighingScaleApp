package com.example.weighingscale.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weighingscale.R;
import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.model.BatchDetail;
import com.example.weighingscale.data.model.City;
import com.example.weighingscale.data.model.Province;
import com.example.weighingscale.data.model.Setting;
import com.example.weighingscale.databinding.FragmentHomeBinding;
import com.example.weighingscale.state.StateConnecting;
import com.example.weighingscale.ui.setting.SettingViewModel;
import com.example.weighingscale.ui.shared.EntityAdapter;
import com.example.weighingscale.ui.shared.LocationViewModel;
import com.example.weighingscale.ui.shared.SelectOptionWrapper;
import com.example.weighingscale.ui.shared.SharedViewModel;
import com.example.weighingscale.util.DateTimeUtil;
import com.example.weighingscale.util.FormatterUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private SharedViewModel sharedViewModel;
    private SettingViewModel settingViewModel;
    private LocationViewModel locationViewModel;
    private LogAdapter adapter;

    private String currentBatchID = null;
    private Setting currentSetting;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Init instance HomeViewModel
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        // Init instance SharedViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Init instance settingViewModel
        settingViewModel = new ViewModelProvider(requireActivity()).get(SettingViewModel.class);

        // Init instance locationViewModel
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        // Observer Log List
        setupRecyclerView(root);

        // Observe Bluetooth status
        sharedViewModel.getBluetoothStatus().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer status) {
                if (status != null && status == StateConnecting.BLUETOOTH_CONNECTED) {
                    enable_auto_mode();
                    sharedViewModel.getWeight().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer weight) {
                            binding.textAmount.setText(String.valueOf(weight));
                        }
                    });
                } else {
                    enable_manual_mode();
                }
            }
        });

        // Set up button listeners
        binding.buttonSaveLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLog();
            }
        });

        binding.buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentBatchID != null) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Selesaikan batch muatan")
                            .setMessage("Apakah kamu yakin ingin menyelesaikan batch muatan ini ?")
                            .setPositiveButton(R.string.yes, (dialog, which) -> {
                                homeViewModel.completeBatch(currentBatchID);
                                Toast.makeText(requireContext(), "Batch muatan sudah selesai", Toast.LENGTH_SHORT).show();
                                navigateToHistory();
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                }else{
                    Toast.makeText(requireContext(), "Tidak ada batch muatan yang aktif", Toast.LENGTH_SHORT).show();
                    showBatchInputDialog();
                }
            }
        });

        return root;
    }

    @SuppressLint("SetTextI18n")
    private void setupRecyclerView(View root) {
         // Adapter log list
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_log);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        adapter = new LogAdapter(requireContext());
        recyclerView.setAdapter(adapter);

        // Observe active batch
        homeViewModel.getActiveBatch().observe(getViewLifecycleOwner(), batch -> {
            if (batch != null) {
                    currentBatchID = batch.id;
                    active_batch();
                    binding.sectionMode.setVisibility(View.VISIBLE);
                    homeViewModel.getBatchDetails(currentBatchID).observe(getViewLifecycleOwner(), data -> {
                        adapter.submitList(data);

                        if (data != null && !data.isEmpty()) {
                            recyclerView.smoothScrollToPosition(0);

                            // Calculate total weight
                            int totalWeight = 0;
                            for (BatchDetail detail : data) {
                                totalWeight += detail.getAmount();
                            }
                            String totalWeightText = String.format(Locale.getDefault(), "%d %s", totalWeight, currentSetting != null ? currentSetting.unit : "Kg");
                            binding.textTotalWeight.setText(totalWeightText);

                            // Update item count
                            String itemCountText = String.format(Locale.getDefault(), "%d karung (sak)", data.size());
                            binding.textTotalItems.setText(itemCountText);

                            binding.cardTotal.setVisibility(View.VISIBLE);
                            binding.finishButtonGroup.setVisibility(View.VISIBLE);
                        } else {
                            binding.cardTotal.setVisibility(View.GONE);
                            binding.finishButtonGroup.setVisibility(View.GONE);
                            binding.textTotalItems.setText("0 karung (sak)");
                        }
                    });
            } else {
                clearBatchDetails();
                showBatchInputDialog();
                inactive_batch();
            }
        });

        adapter.setOnItemClickListener(new LogAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BatchDetail batchDetail) {
                showLogInputDialog(batchDetail);
            }
        });
    }

    private void saveLog() {
        if (currentBatchID == null) {
            showBatchInputDialog();
            Toast.makeText(requireContext(), "Tidak ada batch muatan yang aktif", Toast.LENGTH_SHORT).show();
            return;
        }

        String amountText = (sharedViewModel.getBluetoothStatus().getValue() != null &&
                            sharedViewModel.getBluetoothStatus().getValue() == StateConnecting.BLUETOOTH_CONNECTED) ?
                            binding.textAmount.getText().toString() :
                            binding.editAmount.getText().toString();

        try {
            int amount = FormatterUtil.sanitizeAndConvertToInteger(amountText);
            if (amount <= 0) throw new NumberFormatException();

            homeViewModel.insertBatchDetail(currentBatchID, amount, currentSetting);
            Toast.makeText(requireContext(), "Log sudah disimpan", Toast.LENGTH_SHORT).show();
            resetAmount();
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Nilai yang kamu input tidak valid", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetAmount() {
        Integer bluetoothStatus = sharedViewModel.getBluetoothStatus().getValue();
        if (bluetoothStatus != null && bluetoothStatus == StateConnecting.BLUETOOTH_CONNECTED) {
            sharedViewModel.setWeight(0);
        } else {
            binding.textAmount.setText("0");
            binding.editAmount.setText("");
        }
    }

    @SuppressLint("SetTextI18n")
    private void showLogInputDialog(BatchDetail batchDetail) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input_log, null);

        TextView etAmount = dialogView.findViewById(R.id.et_amount);
        TextView etDateTime = dialogView.findViewById(R.id.til_datetime);

        etAmount.setText(String.valueOf(batchDetail.getAmount()));
        String formattedDate = DateTimeUtil.formatDateTime(batchDetail.getDatetime(), "dd/MM/yyyy HH:mm");
        etDateTime.setText("Tanggal :"+ " " + formattedDate);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView)
                .setTitle("Mengubah nilai log")
                .setPositiveButton("Ubah", (dialog, id) -> {
                    int amount = FormatterUtil.sanitizeAndConvertToInteger(etAmount.getText().toString());
                    if (amount <= 0) throw new NumberFormatException();

                    // Update Batch Detail
                    batchDetail.amount = amount;
                    homeViewModel.updateBatchDetail(batchDetail);

                    Toast.makeText(requireContext(), "Nilai telah diubah", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Batal", (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showBatchInputDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input_batch, null);

        TextView editPicName = dialogView.findViewById(R.id.et_pic_name);
        TextView editPicPhoneNumber = dialogView.findViewById(R.id.et_pic_phone_number);
        TextView editTruckDriver = dialogView.findViewById(R.id.et_truck_driver);
        TextView editTruckDriverPhoneNumber = dialogView.findViewById(R.id.et_truck_driver_phone_number);
        TextView tvDateTime = dialogView.findViewById(R.id.tv_datetime);
        AutoCompleteTextView selectLocProvince = dialogView.findViewById(R.id.select_weighing_location_province);
        AutoCompleteTextView selectLocCity = dialogView.findViewById(R.id.select_weighing_location_city);
        AutoCompleteTextView selectDestProvince = dialogView.findViewById(R.id.select_destination_province);
        AutoCompleteTextView selectDestCity = dialogView.findViewById(R.id.select_destination_city);

        // Autofill value from setting
        observeSetting(() -> {
            editPicName.setText(currentSetting.pic_name);
            editPicPhoneNumber.setText(currentSetting.pic_phone_number);
        });

        // Setup AutoCompleteTextView Weighing Location with adapter
        setupOptionLocation(selectLocProvince, selectLocCity);

        // Setup AutoCompleteTextView Destination with adapter
        setupOptionLocation(selectDestProvince, selectDestCity);

        // Set up datetime picker
        tvDateTime.setOnClickListener(view -> DateTimeUtil.showDateTimePicker(getChildFragmentManager(), tvDateTime));

        // Variable to store selected city's ID
        final String[] deliveryDestinationID = new String[1];
        selectDestCity.setOnItemClickListener((parent, view, position, id) -> {
            SelectOptionWrapper selectedCity = (SelectOptionWrapper) parent.getAdapter().getItem(position);
            if (selectedCity != null) {
                deliveryDestinationID[0] = selectedCity.getId();
            }
        });

        final String[] weighingLocationID = new String[1];
        selectLocCity.setOnItemClickListener((parent, view, position, id) -> {
            SelectOptionWrapper selectedCity = (SelectOptionWrapper) parent.getAdapter().getItem(position);
            if (selectedCity != null) {
                weighingLocationID[0] = selectedCity.getId();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView)
                .setTitle("Masukan data batch muatan")
                .setPositiveButton("Simpan", (dialog, id) -> {
                    String picName = editPicName.getText().toString();
                    String picPhoneNumber = editPicPhoneNumber.getText().toString();
                    String truckDriver = editTruckDriver.getText().toString();
                    String truckDriverPhoneNumber = editTruckDriverPhoneNumber.getText().toString();
                    String dateTime = tvDateTime.getText().toString();

                    Batch batch = new Batch();
                    batch.pic_name = picName;
                    batch.pic_phone_number = picPhoneNumber;
                    batch.datetime = DateTimeUtil.parseDateTime(dateTime);
                    batch.start_date = DateTimeUtil.parseDateTime(dateTime);
                    batch.truck_driver_name = truckDriver;
                    batch.truck_driver_phone_number = truckDriverPhoneNumber;
                    batch.unit = (currentSetting != null ? currentSetting.unit : "kg");
                    batch.rice_price = (currentSetting != null ? currentSetting.rice_price : 0);

                    if (deliveryDestinationID[0] != null) {
                        batch.delivery_destination_id = deliveryDestinationID[0];
                    }
                    if (weighingLocationID[0] != null) {
                        batch.weighing_location_id = weighingLocationID[0];
                    }
                    homeViewModel.insertBatch(batch);
                    Toast.makeText(requireContext(), "Batch muatan sudah aktif", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Batal", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupOptionLocation(AutoCompleteTextView selectProvince, AutoCompleteTextView selectCity) {
        locationViewModel.getAllProvinces().observe(getViewLifecycleOwner(), provinces -> {
            if (provinces != null && !provinces.isEmpty()) {
                List<SelectOptionWrapper> provinceWrappers = new ArrayList<>();
                for (Province province : provinces) {
                    provinceWrappers.add(new SelectOptionWrapper(province.getID(), province.getName()));
                }
                EntityAdapter provinceAdapter = new EntityAdapter(requireContext(), provinceWrappers);
                selectProvince.setAdapter(provinceAdapter);

                selectProvince.setOnItemClickListener((parent, view, position, id) -> {
                    SelectOptionWrapper selectedWrapper = (SelectOptionWrapper) parent.getAdapter().getItem(position);
                    if (selectedWrapper != null) {
                        LocationViewModel.getCitiesByProvinceId(selectedWrapper.getId()).observe(getViewLifecycleOwner(), cities -> {
                            if (cities != null && !cities.isEmpty()) {
                                List<SelectOptionWrapper> cityWrappers = new ArrayList<>();
                                for (City city : cities) {
                                    cityWrappers.add(new SelectOptionWrapper(city.getID(), city.getType() + " " + city.getName()));
                                }
                                EntityAdapter cityAdapter = new EntityAdapter(requireContext(), cityWrappers);
                                selectCity.setAdapter(cityAdapter);
                            }
                        });
                    }
                });
            }
        });
    }

    private void observeSetting(Runnable callback) {
        settingViewModel.getSetting().observe(getViewLifecycleOwner(), setting -> {
            if (setting != null) {
                currentSetting = setting;
                binding.textUnit.setText(setting.unit);
                if (callback != null) {
                    callback.run();
                }
            }
        });
    }

    private void navigateToHistory(){
         // Set up NavOptions to clear back stack
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.navigation_home, true)  // Clear the back stack up to homeFragment
                .build();

        // Navigate to HistoryFragment
        NavController navController = NavHostFragment.findNavController(HomeFragment.this);
        navController.navigate(R.id.action_homeFragment_to_historyFragment, null, navOptions);

        // Update BottomNavigationView
        BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_history);
    }

    @SuppressLint("SetTextI18n")
    private void clearBatchDetails() {
        currentBatchID = null;
        binding.cardTotal.setVisibility(View.GONE);
        binding.textTotalItems.setText("0 karung (sak)");
        binding.finishButtonGroup.setVisibility(View.GONE);
        binding.sectionMode.setVisibility(View.GONE);
        adapter.submitList(null);
    }

    @SuppressLint("SetTextI18n")
    private void enable_auto_mode(){
        binding.textAmount.setVisibility(View.VISIBLE);
        binding.editAmount.setVisibility(View.GONE);
        binding.iconMode.setImageResource(R.drawable.ic_circle_success);
        binding.textMode.setText("Mode Otomatis");
    }

    @SuppressLint("SetTextI18n")
    private void enable_manual_mode(){
        binding.textAmount.setVisibility(View.GONE);
        binding.editAmount.setVisibility(View.VISIBLE);
        binding.iconMode.setImageResource(R.drawable.ic_circle_danger);
        binding.textMode.setText("Mode Manual");
    }

    @SuppressLint("SetTextI18n")
    private void  active_batch(){
        binding.textBatchStatus.setText("Batch aktif");
        binding.iconBatchStatus.setImageResource(R.drawable.ic_circle_success);
    }

    @SuppressLint("SetTextI18n")
    private void  inactive_batch(){
        binding.textBatchStatus.setText("Batch tidak aktif");
        binding.iconBatchStatus.setImageResource(R.drawable.ic_circle_danger);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
