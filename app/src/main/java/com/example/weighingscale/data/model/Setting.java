package com.example.weighingscale.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Setting")
public class Setting {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String picName;
    public String picPhoneNumber;
    public float ricePrice;
}
