package com.example.weighingscale.data.local.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.weighingscale.data.model.BatchDetail;

import java.util.List;

@Dao
public interface BatchDetailDao {

    @Query("SELECT * FROM BatchDetail WHERE batchId = :batchId ORDER BY datetime DESC")
    LiveData<List<BatchDetail>> getBatchDetails(int batchId);

    @Query("SELECT * FROM BatchDetail ORDER BY datetime DESC")
    LiveData<List<BatchDetail>> getAllNotes();

    @Insert
    void insert(BatchDetail batchDetail);
}
