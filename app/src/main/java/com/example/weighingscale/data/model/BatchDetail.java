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
    public double amount;
    public double price;

    public String getId() {
        return id;
    }

    public String getBatch_id() {
        return batch_id;
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
