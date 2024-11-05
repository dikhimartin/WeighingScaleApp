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

    // Getter title
     public String getTitle() {
        String weighingLocation = (weighing_location_city_name != null)
            ? weighing_location_city_name
            : null;

        String deliveryDestination = (delivery_destination_city_name != null)
            ? delivery_destination_city_name
            : null;

        if (weighingLocation != null && deliveryDestination != null) {
            return weighingLocation + " - " + deliveryDestination;
        } else if (weighingLocation != null) {
            return weighingLocation;
        } else if (deliveryDestination != null) {
            return deliveryDestination;
        } else {
            return pic_name != null ? pic_name : "Unknown";
        }
    }
}
