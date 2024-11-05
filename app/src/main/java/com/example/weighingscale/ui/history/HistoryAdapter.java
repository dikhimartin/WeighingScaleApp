package com.example.weighingscale.ui.history;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weighingscale.R;
import com.example.weighingscale.data.dto.BatchDTO;
import com.example.weighingscale.util.DateTimeUtil;
import com.example.weighingscale.util.WeighingUtils;

import java.util.HashSet;
import java.util.Set;

public class HistoryAdapter extends ListAdapter<BatchDTO, HistoryAdapter.HistoryHolder> {
    private OnItemClickListener listener;
    private final Set<String> selectedItems = new HashSet<>();

    public HistoryAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<BatchDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<BatchDTO>() {
        @Override
        public boolean areItemsTheSame(@NonNull BatchDTO oldItem, @NonNull BatchDTO newItem) {
            return oldItem.getID() == newItem.getID();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull BatchDTO oldItem, @NonNull BatchDTO newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getPicPhoneNumber().equals(newItem.getPicPhoneNumber());
        }
    };

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryHolder(itemView);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {
        BatchDTO currentData = getItem(position);
        holder.textViewTitle.setText(currentData.getTitle());
        holder.textViewDateTime.setText(DateTimeUtil.formatDateTime(currentData.start_date, "dd MMMM yyyy HH:mm"));
        holder.textViewTotalWeight.setText(WeighingUtils.convertWeight(currentData.getTotalAmount(), currentData.getUnit(), false));
        holder.itemView.setBackgroundColor(selectedItems.contains(currentData.getID()) ?
                holder.itemView.getResources().getColor(R.color.selectedItem) :
                holder.itemView.getResources().getColor(R.color.defaultItem));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void toggleSelection(String BatchID) {
        if (selectedItems.contains(BatchID)) {
            selectedItems.remove(BatchID);
        } else {
            selectedItems.add(BatchID);
        }
        notifyDataSetChanged();
    }

    public Set<String> getSelectedItems() {
        return selectedItems;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearSelectedItems() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    class HistoryHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewDateTime;
        private final TextView textViewTotalWeight;
        private final ImageButton btnMore;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDateTime = itemView.findViewById(R.id.text_view_datetime);
            textViewTotalWeight = itemView.findViewById(R.id.text_view_total_weight);
            btnMore = itemView.findViewById(R.id.btnMore);

            btnMore.setOnClickListener(view -> {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), btnMore);
                popupMenu.inflate(R.menu.item_action_history);
                popupMenu.setOnMenuItemClickListener(item -> {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        int id = item.getItemId();
                         if (id == R.id.action_share) {
                            listener.onExportClick(getItem(position));
                            return true;
                        } else if (id == R.id.action_delete) {
                            listener.onDeleteClick(getItem(position));
                            return true;
                        }
                    }
                    return false;
                });
                popupMenu.show();
            });

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });

            itemView.setOnLongClickListener(view -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(getItem(position));
                    return true;
                }
                return false;
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(BatchDTO batch);
        void onExportClick(BatchDTO batch);
        void onDeleteClick(BatchDTO batch);
        void onItemLongClick(BatchDTO batch);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
