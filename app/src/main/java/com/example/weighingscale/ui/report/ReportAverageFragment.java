package com.example.weighingscale.ui.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weighingscale.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ReportAverageFragment extends Fragment {

    private BarChart barChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_average_weighing, container, false);
        barChart = view.findViewById(R.id.barChart);
        setupChart();
        return view;
    }

    private void setupChart() {
        // Data dummy
        List<BarEntry> durationEntries = new ArrayList<>();
        List<BarEntry> speedEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Menambahkan data dummy dengan loop
        for (int i = 0; i < 5; i++) {
            float duration = (float) (Math.random() * 10) + 1; // Durasi antara 1 dan 10 jam
            float speed = (float) (Math.random() * 30) + 10;   // Kecepatan antara 10 dan 40 kg/jam

            durationEntries.add(new BarEntry(i, duration));
            speedEntries.add(new BarEntry(i, speed));
            labels.add("Batch " + (i + 1));
        }

        // Setup data
        BarDataSet durationDataSet = new BarDataSet(durationEntries, "Durasi Penimbangan (jam)");
        durationDataSet.setColor(ColorTemplate.COLORFUL_COLORS[0]); // Warna berbeda untuk durasi
        durationDataSet.setValueTextSize(10f);

        BarDataSet speedDataSet = new BarDataSet(speedEntries, "Kecepatan Penimbangan (kg/jam)");
        speedDataSet.setColor(ColorTemplate.COLORFUL_COLORS[1]); // Warna berbeda untuk kecepatan
        speedDataSet.setValueTextSize(10f);

        BarData barData = new BarData(durationDataSet, speedDataSet);
        barData.setBarWidth(0.4f); // Lebar bar

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
        barChart.getAxisRight().setEnabled(false); // Menyembunyikan sumbu Y kanan

        // Customize chart appearance
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(true);
        barChart.animateY(500);

         // Adding Custom MarkerView for more interactive data display
         CustomMarkerView mv = new CustomMarkerView(barChart.getContext(), R.layout.custom_marker_view);
         barChart.setMarker(mv);
    }
}