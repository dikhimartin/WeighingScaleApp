package com.example.weighingscale.data.local.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.model.BatchDetail;

import java.util.List;

@Dao
public interface BatchDetailDao {

    @Query("SELECT * FROM BatchDetail ORDER BY datetime DESC")
    LiveData<List<BatchDetail>> getDatas();

    @Query("SELECT * FROM BatchDetail WHERE batch_id = :batchId ORDER BY datetime DESC")
    LiveData<List<BatchDetail>> getDatasByBatchID(String batchId);

    @Query("SELECT * FROM BatchDetail WHERE batch_id = :batchId ORDER BY datetime DESC")
    List<BatchDetail> getBatchDetailsByBatchId(String batchId); // Changed from LiveData to direct query

    @Insert
    void insert(BatchDetail batchDetail);
}
