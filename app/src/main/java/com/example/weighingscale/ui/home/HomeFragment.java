package com.example.weighingscale.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.example.weighingscale.data.dto.AddressDTO;
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
import com.example.weighingscale.util.LogModelUtils;
import com.example.weighingscale.util.SafeValueUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

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
        // Inflate the custom dialog view
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input_batch, null);

        // Initialize views
        TextInputLayout tilDatetime = dialogView.findViewById(R.id.til_datetime);
        TextInputEditText editPicName = dialogView.findViewById(R.id.et_pic_name);
        TextInputEditText editPicPhoneNumber = dialogView.findViewById(R.id.et_pic_phone_number);
        TextInputEditText editTruckDriver = dialogView.findViewById(R.id.et_truck_driver);
        TextInputEditText editTruckDriverPhoneNumber = dialogView.findViewById(R.id.et_truck_driver_phone_number);
        TextInputEditText tvDatetime = dialogView.findViewById(R.id.tv_datetime);
        AutoCompleteTextView selectLocation = dialogView.findViewById(R.id.select_weighing_location_city);
        AutoCompleteTextView selectDestination = dialogView.findViewById(R.id.select_destination_city);

        // Set current date and time
        tvDatetime.setText(DateTimeUtil.formatDateTime(new Date(), "yyyy-MM-dd HH:mm:ss"));
        tilDatetime.setVisibility(View.VISIBLE);

        // Autofill values from current settings
        observeSetting(() -> {
            editPicName.setText(currentSetting.pic_name);
            editPicPhoneNumber.setText(currentSetting.pic_phone_number);
        });

        // Set up AutoCompleteTextViews with location options
        setupOptionLocation(selectLocation);
        setupOptionLocation(selectDestination);

        // Store selected city IDs
        final String[] deliveryDestinationID = new String[1];
        final String[] weighingLocationID = new String[1];

        selectDestination.setOnItemClickListener((parent, view, position, id) -> {
            SelectOptionWrapper selectedCity = (SelectOptionWrapper) parent.getAdapter().getItem(position);
            if (selectedCity != null) {
                deliveryDestinationID[0] = selectedCity.getId();
            }
        });

        selectLocation.setOnItemClickListener((parent, view, position, id) -> {
            SelectOptionWrapper selectedCity = (SelectOptionWrapper) parent.getAdapter().getItem(position);
            if (selectedCity != null) {
                weighingLocationID[0] = selectedCity.getId();
            }
        });

        // Show datetime picker when clicking on the datetime field
        tvDatetime.setOnClickListener(view -> DateTimeUtil.showDateTimePicker(getChildFragmentManager(), tvDatetime));

        // Create and show the dialog
        new AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Masukan data batch muatan")
            .setPositiveButton("Simpan", (dialog, id) -> {
                // Create and populate batch object
                Batch batch = new Batch();
                batch.pic_name = Objects.requireNonNull(editPicName.getText()).toString();
                batch.pic_phone_number = Objects.requireNonNull(editPicPhoneNumber.getText()).toString();
                batch.datetime = DateTimeUtil.parseDateTime(Objects.requireNonNull(tvDatetime.getText()).toString());
                batch.start_date = batch.datetime;
                batch.truck_driver_name = Objects.requireNonNull(editTruckDriver.getText()).toString();
                batch.truck_driver_phone_number = Objects.requireNonNull(editTruckDriverPhoneNumber.getText()).toString();
                batch.unit = currentSetting != null ? currentSetting.unit : "kg";
                batch.rice_price = currentSetting != null ? currentSetting.rice_price : 0;

                if (deliveryDestinationID[0] != null) {
                    batch.delivery_destination_id = deliveryDestinationID[0];
                }
                if (weighingLocationID[0] != null) {
                    batch.weighing_location_id = weighingLocationID[0];
                }

                // Insert the batch into ViewModel and notify user
                homeViewModel.insertBatch(batch);
                Toast.makeText(requireContext(), "Batch muatan sudah aktif", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Batal", (dialog, id) -> dialog.dismiss())
            .create()
            .show();
    }


    private void setupOptionLocation(AutoCompleteTextView selectAddress) {
        locationViewModel.getAddress().observe(getViewLifecycleOwner(), addresses -> {
            if (addresses == null || addresses.isEmpty()) {
                return; // Early return if the address list is null or empty
            }
            List<SelectOptionWrapper> addressWrappers = new ArrayList<>();
            for (AddressDTO address : addresses) {
                String formattedAddress = String.format("%s %s - %s",
                    address.getCityType() != null ? address.getCityType() : "",
                    address.getCityName() != null ? address.getCityName() : "",
                    address.getProvinceName() != null ? address.getProvinceName() : "");

                addressWrappers.add(new SelectOptionWrapper(address.getID(), formattedAddress));
            }

            EntityAdapter addressAdapter = new EntityAdapter(requireContext(), addressWrappers);
            selectAddress.setAdapter(addressAdapter);
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
