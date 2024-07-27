package com.example.weighingscale.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.util.UUID;

import java.util.Date;
import java.util.UUID;

@Entity(tableName = "BatchDetail",
        foreignKeys = @ForeignKey(entity = Batch.class,
                parentColumns = "id",
                childColumns = "batch_id",
                onDelete = ForeignKey.CASCADE))
public class BatchDetail {
    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "batch_id")
    public String batchId;

    @ColumnInfo(name = "datetime")
    public Date datetime;

    @ColumnInfo(name = "amount")
    public double amount;

    @ColumnInfo(name = "price")
    public double price;

    public String getId() {
        return id;
    }

    public String getBatchId() {
        return batchId;
    }

    public Date getDatetime() {
        return datetime;
    }

    public double getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

    // Constructor
    public BatchDetail() {
        this.id = UUID.randomUUID().toString();
    }
}
