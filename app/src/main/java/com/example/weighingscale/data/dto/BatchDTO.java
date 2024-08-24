package com.example.weighingscale.data.dto;

import com.example.weighingscale.data.model.Batch;

public class BatchDTO extends Batch {

    // Additional Attributes that are not in Batch
    public String weighing_location_province_name;
    public String weighing_location_city_type;
    public String weighing_location_city_name;
    public String delivery_destination_province_name;
    public String delivery_destination_city_type;
    public String delivery_destination_city_name;
    public int total_amount;
    public long total_price;

    // Constructor that calls the superclass constructor
    public BatchDTO() {
        super();  // Call the Batch constructor
    }

    // Additional Getter Methods for the new fields if needed
    public int getTotalAmount() {
        return total_amount;
    }

    // You can also add methods to format or retrieve titles based on these fields
    public String getTitle() {
        return (weighing_location_city_type != null && weighing_location_city_name != null)
            ? String.format("%s %s", weighing_location_city_type, weighing_location_city_name)
            : pic_name;
    }
}
