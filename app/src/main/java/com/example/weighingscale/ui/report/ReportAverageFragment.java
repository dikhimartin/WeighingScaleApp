package com.example.weighingscale.ui.report;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.weighingscale.R;
import com.example.weighingscale.data.dto.BatchDTO;
import com.example.weighingscale.util.DateTimeUtil;
import com.example.weighingscale.util.FormatterUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportAverageFragment extends Fragment {
    private ReportViewModel reportViewModel;
    private BarChart barChart;
    private Button filterButton;
    private TextView dateTextView;
    private final List<Float> speedEntries = new ArrayList<>();    // Store speeds for CustomMarkerView
    private final List<Long> durationEntries = new ArrayList<>();  // Store duration for CustomMarkerView

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_average_weighing, container, false);
        initViews(view);
        setupViewModel();
        setupFilterButton();
        observeReportData();
        return view;
    }

    private void initViews(View view) {
        filterButton = view.findViewById(R.id.button_filter);
        barChart = view.findViewById(R.id.barChart);
        dateTextView = view.findViewById(R.id.text_date_range);
    }

    private void setupViewModel() {
        reportViewModel = new ViewModelProvider(this).get(ReportViewModel.class);
    }

    private void setupFilterButton() {
        filterButton.setOnClickListener(v -> {
            FilterFragment filterFragment = new FilterFragment();

            // Set Bundle to send data
            Bundle args = new Bundle();
            args.putSerializable("start_date", (Date) reportViewModel.getFilter("start_date"));
            args.putSerializable("end_date", (Date) reportViewModel.getFilter("end_date"));
            filterFragment.setArguments(args);

            // Set listener to get result filter
            filterFragment.setFilterListener(this::applyFilters);
            filterFragment.show(getParentFragmentManager(), filterFragment.getTag());
        });
    }

    private void applyFilters(Date startDate, Date endDate) {
        if (startDate == null) startDate = getDefaultStartDate();
        if (endDate == null) endDate = new Date();

        reportViewModel.setFilter("start_date", startDate);
        reportViewModel.setFilter("end_date", endDate);

        observeReportData(); // Reload the chart after applying filters
    }

    private void observeReportData() {
        Date startDate = (Date) reportViewModel.getFilter("start_date");
        Date endDate = (Date) reportViewModel.getFilter("end_date");

        if (endDate == null) {
            startDate = getDefaultStartDate();
            endDate = new Date();
            applyFilters(startDate, endDate);
        }

        dateTextView.setText(DateTimeUtil.formatDateRange(startDate, endDate));

        reportViewModel.getAllBatch(null, startDate, endDate, null)
            .observe(getViewLifecycleOwner(), this::setupChart);
    }

    private void setupChart(List<BatchDTO> batchList) {
        if (barChart == null || batchList == null || batchList.isEmpty()) return;

        List<BarEntry> barEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Float> newSpeedEntries = new ArrayList<>();
        List<Long> newDurationEntries = new ArrayList<>();

        for (int i = 0; i < batchList.size(); i++) {
            BatchDTO batch = batchList.get(i);

            float speed = 200f + (i * 50f);           // Speed in kg/hour
            float durationInHours = WeighingUtils.convertDurationToBarChartFormat(batch.duration);
            barEntries.add(new BarEntry(i, durationInHours));

            newDurationEntries.add(batch.duration);
            newSpeedEntries.add(speed);
            labels.add("Batch " + (i + 1));
        }

        int colorSecondary = ContextCompat.getColor(requireContext(), R.color.gold);
        BarDataSet durationDataSet = new BarDataSet(barEntries, "Durasi Penimbangan (jam)");
        durationDataSet.setColor(colorSecondary);
        durationDataSet.setValueTextSize(10f);

        BarData barData = new BarData(durationDataSet);
        barData.setBarWidth(0.6f);
        barChart.setData(barData);
        barChart.setFitBars(true);
        setupAxes(labels);
        barChart.animateY(500);

        // Update data in CustomMarkerView and set it again
        CustomMarkerView mv = (CustomMarkerView) barChart.getMarker();
        if (mv != null) {
            mv.updateData(newSpeedEntries, newDurationEntries, labels);
        } else {
            mv = new CustomMarkerView(barChart.getContext(), R.layout.custom_marker_view, newSpeedEntries, newDurationEntries, labels);
            barChart.setMarker(mv);
        }
    }


    private void setupAxes(List<String> labels) {
        // Bottom Axis
        // XAxis xAxis = barChart.getXAxis();
        // xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        // xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // xAxis.setGranularity(1f);
        // xAxis.setLabelRotationAngle(45f);

        // Left Axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @SuppressLint("DefaultLocale")
            @Override
            public String getFormattedValue(float value) {
                return String.format("%d Jam", (int) value);
            }
        });
        barChart.getAxisRight().setEnabled(false);
    }

    private Date getDefaultStartDate() {
        Calendar calendar = Calendar.getInstance();
        // set time to 00:00:00
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

}
