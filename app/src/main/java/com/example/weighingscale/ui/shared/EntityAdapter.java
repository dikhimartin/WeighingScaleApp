package com.example.weighingscale.ui.shared;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class EntityAdapter extends ArrayAdapter<SelectOptionWrapper> {
    private final LayoutInflater inflater;

    public EntityAdapter(Context context, List<SelectOptionWrapper> entities) {
        super(context, android.R.layout.simple_dropdown_item_1line, entities);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        SelectOptionWrapper item = getItem(position);
        textView.setText(item != null ? item.getName() : "");
        return convertView;
    }
}
