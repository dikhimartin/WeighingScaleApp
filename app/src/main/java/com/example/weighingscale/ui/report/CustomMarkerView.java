package com.example.weighingscale.ui.report;

import android.content.Context;
import android.widget.TextView;

import com.example.weighingscale.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

public class CustomMarkerView extends MarkerView {
    private TextView tvContent;

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        BarEntry barEntry = (BarEntry) e;
        int batchIndex = (int) barEntry.getX();

        // Dummy data, modify this to get actual values
        String batchLabel = "Batch " + (batchIndex + 1);
        float durationInHours = barEntry.getY();
        int hours = (int) durationInHours;
        int minutes = Math.round((durationInHours - hours) * 60);
        String duration = hours + " Jam " + minutes + " Menit";

        float speed = barEntry.getY(); // Replace this with actual speed
        String speedText = Math.round(speed) + " kg/jam";

        // Set text to display batch details
        tvContent.setText(batchLabel + "\n" + "Durasi: " + duration + "\n" + "Kecepatan: " + speedText);

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}