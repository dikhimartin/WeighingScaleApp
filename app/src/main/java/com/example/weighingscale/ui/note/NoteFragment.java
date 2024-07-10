package com.example.weighingscale.ui.note;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weighingscale.R;
import com.example.weighingscale.data.model.Note;

import java.util.List;

public class NoteFragment extends Fragment {

    private NoteViewModel noteViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        final NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(getViewLifecycleOwner(), new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                adapter.submitList(notes);
            }
        });

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                // Navigasi ke AddEditNoteFragment dengan mengirimkan ID catatan yang dipilih
                Bundle bundle = new Bundle();
                bundle.putInt("note_id", note.getId());

                NavHostFragment.findNavController(NoteFragment.this)
                        .navigate(R.id.action_noteFragment_to_addEditNoteFragment, bundle);
            }
        });

        view.findViewById(R.id.button_add_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddEditNoteFragment();
            }
        });

        return view;
    }

    private void openAddEditNoteFragment() {
        NavHostFragment.findNavController(NoteFragment.this)
                .navigate(R.id.action_noteFragment_to_addEditNoteFragment);
    }

}
