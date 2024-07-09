package com.example.weighingscale.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.weighingscale.R;
import com.example.weighingscale.databinding.FragmentHomeBinding;
import com.example.weighingscale.state.StateBatch;
import com.example.weighingscale.ui.setting.SettingViewModel;
import com.example.weighingscale.ui.shared.SharedViewModel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.widget.EditText;
import android.widget.Toast;
import com.example.weighingscale.data.model.Batch;

import java.util.Date;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        TextView textAmount = root.findViewById(R.id.text_amount);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getWeight().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String weight) {
                textAmount.setText(weight);
            }
        });

        // Observe batch state
        showBatchInputDialog();
//        viewModel.getBatchState().observe(getViewLifecycleOwner(), state -> {
//            if (state == StateBatch.INACTIVE) {
//                // Show input dialog when batch is inactive
//                showBatchInputDialog();
//            } else {
//                // Update UI with current batch details
//                viewModel.getCurrentBatch().observe(getViewLifecycleOwner(), this::updateUIWithBatch);
//            }
//        });

        return root;
    }

    private void showBatchInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input_batch, null);
        builder.setView(dialogView);

        builder.setPositiveButton("Save", (dialog, which) -> {
            Toast.makeText(requireContext(), "Batch saved successfully", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void updateUIWithBatch(Batch batch) {
        // Update UI elements with batch details
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}