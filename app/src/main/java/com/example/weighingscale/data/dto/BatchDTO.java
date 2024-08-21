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
    public long rice_price;
    public int total_amount;
    public long total_price;
    public String weighing_location_id;
    public String weighing_location_geo;
    public String delivery_destination_id;
    public String truck_driver_name;
    public String truck_driver_phone_number;
    public int status;

    // Additional Attribute
    public String weighing_location_province_name;
    public String weighing_location_city_type;
    public String weighing_location_city_name;
    public String delivery_destination_province_name;
    public String delivery_destination_city_type;
    public String delivery_destination_city_name;

    // Getters
    public String getID() { return id; }

    public String getTitle() {
        return (weighing_location_city_type != null && weighing_location_city_name != null)
            ? String.format("%s %s", weighing_location_city_type, weighing_location_city_name)
            : pic_name;
    }

    public long getRice_price(){return rice_price;};

    public String getTruck_driver_name(){return truck_driver_name;};

    public String getPicName() { return pic_name; }

    public String getPicPhoneNumber() { return pic_phone_number; }

    public String getUnit() { return unit; }

    public Date getDatetime() { return datetime; }

    public int getTotalAmount() { return total_amount; }

}
