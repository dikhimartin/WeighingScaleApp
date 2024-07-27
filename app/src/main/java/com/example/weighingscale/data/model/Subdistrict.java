package com.example.weighingscale.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Subdistrict",
        foreignKeys = {
                @ForeignKey(entity = Province.class,
                        parentColumns = "id",
                        childColumns = "province_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = City.class,
                        parentColumns = "id",
                        childColumns = "city_id",
                        onDelete = ForeignKey.CASCADE)
        })
public class Subdistrict {
    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "province_id")
    public String provinceId;

    @ColumnInfo(name = "city_id")
    public String cityId;

    @ColumnInfo(name = "name")
    public String name;
}
