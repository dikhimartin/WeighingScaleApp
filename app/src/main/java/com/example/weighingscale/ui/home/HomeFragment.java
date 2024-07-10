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

import com.example.weighingscale.R;
import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.repository.BatchDetailRepository;
import com.example.weighingscale.data.repository.BatchRepository;
import com.example.weighingscale.databinding.FragmentHomeBinding;
import com.example.weighingscale.state.StateConnecting;
import com.example.weighingscale.ui.shared.SharedViewModel;

import java.util.Date;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private SharedViewModel sharedViewModel;
    private int currentBatchId = -1;
    private double ricePrice = 1000.0; // Example rice price, replace with actual value

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Obtain the repository instances
        BatchRepository batchRepository = BatchRepository.getInstance(requireContext());
        BatchDetailRepository batchDetailRepository = BatchDetailRepository.getInstance(requireContext());

        // Create the ViewModelFactory
        HomeViewModelFactory factory = new HomeViewModelFactory(batchRepository, batchDetailRepository);

        // Get the HomeViewModel using the factory
        homeViewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        // Get the SharedViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observe active batch
        homeViewModel.getActiveBatch().observe(getViewLifecycleOwner(), new Observer<Batch>() {
            @Override
            public void onChanged(Batch batch) {
                if (batch != null) {
                    currentBatchId = batch.id;
                    // Display batch information on the screen
                    // Example: binding.textBatchInfo.setText(batch.toString());
                } else {
                    currentBatchId = -1;
                    // Handle no active batch
                    // Example: binding.textBatchInfo.setText("No active batch");
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
        if (currentBatchId == -1) {
            Toast.makeText(requireContext(), "No active batch", Toast.LENGTH_SHORT).show();
            return;
        }

        String amountText;
        if (Boolean.TRUE.equals(sharedViewModel.getBluetoothStatus().getValue() == StateConnecting.BLUETOOTH_CONNECTED)) {
            amountText = binding.textAmount.getText().toString();
        } else {
            amountText = binding.editAmount.getText().toString();
        }

        try {
            double amount = Double.parseDouble(amountText);
            double price = amount * ricePrice;
            homeViewModel.insertBatchDetail(currentBatchId, amount, price);
            Toast.makeText(requireContext(), "Batch detail saved", Toast.LENGTH_SHORT).show();
            resetAmount();
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetAmount() {
        if (Boolean.TRUE.equals(sharedViewModel.getBluetoothStatus().getValue() == StateConnecting.BLUETOOTH_CONNECTED)) {
            sharedViewModel.setWeight("0");
        } else {
            binding.textAmount.setText("0");
            binding.editAmount.setText("");
        }
    }


    private void finishBatch() {
        if (currentBatchId != -1) {
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
        EditText editDestination = dialogView.findViewById(R.id.et_destination);
        EditText editTruckDriver = dialogView.findViewById(R.id.et_truck_driver);
        EditText editTruckDriverPhoneNumber = dialogView.findViewById(R.id.et_truck_driver_phone_number);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView)
                .setTitle("Input Batch")
                .setPositiveButton("Save", (dialog, id) -> {
                    String picName = editPicName.getText().toString();
                    String picPhoneNumber = editPicPhoneNumber.getText().toString();
                    String destination = editDestination.getText().toString();
                    String truckDriver = editTruckDriver.getText().toString();
                    String truckDriverPhoneNumber = editTruckDriverPhoneNumber.getText().toString();
                    Batch batch = new Batch();
                    batch.picName = picName;
                    batch.picPhoneNumber = picPhoneNumber;
                    batch.datetime = new Date();
                    batch.destination = destination;
                    batch.truckDriver = truckDriver;
                    batch.truckDriverPhoneNumber = truckDriverPhoneNumber;
                    homeViewModel.insertBatch(batch);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
