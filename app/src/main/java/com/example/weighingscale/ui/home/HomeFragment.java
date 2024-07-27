package com.example.weighingscale.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weighingscale.R;
import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.model.City;
import com.example.weighingscale.data.model.Province;
import com.example.weighingscale.data.model.Subdistrict;
import com.example.weighingscale.data.repository.AddressRepository;
import com.example.weighingscale.data.repository.BatchDetailRepository;
import com.example.weighingscale.data.repository.BatchRepository;
import com.example.weighingscale.databinding.FragmentHomeBinding;
import com.example.weighingscale.state.StateConnecting;
import com.example.weighingscale.ui.shared.EntityAdapter;
import com.example.weighingscale.ui.shared.SelectOptionWrapper;
import com.example.weighingscale.ui.shared.SharedViewModel;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private SharedViewModel sharedViewModel;
    private BatchDetailAdapter adapter;
    private String currentBatchId = null;

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

        // Get the HomeViewModel using the factory
        homeViewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        // Get the SharedViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Adapter log list
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_log);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        adapter = new BatchDetailAdapter(requireContext());
        recyclerView.setAdapter(adapter);

        // Observe active batch
        homeViewModel.getActiveBatch().observe(getViewLifecycleOwner(), new Observer<Batch>() {
            @Override
            public void onChanged(Batch batch) {
                if (batch != null) {
                    currentBatchId = batch.id;
                    // Display batch information on the screen
                    // Example: binding.textBatchInfo.setText(batch.toString());
                    homeViewModel.getBatchDetails(currentBatchId).observe(getViewLifecycleOwner(), data -> {
                        BatchDetailAdapter adapter = (BatchDetailAdapter) ((RecyclerView) requireView().findViewById(R.id.recycler_view_log)).getAdapter();
                        if (adapter != null) {
                            adapter.submitList(data);
                            // Auto scroll to top after submitting list
                            if (data != null && !data.isEmpty()) {
                                recyclerView.smoothScrollToPosition(0);
                            }
                        }
                    });
                } else {
                    currentBatchId = null;
                    // Handle no active batch
                    // Example: binding.textBatchInfo.setText("No active batch");
                    adapter.submitList(null);
                    showBatchInputDialog();
                }
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
                finishBatch();
            }
        });

        return root;
    }


    private void saveLog() {
        if (currentBatchId == null) {
            Toast.makeText(requireContext(), "No active batch", Toast.LENGTH_SHORT).show();
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
            double amount = Double.parseDouble(amountText);
            // Example rice price, replace with actual value
            double ricePrice = 1000.0;
            double price = amount * ricePrice;
            homeViewModel.insertBatchDetail(currentBatchId, amount, price);
            Toast.makeText(requireContext(), "Batch detail saved", Toast.LENGTH_SHORT).show();
            resetAmount();
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
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


    private void finishBatch() {
        if (currentBatchId != null) {
            homeViewModel.completeBatch(currentBatchId);
            Toast.makeText(requireContext(), "Batch completed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "No active batch", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBatchInputDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input_batch, null);

        EditText editPicName = dialogView.findViewById(R.id.et_pic_name);
        EditText editPicPhoneNumber = dialogView.findViewById(R.id.et_pic_phone_number);
        EditText editTruckDriver = dialogView.findViewById(R.id.et_truck_driver);
        EditText editTruckDriverPhoneNumber = dialogView.findViewById(R.id.et_truck_driver_phone_number);

        MaterialAutoCompleteTextView actvProvince = dialogView.findViewById(R.id.actv_province);
        MaterialAutoCompleteTextView actvCity = dialogView.findViewById(R.id.actv_city);
        MaterialAutoCompleteTextView actvSubdistrict = dialogView.findViewById(R.id.actv_subdistrict);

        setupAutoCompleteTextViews(actvProvince, actvCity, actvSubdistrict);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView)
                .setTitle("Input Batch")
                .setPositiveButton("Simpan", (dialog, id) -> {
                    String picName = editPicName.getText().toString();
                    String picPhoneNumber = editPicPhoneNumber.getText().toString();
                    String truckDriver = editTruckDriver.getText().toString();
                    String truckDriverPhoneNumber = editTruckDriverPhoneNumber.getText().toString();

                    Batch batch = new Batch();
                    batch.pic_name = picName;
                    batch.pic_phone_number = picPhoneNumber;
                    batch.datetime = new Date();
                    batch.truck_driver_name = truckDriver;
                    batch.truck_driver_phone_number = truckDriverPhoneNumber;
                    homeViewModel.insertBatch(batch);
                })
                .setNegativeButton("Batal", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupAutoCompleteTextViews(MaterialAutoCompleteTextView actvProvince, MaterialAutoCompleteTextView actvCity, MaterialAutoCompleteTextView actvSubdistrict) {
        // Setup Province Adapter
        homeViewModel.getAllProvinces().observe(getViewLifecycleOwner(), provinces -> {
            if (provinces != null && !provinces.isEmpty()) {
                List<SelectOptionWrapper> provinceWrappers = new ArrayList<>();
                for (Province province : provinces) {
                    provinceWrappers.add(new SelectOptionWrapper(province.getId(), province.getName()));
                }
                EntityAdapter provinceAdapter = new EntityAdapter(requireContext(), provinceWrappers);
                actvProvince.setAdapter(provinceAdapter);
                actvProvince.setOnItemClickListener((parent, view, position, id) -> {
                    SelectOptionWrapper selectedWrapper = (SelectOptionWrapper) parent.getAdapter().getItem(position);
                    if (selectedWrapper != null) {
                        loadCities(selectedWrapper.getId(), actvCity, actvSubdistrict);
                    }
                });
            }
        });
    }

    private void loadCities(String provinceId, MaterialAutoCompleteTextView actvCity, MaterialAutoCompleteTextView actvSubdistrict) {
        homeViewModel.getCitiesByProvinceId(provinceId).observe(getViewLifecycleOwner(), cities -> {
            if (cities != null && !cities.isEmpty()) {
                List<SelectOptionWrapper> cityWrappers = new ArrayList<>();
                for (City city : cities) {
                    cityWrappers.add(new SelectOptionWrapper(city.getId(), city.getType() +" "+ city.getName()));
                }
                EntityAdapter cityAdapter = new EntityAdapter(requireContext(), cityWrappers);
                actvCity.setAdapter(cityAdapter);
                actvCity.setOnItemClickListener((parent, view, position, id) -> {
                    SelectOptionWrapper selectedWrapper = (SelectOptionWrapper) parent.getAdapter().getItem(position);
                    if (selectedWrapper != null) {
                        loadSubdistricts(selectedWrapper.getId(), actvSubdistrict);
                    }
                });
            }
        });
    }

    private void loadSubdistricts(String cityId, MaterialAutoCompleteTextView actvSubdistrict) {
        homeViewModel.getSubdistrictsByCityId(cityId).observe(getViewLifecycleOwner(), subdistricts -> {
            if (subdistricts != null && !subdistricts.isEmpty()) {
                List<SelectOptionWrapper> subdistrictWrappers = new ArrayList<>();
                for (Subdistrict subdistrict : subdistricts) {
                    subdistrictWrappers.add(new SelectOptionWrapper(subdistrict.getId(), subdistrict.getName()));
                }
                EntityAdapter subdistrictAdapter = new EntityAdapter(requireContext(), subdistrictWrappers);
                actvSubdistrict.setAdapter(subdistrictAdapter);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
