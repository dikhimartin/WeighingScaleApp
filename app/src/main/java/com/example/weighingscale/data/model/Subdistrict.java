package com.example.weighingscale.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.UUID;

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
    public String province_id;
    public String city_id;
    public String name;

    // Constructor
    public Subdistrict() {
        this.id = UUID.randomUUID().toString();
    }

    @NonNull
    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }
}
