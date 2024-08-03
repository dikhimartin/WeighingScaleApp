package com.example.weighingscale.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.util.UUID;

import java.util.Date;

@Entity(tableName = "BatchDetail",
        foreignKeys = @ForeignKey(entity = Batch.class,
                parentColumns = "id",
                childColumns = "batch_id",
                onDelete = ForeignKey.CASCADE))
public class BatchDetail {
    @PrimaryKey
    @NonNull
    public String id;
    public String batch_id;
    public Date datetime;
    public String unit;
    public int amount;
    public double price;

    @NonNull
    public String getId() {
        return id;
    }

    public Date getDatetime() {
        return datetime;
    }

    public double getAmount() {
        return amount;
    }

    public String getUnit(){
        return unit;
    }

    // Constructor
    public BatchDetail() {
        this.id = UUID.randomUUID().toString();
    }
}
