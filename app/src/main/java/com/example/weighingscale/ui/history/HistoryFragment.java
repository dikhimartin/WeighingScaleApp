package com.example.weighingscale.ui.history;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weighingscale.R;
import com.example.weighingscale.data.dto.BatchDTO;
import com.example.weighingscale.util.PDFUtil;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    private HistoryAdapter adapter;
    private RecyclerView recyclerView;
    private ImageView imageNoData;
    private TextView textNoData;
    private View deleteAllButton;
    private boolean isSelectionMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        initViews(view);
        setupRecyclerView();
        setupViewModel();
        setupDeleteAllButton();
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        imageNoData = view.findViewById(R.id.image_no_data);
        textNoData = view.findViewById(R.id.text_no_data);
        deleteAllButton = view.findViewById(R.id.button_delete_all);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        adapter = new HistoryAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BatchDTO batch) {
                if (isSelectionMode) {
                    adapter.toggleSelection(batch.getID());
                    updateDeleteAllButtonVisibility();
                } else {
                    navigateToDetail(batch);
                }
            }

            @Override
            public void onExportClick(BatchDTO batch) {
                historyViewModel.getBatchDetails(batch.getID()).observe(getViewLifecycleOwner(), batchDetails -> {
                    File pdfFile = PDFUtil.generatePDF(requireContext(), batch, batchDetails);
                    if (pdfFile != null) {
                        PDFUtil.sharePDF(requireContext(), pdfFile);
                    }
                });
            }

            @Override
            public void onDeleteClick(BatchDTO batch) {
                new AlertDialog.Builder(requireContext())
                    .setTitle("Hapus data")
                    .setMessage("Apakah kamu yakin ingin menghapus data ini ?")
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        historyViewModel.deleteBatch(batch);
                        Snackbar.make(requireView(), batch.getPicName() + " " + getString(R.string.deleted), Snackbar.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
            }

            @Override
            public void onItemLongClick(BatchDTO batch) {
                if (!isSelectionMode) {
                    isSelectionMode = true;
                    adapter.toggleSelection(batch.getID());
                    updateDeleteAllButtonVisibility();
                }
            }
        });
    }

    private void navigateToDetail(BatchDTO batch) {
        Bundle bundle = new Bundle();
        bundle.putString("batch_id", batch.getID());
        NavHostFragment.findNavController(HistoryFragment.this)
                .navigate(R.id.action_HistoryFragment_to_HistoryDetailFragment, bundle);
    }

    private void setupViewModel() {
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        historyViewModel.getAllBatch().observe(getViewLifecycleOwner(), batches -> {
            HistoryAdapter adapter = (HistoryAdapter) ((RecyclerView) requireView().findViewById(R.id.recycler_view)).getAdapter();
            if (adapter != null) {
                adapter.submitList(batches);
                toggleEmptyState(batches.isEmpty());
            }
        });
    }

    private void setupDeleteAllButton() {
        deleteAllButton.setOnClickListener(v -> {
            if (adapter != null) {
                List<String> selectedIds = new ArrayList<>(adapter.getSelectedItems());
                if (!selectedIds.isEmpty()) {
                    showDeleteAllConfirmationDialog(selectedIds);
                } else {
                    Snackbar.make(requireView(), "No batches selected to delete", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDeleteAllConfirmationDialog(List<String> selectedIds) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_selected_data_title)
                .setMessage(R.string.delete_selected_data_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    historyViewModel.deleteBatchByIds(selectedIds);
                    adapter.clearSelectedItems();
                    updateDeleteAllButtonVisibility();
                    isSelectionMode = false;
                    Snackbar.make(requireView(), R.string.message_selected_deleted, Snackbar.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void updateDeleteAllButtonVisibility() {
        if (adapter != null && adapter.getSelectedItems().isEmpty()) {
            deleteAllButton.setVisibility(View.GONE);
            isSelectionMode = false; // Exit selection mode when no items are selected
        } else {
            deleteAllButton.setVisibility(View.VISIBLE);
        }
    }

    private void toggleEmptyState(boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            imageNoData.setVisibility(View.VISIBLE);
            textNoData.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            imageNoData.setVisibility(View.GONE);
            textNoData.setVisibility(View.GONE);
        }
    }
}
