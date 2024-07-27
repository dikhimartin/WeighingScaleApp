package com.example.weighingscale.ui.setting;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

public class UnitAdapter extends ArrayAdapter<String> {

    public UnitAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, android.R.layout.simple_dropdown_item_1line, objects);
    }
}
