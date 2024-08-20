package com.example.weighingscale.data.dto;

import com.example.weighingscale.data.model.Batch;

import java.util.Date;

public class BatchDTO extends Batch {
    public String id;
    public String pic_name;
    public String pic_phone_number;
    public Date datetime;
    public Date start_date;
    public Date end_date;
    public long duration;
    public String unit;
    public double rice_price;
    public int total_amount;
    public double total_price;
    public String weighing_location_id;
    public String weighing_location_geo;
    public String delivery_destination_id;
    public String truck_driver_name;
    public String truck_driver_phone_number;
    public int status;

    // Additional Attribute
    public String weighing_location_name;
    public String delivery_destination_name;

    // Getters
    public String getID() { return id; }

    public String getPicName() { return pic_name; }

    public String getPicPhoneNumber() { return pic_phone_number; }

    public String getUnit() { return unit; }

    public Date getDatetime() { return datetime; }

    public int getTotalAmount() { return total_amount; }

}
