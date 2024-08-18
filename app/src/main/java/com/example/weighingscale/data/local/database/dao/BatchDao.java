package com.example.weighingscale.data.local.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.weighingscale.data.model.Batch;

import java.util.List;

@Dao
public interface BatchDao {
    @Query("SELECT * FROM Batch")
    LiveData<List<Batch>> getDatas();

    @Query("SELECT * FROM Batch WHERE id = :id")
    LiveData<Batch> getDataById(String id);

    @Query("SELECT * FROM Batch WHERE status = 1 LIMIT 1")
    LiveData<Batch> getActiveBatch();

    @Query("SELECT * FROM Batch WHERE id = :id")
    Batch getBatchById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBatch(Batch batch);

    @Update
    void updateBatch(Batch batch);

    @Transaction
    @Query("UPDATE Batch SET status = 0 WHERE status = 1")
    void forceCompleteBatch();

    @Query("DELETE FROM Batch WHERE id IN (:ids)")
    void deleteByIds(List<String> ids);

    @Delete
    void delete(Batch batch);

    @Query("DELETE FROM Batch")
    void deleteAll();
}
