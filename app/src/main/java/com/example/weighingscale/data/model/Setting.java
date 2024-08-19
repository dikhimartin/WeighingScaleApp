package com.example.weighingscale.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Setting")
public class Setting {
    @PrimaryKey
    @NonNull
    public String pic_name;
    public String pic_phone_number;
    public double rice_price;
    public String unit; // 'g' or 'kg'
}
