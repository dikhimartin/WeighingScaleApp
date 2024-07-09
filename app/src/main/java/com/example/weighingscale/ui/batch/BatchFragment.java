package com.example.weighingscale.ui.batch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.weighingscale.databinding.FragmentBatchBinding;

public class BatchFragment extends Fragment {

    private FragmentBatchBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BatchViewModel notificationsViewModel =
                new ViewModelProvider(this).get(BatchViewModel.class);

        binding = FragmentBatchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textBatch;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}