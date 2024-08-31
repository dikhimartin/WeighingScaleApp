package com.example.weighingscale.data.dto;

import com.example.weighingscale.data.model.City;

public class AddressDTO extends City {
    // Additional Attributes
    public String city_type;
    public String city_name;
    public String province_name;

    // Constructor that calls the superclass constructor
    public AddressDTO() {
        super();  // Call the Batch constructor
    }

    // Additional Getter
    public String getCityType(){
        return city_type;
    }

    public String getCityName(){
        return city_name;
    }

    public String getProvinceName(){
        return province_name;
    }
}


