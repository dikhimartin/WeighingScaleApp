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

    @Query(
        "SELECT " +
        "    b.id, " +
        "    b.pic_name, " +
        "    b.pic_phone_number, " +
        "    b.datetime, " +
        "    b.start_date, " +
        "    b.end_date, " +
        "    b.duration, " +
        "    b.unit, " +
        "    b.rice_price, " +
        "    b.total_amount, " +
        "    b.total_price, " +
        "    b.weighing_location_id, " +
        "    b.weighing_location_geo, " +
        "    b.delivery_destination_id, " +
        "    b.truck_driver_name, " +
        "    b.truck_driver_phone_number, " +
        "    b.status, " +
        "    wlp.name AS weighing_location_province_name, " +
        "    wlc.name AS weighing_location_city_name, " +
        "    wlc.type AS weighing_location_city_type, " +
        "    ddp.name AS delivery_destination_province_name, " +
        "    ddc.name AS delivery_destination_city_name, " +
        "    ddc.type AS delivery_destination_city_type " +
        "FROM Batch b " +
        "LEFT JOIN City wlc ON b.weighing_location_id = wlc.id " +
        "LEFT JOIN Province wlp ON wlc.province_id = wlp.id " +
        "LEFT JOIN City ddc ON b.delivery_destination_id = ddc.id " +
        "LEFT JOIN Province ddp ON ddc.province_id = ddp.id " +
        "ORDER BY b.datetime DESC"
    )
    LiveData<List<BatchDTO>> getDatas();

    @Query(
        "SELECT " +
        "    b.id, " +
        "    b.pic_name, " +
        "    b.pic_phone_number, " +
        "    b.datetime, " +
        "    b.start_date, " +
        "    b.end_date, " +
        "    b.duration, " +
        "    b.unit, " +
        "    b.rice_price, " +
        "    b.total_amount, " +
        "    b.total_price, " +
        "    b.weighing_location_id, " +
        "    b.weighing_location_geo, " +
        "    b.delivery_destination_id, " +
        "    b.truck_driver_name, " +
        "    b.truck_driver_phone_number, " +
        "    b.status, " +
        "    wlp.name AS weighing_location_province_name, " +
        "    wlc.name AS weighing_location_city_name, " +
        "    wlc.type AS weighing_location_city_type, " +
        "    ddp.name AS delivery_destination_province_name, " +
        "    ddc.name AS delivery_destination_city_name, " +
        "    ddc.type AS delivery_destination_city_type " +
        "FROM Batch b " +
        "LEFT JOIN City wlc ON b.weighing_location_id = wlc.id " +
        "LEFT JOIN Province wlp ON wlc.province_id = wlp.id " +
        "LEFT JOIN City ddc ON b.delivery_destination_id = ddc.id " +
        "LEFT JOIN Province ddp ON ddc.province_id = ddp.id " +
        "WHERE b.id = :id"
    )
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
