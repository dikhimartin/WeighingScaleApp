package com.example.weighingscale.data.model;

import androidx.annotation.NonNull;
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
    public int amount;

    // Constructor
    public BatchDetail() {
        this.id = UUID.randomUUID().toString();
    }

    @NonNull
    public String getID() {
        return id;
    }

    public Date getDatetime() {
        return datetime;
    }

    public int getAmount() {
        return amount;
    }
}
