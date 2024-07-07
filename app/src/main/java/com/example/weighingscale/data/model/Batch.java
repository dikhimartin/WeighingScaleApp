package com.example.weighingscale.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Batch")
public class Batch {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String picName;
    public Date datetime;
    public String picPhoneNumber;
    public String destination;
    public String truckDriver;
    public String truckDriverPhoneNumber;
}
