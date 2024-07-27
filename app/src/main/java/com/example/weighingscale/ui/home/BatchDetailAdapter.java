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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedDate = dateFormat.format(currentData.getDatetime());
        holder.textViewWeight.setText(String.format(Locale.getDefault(), "%.1f Kg", currentData.getAmount()));
        holder.textViewDate.setText(formattedDate);
        if (position == 0) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.selectedItem));
        } else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        }
    }

    public class BatchDetailHolder extends RecyclerView.ViewHolder {
        private TextView textViewDate;
        private TextView textViewWeight;
        private CardView cardView;

        public BatchDetailHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewWeight = itemView.findViewById(R.id.text_view_weight);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
