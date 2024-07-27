package com.example.weighingscale.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Setting")
public class Setting {
    @PrimaryKey
    @NonNull
    public String picName;

    @ColumnInfo(name = "pic_phone_number")
    public String picPhoneNumber;

    @ColumnInfo(name = "rice_price")
    public float ricePrice;

    @ColumnInfo(name = "unit")
    public String unit; // 'g' or 'kg'
}
