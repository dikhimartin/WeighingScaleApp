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
import com.example.weighingscale.data.model.Batch;

import java.util.HashSet;
import java.util.Set;

public class HistoryAdapter extends ListAdapter<Batch, HistoryAdapter.HistoryHolder> {
    private OnItemClickListener listener;
    private Set<String> selectedItems = new HashSet<>();

    public HistoryAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Batch> DIFF_CALLBACK = new DiffUtil.ItemCallback<Batch>() {
        @Override
        public boolean areItemsTheSame(@NonNull Batch oldItem, @NonNull Batch newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Batch oldItem, @NonNull Batch newItem) {
            return oldItem.getPicName().equals(newItem.getPicName()) &&
                    oldItem.getPicPhoneNumber().equals(newItem.getPicPhoneNumber());
        }
    };

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new HistoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {
        Batch currentNote = getItem(position);
        holder.textViewTitle.setText(currentNote.getPicName());
        holder.textViewDescription.setText(currentNote.getPicPhoneNumber());
        holder.itemView.setBackgroundColor(selectedItems.contains(currentNote.getId()) ?
                holder.itemView.getResources().getColor(R.color.selectedItem) :
                holder.itemView.getResources().getColor(R.color.defaultItem));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void toggleSelection(String noteId) {
        if (selectedItems.contains(noteId)) {
            selectedItems.remove(noteId);
        } else {
            selectedItems.add(noteId);
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
        private TextView textViewTitle;
        private TextView textViewDescription;
        private ImageButton btnMore;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            btnMore = itemView.findViewById(R.id.btnMore);

            btnMore.setOnClickListener(view -> {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), btnMore);
                popupMenu.inflate(R.menu.item_action_menu);
                popupMenu.setOnMenuItemClickListener(item -> {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        int id = item.getItemId();
                        if (id == R.id.action_edit) {
                            listener.onEditClick(getItem(position));
                            return true;
                        } else if (id == R.id.action_export) {
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
        void onItemClick(Batch batch);
        void onEditClick(Batch batch);
        void onExportClick(Batch batch);
        void onDeleteClick(Batch batch);
        void onItemLongClick(Batch batch);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
