package com.example.weighingscale.ui.history;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weighingscale.data.model.BatchDetail;
import com.example.weighingscale.databinding.ItemLogBinding;
import com.example.weighingscale.util.DateTimeUtil;
import java.util.Locale;

public class HistoryDetailAdapter extends ListAdapter<BatchDetail, HistoryDetailAdapter.BatchDetailHolder> {

    private static final DiffUtil.ItemCallback<BatchDetail> DIFF_CALLBACK = new DiffUtil.ItemCallback<BatchDetail>() {
        @Override
        public boolean areItemsTheSame(@NonNull BatchDetail oldItem, @NonNull BatchDetail newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull BatchDetail oldItem, @NonNull BatchDetail newItem) {
            return oldItem.equals(newItem);
        }
    };

    public HistoryDetailAdapter(Context context) {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public BatchDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLogBinding binding = ItemLogBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BatchDetailHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BatchDetailHolder holder, int position) {
        BatchDetail currentData = getItem(position);
        if (currentData == null) return; // Early return if null

        holder.bind(currentData);
    }

    public static class BatchDetailHolder extends RecyclerView.ViewHolder {
        private final ItemLogBinding binding;

        public BatchDetailHolder(@NonNull ItemLogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(BatchDetail batchDetail) {
            String formattedDate = DateTimeUtil.formatDateTime(batchDetail.getDatetime(), "dd/MM/yyyy HH:mm");
            String amountText = String.format(Locale.getDefault(), "%.0f %s", batchDetail.getAmount(), batchDetail.getUnit());
            binding.textViewWeight.setText(amountText);
            binding.textViewDate.setText(formattedDate);
        }
    }
}
