package com.example.weighingscale.ui.note;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.weighingscale.R;
import com.example.weighingscale.data.model.Note;

public class AddEditNoteFragment extends Fragment {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private NumberPicker numberPickerPriority;

    private NoteViewModel noteViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_add, container, false);

        editTextTitle = view.findViewById(R.id.edit_text_title);
        editTextDescription = view.findViewById(R.id.edit_text_description);
        numberPickerPriority = view.findViewById(R.id.number_picker_priority);

        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        // Check if arguments contain a note ID, indicating edit mode
        if (getArguments() != null && getArguments().containsKey("note_id")) {
            // Retrieve note details and populate the UI for editing
            int noteId = getArguments().getInt("note_id", -1);
            noteViewModel.getNoteById(noteId).observe(getViewLifecycleOwner(), new Observer<Note>() {
                @Override
                public void onChanged(Note note) {
                    if (note != null) {
                        editTextTitle.setText(note.getTitle());
                        editTextDescription.setText(note.getDescription());
                        numberPickerPriority.setValue(note.getPriority());
                    }
                }
            });
        }

        // Setup save button click listener
        view.findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        return view;
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        int priority = numberPickerPriority.getValue();

        if (title.isEmpty()) {
            editTextTitle.setError("Title required");
            editTextTitle.requestFocus();
            return;
        }

        Note note = new Note(title, description, priority);

        // Check if this is an edit or new note
        if (getArguments() != null && getArguments().containsKey("note_id")) {
            int noteId = getArguments().getInt("note_id", -1);
            note.setId(noteId);
            noteViewModel.update(note);
        } else {
            noteViewModel.insert(note);
        }

        // Navigate back to NoteFragment
        requireActivity().onBackPressed();
    }
}
