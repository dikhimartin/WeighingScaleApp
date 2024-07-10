package com.example.weighingscale.data.local.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.weighingscale.data.model.BatchDetail;

import java.util.List;

@Dao
public interface BatchDetailDao {
    @Query("SELECT * FROM BatchDetail WHERE batchId = :batchId")
    List<BatchDetail> getBatchDetails(int batchId);

    @Query("SELECT * FROM BatchDetail WHERE batchId = :batchId")
    LiveData<List<BatchDetail>> getBatchDetailsByBatchId(int batchId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBatchDetail(BatchDetail batchDetail);

    @Insert
    void insert(BatchDetail batchDetail);
}
