package com.example.weighingscale.data.local.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.model.BatchDetail;

import java.util.List;

@Dao
public interface BatchDao {
    @Query("SELECT * FROM Batch")
    List<Batch> getAllBatches();

    @Query("SELECT * FROM Batch LIMIT 1")
    Batch getFirstBatch();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBatch(Batch batch);

    @Transaction
    @Query("SELECT * FROM Batch WHERE id = :batchId")
    Batch getBatchWithDetails(int batchId);
}
