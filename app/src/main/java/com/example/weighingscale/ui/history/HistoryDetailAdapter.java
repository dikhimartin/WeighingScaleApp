package com.example.weighingscale.ui.history;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.model.BatchDetail;
import com.example.weighingscale.databinding.ItemHistoryDetailBinding;
import com.example.weighingscale.util.DateTimeUtil;
import java.util.Locale;

public class HistoryDetailAdapter extends ListAdapter<BatchDetail, HistoryDetailAdapter.BatchDetailHolder> {
    private final Batch currentBatch;

    public HistoryDetailAdapter(Context context, Batch batch) {
        super(DIFF_CALLBACK);
        this.currentBatch = batch;
    }

    private static final DiffUtil.ItemCallback<BatchDetail> DIFF_CALLBACK = new DiffUtil.ItemCallback<BatchDetail>() {
        @Override
        public boolean areItemsTheSame(@NonNull BatchDetail oldItem, @NonNull BatchDetail newItem) {
            return oldItem.getID().equals(newItem.getID());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull BatchDetail oldItem, @NonNull BatchDetail newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public BatchDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHistoryDetailBinding binding = ItemHistoryDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BatchDetailHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BatchDetailHolder holder, int position) {
        BatchDetail currentData = getItem(position);
        if (currentData == null) return;

        holder.bind(currentData, currentBatch);
    }

    public static class BatchDetailHolder extends RecyclerView.ViewHolder {
        private final ItemHistoryDetailBinding binding;

        public BatchDetailHolder(@NonNull ItemHistoryDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(BatchDetail batchDetail, Batch currentBatch) {
            String formattedDate = DateTimeUtil.formatDateTime(batchDetail.getDatetime(), "dd/MM/yyyy HH:mm");
            String amountText = String.format(Locale.getDefault(), "%d %s", batchDetail.getAmount(), currentBatch != null ? currentBatch.unit : "Kg");
            binding.textViewWeight.setText(amountText);
            binding.textViewDate.setText(formattedDate);
        }
    }
}
