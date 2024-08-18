package com.example.weighingscale.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weighingscale.R;
import com.example.weighingscale.data.model.BatchDetail;
import com.example.weighingscale.util.DateTimeUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class BatchDetailAdapter extends ListAdapter<BatchDetail, BatchDetailAdapter.BatchDetailHolder> {
    private Context context;

    public BatchDetailAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    private static final DiffUtil.ItemCallback<BatchDetail> DIFF_CALLBACK = new DiffUtil.ItemCallback<BatchDetail>() {
        @Override
        public boolean areItemsTheSame(@NonNull BatchDetail oldItem, @NonNull BatchDetail newItem) {
            return oldItem.getId() == newItem.getId();
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
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_log, parent, false);
        return new BatchDetailHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BatchDetailHolder holder, int position) {
        BatchDetail currentData = getItem(position);
        if (currentData == null) {
            return; // Return early if data at position is null
        }

        String formattedDate = DateTimeUtil.formatDateTime(currentData.getDatetime(), "dd/MM/yyyy HH:mm");

        // Format the amount text including the unit
        String amountText = String.format(Locale.getDefault(), "%.0f %s", currentData.getAmount(), currentData.getUnit());
        holder.textViewWeight.setText(amountText);
        holder.textViewDate.setText(formattedDate);

        // Change background color based on position
        if (position == 0) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.selectedItem));
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        }
    }

    public static class BatchDetailHolder extends RecyclerView.ViewHolder {
        private final TextView textViewDate;
        private final TextView textViewWeight;
        private final CardView cardView;

        public BatchDetailHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewWeight = itemView.findViewById(R.id.text_view_weight);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
