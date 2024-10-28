package com.example.weighingscale.data.local.database.dao;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import java.util.Date;

import com.example.weighingscale.data.dto.BatchDTO;
import com.example.weighingscale.data.model.Batch;

import java.util.List;

@Dao
public interface BatchDao {

    @Query("SELECT b.id, b.pic_name, b.pic_phone_number, b.datetime, b.start_date, b.end_date, " +
           "b.duration, b.unit, b.rice_price, SUM(IFNULL(bd.amount, 0)) AS total_amount, " +
           "SUM(IFNULL(bd.amount, 0)) * b.rice_price AS total_price, b.weighing_location_id, " +
           "b.delivery_destination_id, b.truck_driver_name, " +
           "b.truck_driver_phone_number, b.status, wlp.name AS weighing_location_province_name, " +
           "wlc.name AS weighing_location_city_name, wlc.type AS weighing_location_city_type, " +
           "ddp.name AS delivery_destination_province_name, ddc.name AS delivery_destination_city_name, " +
           "ddc.type AS delivery_destination_city_type " +
           "FROM Batch b " +
           "LEFT JOIN BatchDetail bd ON b.id = bd.batch_id " +
           "LEFT JOIN City wlc ON b.weighing_location_id = wlc.id " +
           "LEFT JOIN Province wlp ON wlc.province_id = wlp.id " +
           "LEFT JOIN City ddc ON b.delivery_destination_id = ddc.id " +
           "LEFT JOIN Province ddp ON ddc.province_id = ddp.id " +
           "WHERE (:searchQuery IS NULL OR " +
           "       b.pic_name LIKE :searchQuery OR " +
           "       b.pic_phone_number LIKE :searchQuery OR " +
           "       b.truck_driver_name LIKE :searchQuery OR " +
           "       b.truck_driver_phone_number LIKE :searchQuery OR " +
           "       wlp.name LIKE :searchQuery OR " +
           "       wlc.name LIKE :searchQuery OR " +
           "       ddc.name LIKE :searchQuery) " +
           "AND (:startDate IS NULL OR :endDate IS NULL OR " +
           "     b.start_date BETWEEN :startDate AND :endDate) " +
           "GROUP BY b.id")
    LiveData<List<BatchDTO>> getDatas(@Nullable String searchQuery, @Nullable Date startDate, @Nullable Date endDate);

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
        "    SUM(IFNULL(bd.amount, 0)) AS total_amount, " +
        "    SUM(IFNULL(bd.amount, 0)) * b.rice_price AS total_price, " +
        "    b.weighing_location_id, " +
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
        "LEFT JOIN BatchDetail bd ON b.id = bd.batch_id " +
        "LEFT JOIN City wlc ON b.weighing_location_id = wlc.id " +
        "LEFT JOIN Province wlp ON wlc.province_id = wlp.id " +
        "LEFT JOIN City ddc ON b.delivery_destination_id = ddc.id " +
        "LEFT JOIN Province ddp ON ddc.province_id = ddp.id " +
        "WHERE b.id = :id " +
        "GROUP BY b.id " +
        "ORDER BY b.datetime DESC"
    )
    LiveData<BatchDTO> getDataByID(String id);

    @Query("SELECT * FROM Batch")
    LiveData<List<Batch>> getAllBatch();

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

    @Query("DELETE FROM Batch WHERE id IN (:ids)")
    void deleteByIDs(List<String> ids);

    @Delete
    void delete(Batch batch);

    @Query("DELETE FROM Batch")
    void deleteAll();
}
