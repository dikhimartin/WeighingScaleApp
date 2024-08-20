package com.example.weighingscale.data.local.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.weighingscale.data.dto.BatchDTO;
import com.example.weighingscale.data.model.Batch;

import java.util.List;

@Dao
public interface BatchDao {

    @Query("SELECT b.id, b.pic_name, b.pic_phone_number, b.datetime, b.start_date, b.end_date, b.duration, b.unit, b.rice_price, b.total_amount, b.total_price, b.weighing_location_id, b.weighing_location_geo, b.delivery_destination_id, b.truck_driver_name, b.truck_driver_phone_number, b.status, " +
           "wl.name AS weighing_location_name, dd.name AS delivery_destination_name " +
           "FROM Batch b " +
           "LEFT JOIN City wl ON b.weighing_location_id = wl.id " +
           "LEFT JOIN City dd ON b.delivery_destination_id = dd.id " +
           "ORDER BY b.datetime DESC")
    LiveData<List<BatchDTO>> getDatas();

    @Query("SELECT b.id, b.pic_name, b.pic_phone_number, b.datetime, b.start_date, b.end_date, b.duration, b.unit, b.rice_price, b.total_amount, b.total_price, b.weighing_location_id, b.weighing_location_geo, b.delivery_destination_id, b.truck_driver_name, b.truck_driver_phone_number, b.status, " +
           "wl.name AS weighing_location_name, dd.name AS delivery_destination_name " +
           "FROM Batch b " +
           "LEFT JOIN City wl ON b.weighing_location_id = wl.id " +
           "LEFT JOIN City dd ON b.delivery_destination_id = dd.id " +
           "WHERE b.id = :id")
    LiveData<BatchDTO> getDataByID(String id);

    @Query("SELECT * FROM Batch WHERE status = 1 LIMIT 1")
    LiveData<Batch> getActiveBatch();

    @Query("SELECT * FROM Batch WHERE id = :id")
    Batch getBatchByID(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Batch batch);

    @Update
    void update(Batch batch);

    @Transaction
    @Query("UPDATE Batch SET status = 0 WHERE status = 1")
    void forceCompleteBatch();

    @Query("DELETE FROM Batch WHERE id = :id")
    void deleteByID(String id);

    @Query("DELETE FROM Batch WHERE id IN (:ids)")
    void deleteByIDs(List<String> ids);

    @Delete
    void delete(Batch batch);

    @Query("DELETE FROM Batch")
    void deleteAll();
}
