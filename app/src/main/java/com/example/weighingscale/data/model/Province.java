package com.example.weighingscale.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Province")
public class Province {
    @PrimaryKey
    @NonNull
    public String id;
    public String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
