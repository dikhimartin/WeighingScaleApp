package com.example.weighingscale.ui.report;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import com.github.mikephil.charting.charts.BarChart;
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

    // Inflate the layout and initialize views for the report fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_average_weighing, container, false);
        initViews(view); // Initialize UI elements
        setupViewModel(); // Initialize the ViewModel
        setupFilterButton(); // Set up filter button functionality
        observeReportData(); // Observe data and populate the chart
        return view;
    }

    // Initialize UI components like button, chart, and text view
    private void initViews(View view) {
        filterButton = view.findViewById(R.id.button_filter);
        barChart = view.findViewById(R.id.barChart);
        dateTextView = view.findViewById(R.id.text_date_range);
    }

    // Set up the ViewModel to interact with data
    private void setupViewModel() {
        reportViewModel = new ViewModelProvider(this).get(ReportViewModel.class);
    }

    // Configure the filter button to open the filter fragment and apply date filters
    private void setupFilterButton() {
        filterButton.setOnClickListener(v -> {
            FilterFragment filterFragment = new FilterFragment();

            // Bundle current filter data (start and end dates) to send to the FilterFragment
            Bundle args = new Bundle();
            args.putSerializable("start_date", (Date) reportViewModel.getFilter("start_date"));
            args.putSerializable("end_date", (Date) reportViewModel.getFilter("end_date"));
            filterFragment.setArguments(args);

            // Set the listener to apply filters after selecting dates
            filterFragment.setFilterListener(this::applyFilters);
            filterFragment.show(getParentFragmentManager(), filterFragment.getTag());
        });
    }

    // Apply the selected filters to update the chart data
    private void applyFilters(Date startDate, Date endDate) {
        if (startDate == null) startDate = getDefaultStartDate(); // Set default start date if not selected
        if (endDate == null) endDate = new Date(); // Default to the current date if end date is not selected

        // Store the selected filters in the ViewModel
        reportViewModel.setFilter("start_date", startDate);
        reportViewModel.setFilter("end_date", endDate);

        observeReportData(); // Reload the data and chart after filters are applied
    }

    // Observe and fetch report data within the selected date range
    private void observeReportData() {
        Date startDate = (Date) reportViewModel.getFilter("start_date");
        Date endDate = (Date) reportViewModel.getFilter("end_date");

        // Set default date range if not already defined
        if (endDate == null) {
            startDate = getDefaultStartDate();
            endDate = new Date();
            applyFilters(startDate, endDate);
        }

        // Update date range text in the UI
        dateTextView.setText(DateTimeUtil.formatDateRange(startDate, endDate));

        // Fetch and observe all batches within the selected date range and update the chart
        reportViewModel.getAllBatch(null, startDate, endDate, null)
            .observe(getViewLifecycleOwner(), this::setupChart);
    }

    // Set up the chart with the list of BatchDTO data
    private void setupChart(List<BatchDTO> batchList) {
        if (barChart == null || batchList == null || batchList.isEmpty()) return; // Check for valid data

        List<BarEntry> barEntries = createBarEntries(batchList); // Create bar chart entries from batch data
        BarData barData = createBarData(barEntries); // Create the bar chart data
        barChart.setData(barData); // Set data to the chart
        barChart.setFitBars(true); // Ensure bars are properly aligned within the chart
        barChart.animateY(500); // Add animation for a smooth appearance
        setupAxes(); // Set up chart axes
        setupMarkerView(batchList); // Attach custom marker view to display batch details on tap
    }

    // Convert the batch list to bar chart entries representing the duration in hours
    private List<BarEntry> createBarEntries(List<BatchDTO> batchList) {
        List<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < batchList.size(); i++) {
            float durationInHours = WeighingUtils.convertDurationToBarChartFormat(batchList.get(i).duration); // Convert duration
            barEntries.add(new BarEntry(i, durationInHours)); // Add entry for each batch
        }
        return barEntries;
    }

    // Create and format the bar chart data set
    private BarData createBarData(List<BarEntry> barEntries) {
        BarDataSet durationDataSet = new BarDataSet(barEntries, "Durasi Penimbangan (x jam x menit)"); // Create dataset for bar chart
        durationDataSet.setColor(ContextCompat.getColor(requireContext(), R.color.gold)); // Set bar color
        durationDataSet.setValueTextSize(10f); // Set value text size

        // Create BarData object and set the value formatter for custom display format
        BarData barData = new BarData(durationDataSet);
        barData.setBarWidth(0.6f); // Set bar width for better visibility
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return formatDuration(value); // Format bar values into "X Jam X Menit"
            }
        });
        return barData;
    }

    // Set up the custom marker view that displays batch information when a bar is selected
    private void setupMarkerView(List<BatchDTO> batchEntries) {
        MarkerViewReportAverage mv = (MarkerViewReportAverage) barChart.getMarker();
        if (mv != null) {
            mv.updateData(batchEntries); // Update marker with the current batch data
        } else {
            mv = new MarkerViewReportAverage(barChart.getContext(), R.layout.custom_marker_view_report_average_weighing, batchEntries);
            barChart.setMarker(mv); // Attach the marker view to the chart
        }
    }

    // Configure the chart's axes, including axis granularity and formatting
    private void setupAxes() {
        // Configure the left Y-axis for displaying hours
        // Bottom Axis
        // XAxis xAxis = barChart.getXAxis();
        // xAxis.setValueFormatter(new IndexAxisValueFormatter("labels name"));
        // xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // xAxis.setGranularity(1f);
        // xAxis.setLabelRotationAngle(45f);

        // Left Axis
        // YAxis leftAxis = barChart.getAxisLeft();
        // leftAxis.setGranularity(1f);  // Only display full hours
        // leftAxis.setValueFormatter(new ValueFormatter() {
        //     @SuppressLint("DefaultLocale")
        //     @Override
        //     public String getFormattedValue(float value) {
        //         return String.format("%d Jam", (int) value);  // Display as "X Jam"
        //     }
        // });
        barChart.getAxisRight().setEnabled(false); // Disable the right axis since it's not needed
    }

    // Get the default start date for filtering, which is set to the beginning of the current day
    private Date getDefaultStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime(); // Return date set to midnight
    }

    // Format duration from float (hours) into "X Jam X Menit" or "X Menit"
    @SuppressLint("DefaultLocale")
    public String formatDuration(float value) {
        long hours = (long) value; // Get the whole number of hours
        long minutes = Math.round((value - hours) * 60); // Convert the decimal part into minutes

        // Return formatted string based on whether there are hours or minutes
        if (hours == 0) {
            return String.format("%d Menit", minutes); // Only display minutes if no hours
        } else if (minutes == 0) {
            return String.format("%d Jam", hours); // Only display hours if no minutes
        } else {
            return String.format("%d Jam %02d Menit", hours, minutes); // Display both hours and minutes
        }
    }
}
