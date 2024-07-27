package com.example.weighingscale.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "City")
public class City {
    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "province_id")
    public String provinceId;

    @ColumnInfo(name = "name")
    public String name;
}
