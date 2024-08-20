package com.example.weighingscale.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "Province")
public class Province {
    @PrimaryKey
    @NonNull
    public String id;
    public String name;

    // Constructor
    public Province() {
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
