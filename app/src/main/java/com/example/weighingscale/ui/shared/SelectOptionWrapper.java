package com.example.weighingscale.ui.shared;

import androidx.annotation.NonNull;

public class SelectOptionWrapper {
    private final String id;
    private final String name;

    public SelectOptionWrapper(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        return name; // This will be displayed in the AutoCompleteTextView
    }
}
