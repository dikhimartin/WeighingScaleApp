package com.example.weighingscale.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weighingscale.R;
import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.model.BatchDetail;
import com.example.weighingscale.data.model.City;
import com.example.weighingscale.data.model.Province;
import com.example.weighingscale.data.model.Setting;
import com.example.weighingscale.data.repository.AddressRepository;
import com.example.weighingscale.data.repository.BatchDetailRepository;
import com.example.weighingscale.data.repository.BatchRepository;
import com.example.weighingscale.databinding.FragmentHomeBinding;
import com.example.weighingscale.state.StateConnecting;
import com.example.weighingscale.ui.setting.SettingViewModel;
import com.example.weighingscale.ui.shared.EntityAdapter;
import com.example.weighingscale.ui.shared.SelectOptionWrapper;
import com.example.weighingscale.ui.shared.SharedViewModel;
import com.example.weighingscale.util.DateTimeUtil;

import android.widget.AutoCompleteTextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private SettingViewModel settingViewModel;
    private SharedViewModel sharedViewModel;
    private BatchDetailAdapter adapter;

    private String currentBatchId = null;
    private Setting currentSetting;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Obtain the repository instances
        BatchRepository batchRepository = BatchRepository.getInstance(requireContext());
        BatchDetailRepository batchDetailRepository = BatchDetailRepository.getInstance(requireContext());
        AddressRepository addressRepository = AddressRepository.getInstance(requireContext());

        // Create the ViewModelFactory
        HomeViewModelFactory factory = new HomeViewModelFactory(batchRepository, batchDetailRepository, addressRepository);

        // Init instance HomeViewModel
        homeViewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        // Init instance SharedViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Init instance settingViewModel
        settingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);

        // Adapter log list
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_log);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        adapter = new BatchDetailAdapter(requireContext());
        recyclerView.setAdapter(adapter);

        // Observe setting once
        settingViewModel.getSetting().observe(getViewLifecycleOwner(), setting -> {
            if (setting != null) {
                currentSetting = setting;
                binding.textUnit.setText(setting.unit);
            }
        });

        // Observe active batch
        homeViewModel.getActiveBatch().observe(getViewLifecycleOwner(), batch -> {
            if (batch != null) {
                currentBatchId = batch.id;
                homeViewModel.getBatchDetails(currentBatchId).observe(getViewLifecycleOwner(), data -> {
                    adapter.submitList(data);

                    if (data != null && !data.isEmpty()) {
                        recyclerView.smoothScrollToPosition(0);

                        // Calculate total weight
                        double totalWeight = 0.0;
                        for (BatchDetail detail : data) {
                            totalWeight += detail.getAmount();
                        }
                        String totalWeightText = String.format(Locale.getDefault(), "%.0f %s", totalWeight, currentSetting != null ? currentSetting.unit : "Kg");
                        binding.textTotalWeight.setText(totalWeightText);

                        // Update item count
                        String itemCountText = String.format(Locale.getDefault(), "%d Item", data.size());
                        binding.textTotalItems.setText(itemCountText);

                        binding.cardTotal.setVisibility(View.VISIBLE);
                    } else {
                        binding.cardTotal.setVisibility(View.GONE);
                        binding.textTotalItems.setText("0 Item");
                    }
                });
            } else {
                currentBatchId = null;
                binding.cardTotal.setVisibility(View.GONE);
                binding.textTotalItems.setText("0 Item");
                adapter.submitList(null);
                showBatchInputDialog();
            }
        });

        // Observe Bluetooth status
        sharedViewModel.getBluetoothStatus().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer status) {
                if (status != null && status == StateConnecting.BLUETOOTH_CONNECTED) {
                    binding.textAmount.setVisibility(View.VISIBLE);
                    binding.editAmount.setVisibility(View.GONE);
                    sharedViewModel.getWeight().observe(getViewLifecycleOwner(), new Observer<String>() {
                        @Override
                        public void onChanged(String weight) {
                            binding.textAmount.setText(weight);
                        }
                    });
                } else {
                    binding.textAmount.setVisibility(View.GONE);
                    binding.editAmount.setVisibility(View.VISIBLE);
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
                if (currentBatchId != null) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Selesaikan batch muatan")
                            .setMessage("Apakah kamu yakin ingin menyelesaikan batch muatan ini ?")
                            .setPositiveButton(R.string.yes, (dialog, which) -> {
                                homeViewModel.completeBatch(currentBatchId);
                                Toast.makeText(requireContext(), "Batch muatan sudah selesai", Toast.LENGTH_SHORT).show();
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

    private void saveLog() {
        if (currentBatchId == null) {
            Toast.makeText(requireContext(), "Tidak ada batch muatan yang aktif", Toast.LENGTH_SHORT).show();
            showBatchInputDialog();
            return;
        }

        String amountText;
        Integer bluetoothStatus = sharedViewModel.getBluetoothStatus().getValue();
        if (bluetoothStatus != null && bluetoothStatus == StateConnecting.BLUETOOTH_CONNECTED) {
            amountText = binding.textAmount.getText().toString();
        } else {
            amountText = binding.editAmount.getText().toString();
        }

        try {
            int amount = Integer.parseInt(amountText);
            homeViewModel.insertBatchDetail(currentBatchId, amount, currentSetting);
            Toast.makeText(requireContext(), "Log sudah disimpan", Toast.LENGTH_SHORT).show();
            resetAmount();
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Nilai yang kamu input tidak valid", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetAmount() {
        Integer bluetoothStatus = sharedViewModel.getBluetoothStatus().getValue();
        if (bluetoothStatus != null && bluetoothStatus == StateConnecting.BLUETOOTH_CONNECTED) {
            sharedViewModel.setWeight("0");
        } else {
            binding.textAmount.setText("0");
            binding.editAmount.setText("");
        }
    }

    private void showBatchInputDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input_batch, null);

        TextView editPicName = dialogView.findViewById(R.id.et_pic_name);
        TextView editPicPhoneNumber = dialogView.findViewById(R.id.et_pic_phone_number);
        TextView editTruckDriver = dialogView.findViewById(R.id.et_truck_driver);
        TextView editTruckDriverPhoneNumber = dialogView.findViewById(R.id.et_truck_driver_phone_number);
        TextView tvDateTime = dialogView.findViewById(R.id.tv_datetime);

        // Autofill value from setting
        if (currentSetting != null) {
            editPicName.setText(currentSetting.pic_name);
            editPicPhoneNumber.setText(currentSetting.pic_phone_number);
        }

        // Setup AutoCompleteTextView Weighing Location with adapter
        AutoCompleteTextView selectLocProvince = dialogView.findViewById(R.id.select_weighing_location_province);
        AutoCompleteTextView selectLocCity = dialogView.findViewById(R.id.select_weighing_location_city);
        setupOptionLocation(selectLocProvince, selectLocCity);

        // Setup AutoCompleteTextView Destination with adapter
        AutoCompleteTextView selectDestProvince = dialogView.findViewById(R.id.select_destination_province);
        AutoCompleteTextView selectDestCity = dialogView.findViewById(R.id.select_destination_city);
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
                .setTitle("Silahkan masukan data batch muatan")
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
                    batch.truck_driver_name = truckDriver;
                    batch.truck_driver_phone_number = truckDriverPhoneNumber;
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
        homeViewModel.getAllProvinces().observe(getViewLifecycleOwner(), provinces -> {
            if (provinces != null && !provinces.isEmpty()) {
                List<SelectOptionWrapper> provinceWrappers = new ArrayList<>();
                for (Province province : provinces) {
                    provinceWrappers.add(new SelectOptionWrapper(province.getId(), province.getName()));
                }
                EntityAdapter provinceAdapter = new EntityAdapter(requireContext(), provinceWrappers);
                selectProvince.setAdapter(provinceAdapter);

                selectProvince.setOnItemClickListener((parent, view, position, id) -> {
                    SelectOptionWrapper selectedWrapper = (SelectOptionWrapper) parent.getAdapter().getItem(position);
                    if (selectedWrapper != null) {
                        homeViewModel.getCitiesByProvinceId(selectedWrapper.getId()).observe(getViewLifecycleOwner(), cities -> {
                            if (cities != null && !cities.isEmpty()) {
                                List<SelectOptionWrapper> cityWrappers = new ArrayList<>();
                                for (City city : cities) {
                                    cityWrappers.add(new SelectOptionWrapper(city.getId(), city.getType() + " " + city.getName()));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
