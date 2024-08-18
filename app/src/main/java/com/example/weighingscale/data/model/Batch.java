package com.example.weighingscale.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.util.UUID;

import java.util.Date;

@Entity(tableName = "Batch",
        foreignKeys = {
                @ForeignKey(entity = City.class,
                        parentColumns = "id",
                        childColumns = "weighing_location_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = City.class,
                        parentColumns = "id",
                        childColumns = "delivery_destination_id",
                        onDelete = ForeignKey.CASCADE)
        })
public class Batch {
    @PrimaryKey
    @NonNull
    public String id;
    public String pic_name;
    public String pic_phone_number;
    public Date datetime;
    public Date start_date;
    public Date end_date;
    public long duration; // Duration in milliseconds
    public String unit;
    public int total_amount;
    public double total_price;
    public String weighing_location_id;
    public String weighing_location_geo;
    public String delivery_destination_id;
    public String truck_driver_name;
    public String truck_driver_phone_number;
    public int status; // 1 for active, 0 for completed

    // Constructor
    public Batch() {
        this.id = UUID.randomUUID().toString();
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getPicName() {
        return pic_name;
    }

    public String getPicPhoneNumber() {
        return pic_phone_number;
    }

    public String getUnit() {
        return unit;
    }

    public Date getDatetime() {
        return datetime;
    }

    public Date getStartDate() {
        return start_date;
    }

    public Date getEndDate() {
        return end_date;
    }

    public int getTotalAmount() {
        return total_amount;
    }

    public double getTotalPrice() {
        return total_price;
    }
}
