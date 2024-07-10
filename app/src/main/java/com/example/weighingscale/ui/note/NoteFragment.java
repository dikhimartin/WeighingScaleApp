package com.example.weighingscale.ui.note;

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
import com.example.weighingscale.data.model.Note;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;

public class NoteFragment extends Fragment {

    private NoteViewModel noteViewModel;
    private NoteAdapter adapter;
    private View deleteAllButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        setupRecyclerView(view);
        setupViewModel();
        setupAddButton(view);
        setupDeleteAllButton(view);
        return view;
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                adapter.toggleSelection(note.getId());
                updateDeleteAllButtonVisibility();
            }

            @Override
            public void onEditClick(Note note) {
                Bundle bundle = new Bundle();
                bundle.putInt("note_id", note.getId());
                NavHostFragment.findNavController(NoteFragment.this)
                        .navigate(R.id.action_noteFragment_to_addEditNoteFragment, bundle);
            }

            @Override
            public void onExportClick(Note note) {
                // Implement export functionality here
                Snackbar.make(requireView(), "Export " + note.getTitle(), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(Note note) {
                noteViewModel.delete(note);
                Snackbar.make(requireView(), note.getTitle() + " " + R.string.deleted, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(Note note) {
                // TODO : Do something when on item click long click
            }

        });
    }

    private void setupViewModel() {
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(getViewLifecycleOwner(), notes -> {
            NoteAdapter adapter = (NoteAdapter) ((RecyclerView) requireView().findViewById(R.id.recycler_view)).getAdapter();
            if (adapter != null) {
                adapter.submitList(notes);
            }
        });
    }

    private void setupAddButton(View view) {
        view.findViewById(R.id.button_add_note).setOnClickListener(v ->
                NavHostFragment.findNavController(NoteFragment.this)
                        .navigate(R.id.action_noteFragment_to_addEditNoteFragment)
        );
    }

    private void setupDeleteAllButton(View view) {
        deleteAllButton = view.findViewById(R.id.button_delete_all);
        deleteAllButton.setOnClickListener(v -> {
            if (adapter != null) {
                List<Integer> selectedNoteIds = new ArrayList<>(adapter.getSelectedItems());

                if (!selectedNoteIds.isEmpty()) {
                    showDeleteConfirmationDialog(selectedNoteIds);
                } else {
                    Snackbar.make(requireView(), "No notes selected to delete", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDeleteConfirmationDialog(List<Integer> selectedNoteIds) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_selected_data_title)
                .setMessage(R.string.delete_selected_data_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    noteViewModel.deleteNotesByIds(selectedNoteIds);
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
