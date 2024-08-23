package com.example.weighingscale.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "Setting")
public class Setting {
    @PrimaryKey
    @NonNull
    public String id;
    public String pic_name;
    public String pic_phone_number;
    public long rice_price;
    public String unit; // 'g', 'mg' or 'kg'

    // Constructor
    public Setting() {
        this.id = UUID.randomUUID().toString();
    }
}
