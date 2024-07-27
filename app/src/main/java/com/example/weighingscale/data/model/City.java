package com.example.weighingscale.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "City")
public class City {
    @PrimaryKey
    @NonNull
    public String id;
    public String province_id;
    public String postal_code;
    public String type;
    public String name;
}
