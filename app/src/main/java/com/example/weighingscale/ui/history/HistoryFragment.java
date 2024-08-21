package com.example.weighingscale.ui.history;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weighingscale.R;
import com.example.weighingscale.data.dto.BatchDTO;
import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.util.PDFUtil;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    private HistoryAdapter adapter;
    private View deleteAllButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        setupRecyclerView(view);
        setupViewModel();
        setupDeleteAllButton(view);
        return view;
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        adapter = new HistoryAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BatchDTO batch) {
                adapter.toggleSelection(batch.getID());
                updateDeleteAllButtonVisibility();
            }

            @Override
            public void onDetailClick(BatchDTO batch) {
                 Bundle bundle = new Bundle();
                 bundle.putString("batch_id", batch.getID());
                 NavHostFragment.findNavController(HistoryFragment.this)
                         .navigate(R.id.action_HistoryFragment_to_HistoryDetailFragment, bundle);
            }

            @Override
            public void onExportClick(BatchDTO batch) {
                // Ambil batch details terlebih dahulu
                historyViewModel.getBatchDetails(batch.getID()).observe(getViewLifecycleOwner(), batchDetails -> {
                    // Buat PDF
                    File pdfFile = PDFUtil.generatePDF(requireContext(), batch, batchDetails);
                    // Bagikan PDF
                    if (pdfFile != null) {
                        PDFUtil.sharePDF(requireContext(), pdfFile);
                    }
                });
            }

            @Override
            public void onDeleteClick(BatchDTO batch) {
                historyViewModel.deleteBatchByID(batch.getID());
                Snackbar.make(requireView(), batch.getPicName() + " " + getString(R.string.deleted), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(BatchDTO batch) {
                // TODO : Do something when on item click long click
            }
        });
    }

    private void setupViewModel() {
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        historyViewModel.getAllBatch().observe(getViewLifecycleOwner(), batches -> {
            HistoryAdapter adapter = (HistoryAdapter) ((RecyclerView) requireView().findViewById(R.id.recycler_view)).getAdapter();
            if (adapter != null) {
                adapter.submitList(batches);
            }
        });
    }

    private void setupDeleteAllButton(View view) {
        deleteAllButton = view.findViewById(R.id.button_delete_all);
        deleteAllButton.setOnClickListener(v -> {
            if (adapter != null) {
                List<String> selectedIds = new ArrayList<>(adapter.getSelectedItems());
                if (!selectedIds.isEmpty()) {
                    showDeleteConfirmationDialog(selectedIds);
                } else {
                    Snackbar.make(requireView(), "No batches selected to delete", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDeleteConfirmationDialog(List<String> selectedIds) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_selected_data_title)
                .setMessage(R.string.delete_selected_data_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    historyViewModel.deleteBatchByIds(selectedIds);
                    adapter.clearSelectedItems();
                    updateDeleteAllButtonVisibility();
                    Snackbar.make(requireView(), R.string.message_selected_deleted, Snackbar.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void updateDeleteAllButtonVisibility() {
        if (adapter != null && adapter.getSelectedItems().isEmpty()) {
            deleteAllButton.setVisibility(View.GONE);
        } else {
            deleteAllButton.setVisibility(View.VISIBLE);
        }
    }

}
