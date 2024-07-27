package com.example.weighingscale.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.util.UUID;

import java.util.Date;

@Entity(tableName = "Batch",
        foreignKeys = @ForeignKey(entity = Subdistrict.class,
                parentColumns = "id",
                childColumns = "weighing_location_id",
                onDelete = ForeignKey.CASCADE))
public class Batch {
    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "pic_name")
    public String picName;

    @ColumnInfo(name = "pic_phone_number")
    public String picPhoneNumber;

    @ColumnInfo(name = "datetime")
    public Date datetime;

    @ColumnInfo(name = "weighing_location_id")
    public String weighingLocationId;

    @ColumnInfo(name = "weighing_location_geo")
    public String weighingLocationGeo;

    @ColumnInfo(name = "delivery_destination_id")
    public String deliveryDestinationId;

    @ColumnInfo(name = "truck_driver_name")
    public String truckDriverName;

    @ColumnInfo(name = "truck_driver_phone_number")
    public String truckDriverPhoneNumber;

    public int status; // 1 for active, 0 for completed

    // Constructor
    public Batch() {
        this.id = UUID.randomUUID().toString();
    }
}
