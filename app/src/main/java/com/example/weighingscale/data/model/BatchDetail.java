package com.example.weighingscale.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "BatchDetail",
        foreignKeys = @ForeignKey(entity = Batch.class,
                parentColumns = "id",
                childColumns = "batchId",
                onDelete = ForeignKey.CASCADE))
public class BatchDetail {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int batchId;
    public Date datetime;
    public float amount;
    public float price;
}
