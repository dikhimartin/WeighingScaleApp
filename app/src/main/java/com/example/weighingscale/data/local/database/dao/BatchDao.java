package com.example.weighingscale.data.local.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.weighingscale.data.model.Batch;

import java.util.List;

@Dao
public interface BatchDao {
    @Query("SELECT * FROM Batch")
    LiveData<List<Batch>> getDatas();

    @Query("SELECT * FROM Batch WHERE status = 1 LIMIT 1")
    LiveData<Batch> getActiveBatch();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBatch(Batch batch);

    @Transaction
    @Query("UPDATE Batch SET status = 0 WHERE id = :batchId")
    void completeBatch(String batchId);

    @Transaction
    @Query("UPDATE Batch SET status = 0 WHERE status = 1")
    void completeBatch();
}
