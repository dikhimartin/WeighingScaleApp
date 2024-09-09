package com.example.weighingscale.ui.report;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.example.weighingscale.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.List;

@SuppressLint("ViewConstructor")
public class CustomMarkerView extends MarkerView {

    private final TextView tvContent;
    private final List<Float> speedEntries;
    private final List<String> labels;

    public CustomMarkerView(Context context, int layoutResource, List<Float> speedEntries, List<String> labels) {
        super(context, layoutResource);
        this.speedEntries = speedEntries;
        this.labels = labels;
        tvContent = findViewById(R.id.tvContent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        BarEntry barEntry = (BarEntry) e;
        int batchIndex = (int) barEntry.getX();

        // Get corresponding speed and batch label
        float speed = speedEntries.get(batchIndex);
        String batchLabel = labels.get(batchIndex);

        // Format duration
        float durationInHours = barEntry.getY();
        int hours = (int) durationInHours;
        int minutes = Math.round((durationInHours - hours) * 60);
        String duration = hours + " Jam " + minutes + " Menit";

        // Set content for the marker
        String speedText = Math.round(speed) + " kg/jam";
        tvContent.setText(batchLabel + "\nDurasi: " + duration + "\nKecepatan: " + speedText);

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-((float) getWidth() / 2), -getHeight()); // Center the marker above the point
    }
}
