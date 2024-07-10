package com.example.weighingscale.ui.note;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.weighingscale.R;
import com.example.weighingscale.data.model.Note;

public class FormNoteFragment extends Fragment {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private NumberPicker numberPickerPriority;
    private NoteViewModel noteViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_form, container, false);

        initializeViews(view);
        setupNumberPicker();
        initializeViewModel();
        setupMode(view);

        return view;
    }

    private void initializeViews(View view) {
        editTextTitle = view.findViewById(R.id.edit_text_title);
        editTextDescription = view.findViewById(R.id.edit_text_description);
        numberPickerPriority = view.findViewById(R.id.number_picker_priority);
    }

    private void setupNumberPicker() {
        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);
    }

    private void initializeViewModel() {
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
    }

    private void setupMode(View view) {
        Bundle args = getArguments();
        if (args != null && args.containsKey("note_id")) {
            int noteId = args.getInt("note_id", -1);
            noteViewModel.getNoteById(noteId).observe(getViewLifecycleOwner(), note -> {
                if (note != null) {
                    populateNoteFields(note);
                    updateActionBarTitle(getString(R.string.update)+ " " + note.getTitle());
                    updateSaveButtonText(view, getString(R.string.update));
                }
            });
        } else {
            updateActionBarTitle(getString(R.string.add));
            updateSaveButtonText(view, getString(R.string.save));
        }

        view.findViewById(R.id.button_save).setOnClickListener(v -> saveData());
    }

    private void populateNoteFields(Note note) {
        editTextTitle.setText(note.getTitle());
        editTextDescription.setText(note.getDescription());
        numberPickerPriority.setValue(note.getPriority());
    }

    private void updateActionBarTitle(String title) {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    private void updateSaveButtonText(View view, String text) {
        Button saveButton = view.findViewById(R.id.button_save);
        saveButton.setText(text);
    }

    private void saveData() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        int priority = numberPickerPriority.getValue();

        if (title.isEmpty()) {
            editTextTitle.setError(getString(R.string.is_required));
            editTextTitle.requestFocus();
            return;
        }

        Note note = new Note(title, description, priority);

        if (getArguments() != null && getArguments().containsKey("note_id")) {
            note.setId(getArguments().getInt("note_id", -1));
            noteViewModel.update(note);
            Toast.makeText(requireContext(), R.string.message_update_success, Toast.LENGTH_SHORT).show();
        } else {
            noteViewModel.insert(note);
            Toast.makeText(requireContext(), R.string.message_save_success, Toast.LENGTH_SHORT).show();
        }

        requireActivity().onBackPressed();
    }
}
