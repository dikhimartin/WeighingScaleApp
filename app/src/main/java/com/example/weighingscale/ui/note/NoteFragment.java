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

public class NoteFragment extends Fragment {

    private NoteViewModel noteViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        setupRecyclerView(view);
        setupViewModel();
        setupAddButton(view);
        return view;
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        final NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(note -> {
            Bundle bundle = new Bundle();
            bundle.putInt("note_id", note.getId());
            NavHostFragment.findNavController(NoteFragment.this)
                    .navigate(R.id.action_noteFragment_to_addEditNoteFragment, bundle);
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
}
