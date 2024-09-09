package com.example.weighingscale.ui.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.weighingscale.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class ReportAverageFragment extends Fragment {

    private BarChart barChart;
    private final List<Float> speedEntries = new ArrayList<>(); // Store speeds for CustomMarkerView

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_average_weighing, container, false);
        barChart = view.findViewById(R.id.barChart);
        setupChart();
        return view;
    }

    private void setupChart() {
        List<BarEntry> durationEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            float duration = (float) (Math.random() * 10) + 1; // Duration between 1 and 10 hours
            float speed = (float) (Math.random() * 30) + 10;   // Speed between 10 and 40 kg/h

            durationEntries.add(new BarEntry(i, duration));
            speedEntries.add(speed);
            labels.add("Batch " + (i + 1));
        }

        int colorSecondary = ContextCompat.getColor(requireContext(), R.color.gold);

        // Setup BarDataSet for duration
        BarDataSet durationDataSet = new BarDataSet(durationEntries, "Durasi Penimbangan (jam)");
        durationDataSet.setColor(colorSecondary);
        durationDataSet.setValueTextSize(10f);

        BarData barData = new BarData(durationDataSet);
        barData.setBarWidth(0.6f); // Set bar width

        barChart.setData(barData);
        barChart.setFitBars(true);

        // Configure XAxis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelRotationAngle(45f); // Rotate labels to fit

        // Configure YAxis
        barChart.getAxisLeft().setGranularity(1f);
        barChart.getAxisRight().setEnabled(false); // Disable right Y-axis

        // Customize chart appearance
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(true);
        barChart.animateY(500);

        // Add Custom MarkerView for interactive data display
        CustomMarkerView mv = new CustomMarkerView(barChart.getContext(), R.layout.custom_marker_view, speedEntries, labels);
        barChart.setMarker(mv);
    }

}
