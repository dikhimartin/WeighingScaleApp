package com.example.weighingscale.ui.note;

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
import com.example.weighingscale.data.model.Note;

import java.util.HashSet;
import java.util.Set;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteHolder> {
    private OnItemClickListener listener;
    private Set<Integer> selectedItems = new HashSet<>();

    public NoteAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription());
        }
    };

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note currentNote = getItem(position);
        holder.textViewTitle.setText(currentNote.getTitle());
        holder.textViewDescription.setText(currentNote.getDescription());
        holder.itemView.setBackgroundColor(selectedItems.contains(currentNote.getId()) ?
                holder.itemView.getResources().getColor(R.color.selectedItem) :
                holder.itemView.getResources().getColor(R.color.defaultItem));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void toggleSelection(int noteId) {
        if (selectedItems.contains(noteId)) {
            selectedItems.remove(noteId);
        } else {
            selectedItems.add(noteId);
        }
        notifyDataSetChanged();
    }

    public Set<Integer> getSelectedItems() {
        return selectedItems;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearSelectedItems() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDescription;
        private ImageButton btnMore;

        public NoteHolder(@NonNull View itemView) {
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
                        } else if (id == R.id.action_share) {
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
        void onItemClick(Note note);
        void onEditClick(Note note);
        void onExportClick(Note note);
        void onDeleteClick(Note note);
        void onItemLongClick(Note note);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
