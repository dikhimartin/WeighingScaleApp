package com.example.weighingscale.ui.shared;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

public class SharedAdapter extends ArrayAdapter<String> {
    public SharedAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, android.R.layout.simple_dropdown_item_1line, objects);
    }
}
