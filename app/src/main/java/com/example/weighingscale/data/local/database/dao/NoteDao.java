package com.example.weighingscale.data.local.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.weighingscale.data.model.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("DELETE FROM Note")
    void deleteAllNotes();

    @Query("SELECT * FROM Note ORDER BY priority DESC")
    LiveData<List<Note>> getAllNotes();

    @Query("SELECT * FROM Note WHERE id = :id")
    LiveData<Note> getNoteById(int id);

    @Query("DELETE FROM Note WHERE id IN (:ids)")
    void deleteNotesByIds(List<Integer> ids);
}
