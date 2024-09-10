package com.example.weighingscale.ui.report;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.example.weighingscale.R;
import com.example.weighingscale.data.dto.BatchDTO;
import com.example.weighingscale.util.DateTimeUtil;
import com.example.weighingscale.util.FormatterUtil;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.List;

@SuppressLint("ViewConstructor")
public class CustomMarkerView extends MarkerView {

    private final TextView tvContent;
//    private final List<Float> speedEntries;
//    private final List<Long> durationEntries;
//    private final List<String> labels;
    private final List<BatchDTO> batchEntries;

    public CustomMarkerView(Context context, int layoutResource, List<BatchDTO> batchEntries) {
//    public CustomMarkerView(Context context, int layoutResource, List<Float> speedEntries, List<Long> durationEntries, List<String> labels, List<BatchDTO> batch) {
        super(context, layoutResource);
//        this.speedEntries = speedEntries;
//        this.durationEntries = durationEntries;
//        this.labels = labels;
        this.batchEntries = batchEntries;
        tvContent = findViewById(R.id.tvContent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        BarEntry barEntry = (BarEntry) e;
        int batchIndex = (int) barEntry.getX();

        // Get corresponding speed and batch label
        BatchDTO batch = batchEntries.get(batchIndex);
        // float speed = speedEntries.get(batchIndex);
        // long duration = durationEntries.get(batchIndex);
        // String batchLabel = labels.get(batchIndex);

        String batchLabel =String.format("Batch :%s\n", DateTimeUtil.formatDateTime(batch.start_date, "dd/MM/yyyy HH:mm"));

        // Format
        String durationFormat = FormatterUtil.formatDuration(batch.duration);
        tvContent.setText(batchLabel + "\nDurasi: " + durationFormat);

        // Set content for the marker
        // String speedText = Math.round(speed) + " kg/jam";
        // tvContent.setText(batchLabel + "\nDurasi: " + durationFormat + "\nKecepatan: " + speedText);

        super.refreshContent(e, highlight);
    }

    public void updateData(List <BatchDTO> newBatch) {
        //    public void updateData(List<Float> newSpeedEntries, List<Long> newDurationEntries, List<String> newLabels) {
        //        this.speedEntries.clear();
        //        this.speedEntries.addAll(newSpeedEntries);
        //
        //        this.durationEntries.clear();
        //        this.durationEntries.addAll(newDurationEntries);
        //
        //        this.labels.clear();
        //        this.labels.addAll(newLabels);
        this.batchEntries.clear();
        this.batchEntries.addAll(newBatch);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-((float) getWidth() / 2), -getHeight()); // Center the marker above the point
    }
}
