package com.example.weighingscale.ui.report;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.weighingscale.R;
import com.example.weighingscale.data.dto.BatchDTO;
import com.example.weighingscale.data.dto.ReportDTO;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;


public class ReportAverage extends Fragment {
    protected BarChart barChart;
    private ReportViewModel reportViewModel;
    Button filterButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_average_weighing, container, false);
        Log.d("MODULE_REPORT_fragment", "Layout inflated: " + view);

        initViews(view);
        setupViewModel();
        setupFilterButton();
        observeReportData();
        return view;
    }

    private void initViews(View view) {
        Log.d("MODULE_REPORT_fragment", "View is: " + view);

        filterButton = view.findViewById(R.id.button_filter);
        barChart = view.findViewById(R.id.bar_chart);

        if (filterButton == null) {
            Log.e("MODULE_REPORT_fragment", "FilterButton is null after findViewById");
        } else {
            Log.d("MODULE_REPORT_fragment", "FilterButton initialized successfully");
        }

        if (barChart == null) {
            Log.e("MODULE_REPORT_fragment", "BarChart is null after findViewById");
        } else {
            Log.d("MODULE_REPORT_fragment", "BarChart initialized successfully");
        }
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
            filterFragment.setFilterListener((startDate, endDate) -> {
                applyFilters(startDate, endDate);
            });
            filterFragment.show(getParentFragmentManager(), filterFragment.getTag());
        });
    }

    private void applyFilters(Date startDate, Date endDate) {
        if (startDate == null) {
            startDate = getDefaultStartDate(); // Ambil tanggal 7 hari yang lalu
        }
        if (endDate == null) {
            endDate = new Date(); // Set tanggal hari ini jika endDate null
        }

        reportViewModel.setFilter("start_date", startDate);
        reportViewModel.setFilter("end_date", endDate);
    }

    private void observeReportData() {
        Date startDate = (Date) reportViewModel.getFilter("start_date");
        Date endDate = (Date) reportViewModel.getFilter("end_date");

        // Jika tidak ada tanggal yang diset, gunakan 7 hari terakhir sebagai default
        if (startDate == null || endDate == null) {
            startDate = getDefaultStartDate();
            endDate = new Date(); // Tanggal hari ini
            applyFilters(startDate, endDate); // Simpan filter ke ViewModel
        }

        reportViewModel.getAllBatch(null, startDate, endDate, null)
            .observe(getViewLifecycleOwner(), batchDTO -> {
                if (batchDTO != null) {
                    displayChart(batchDTO); // Tampilkan chart
                }
            });
    }

    private Date getDefaultStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7); // 7 hari yang lalu
        return calendar.getTime();
    }

    private void displayChart(List<BatchDTO> batchList) {
        if (barChart == null || batchList == null || batchList.isEmpty()) {
            return;
        }

        ArrayList<BarEntry> durationEntries = new ArrayList<>();
        ArrayList<BarEntry> speedEntries = new ArrayList<>();
        ArrayList<String> batchLabels = new ArrayList<>(); // For dynamic batch labels

        // Create dummy data and batch labels dynamically
        for (int i = 0; i < batchList.size(); i++) {
            BatchDTO batch = batchList.get(i);

            // Example dummy values (Replace with actual values in real data)
            float durationInMinutes = 80f + (i * 30); // Total duration in minutes
            float speed = 200f + (i * 50f);           // Speed in kg/hour

            // Convert duration to hours and minutes (e.g., 80 minutes becomes 1 hour 20 minutes)
            float durationInHours = durationInMinutes / 60f;

            durationEntries.add(new BarEntry(i, durationInHours)); // Add duration entry
            speedEntries.add(new BarEntry(i, speed));              // Add speed entry

            // Add batch label dynamically
            batchLabels.add("Batch " + (i + 1));
        }

        // Creating BarDataSet for Duration Penimbangan
        BarDataSet durationSet = new BarDataSet(durationEntries, "Durasi Penimbangan");
        durationSet.setColor(Color.BLUE);
        durationSet.setAxisDependency(YAxis.AxisDependency.LEFT); // Assign to left y-axis

        // Creating BarDataSet for Kecepatan Penimbangan
        BarDataSet speedSet = new BarDataSet(speedEntries, "Kecepatan Penimbangan");
        speedSet.setColor(Color.RED);
        speedSet.setAxisDependency(YAxis.AxisDependency.RIGHT); // Assign to right y-axis

        // Combine the datasets into a BarData object
        BarData barData = new BarData(durationSet, speedSet);

        // Adjust bar width (adjust based on your needs)
        float groupSpace = 0.4f; // Space between groups of bars (batches)
        float barSpace = 0.05f;  // Space between bars within a group
        float barWidth = 0.4f;   // Width of each bar (duration and speed)

        // Set bar width
        barData.setBarWidth(barWidth);

        // Set the data to the chart
        barChart.setData(barData);

        // Group the bars
        barChart.groupBars(0f, groupSpace, barSpace); // Grouping starts from index 0

        // Format the x-axis labels dynamically based on batchList size
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f); // Ensure proper label spacing
        xAxis.setValueFormatter(new IndexAxisValueFormatter(batchLabels)); // Use dynamic labels

        // Configure Y-axis for duration (left) and speed (right)
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(1f);  // Set granularity for left axis (duration in hours)
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Convert float hours back to "x Jam y Menit"
                int hours = (int) value;
                int minutes = Math.round((value - hours) * 60);
                return hours + " Jam " + minutes + " Menit";
            }
        });

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setGranularity(50f);  // Set granularity for right axis (speed in kg/hour)
        rightAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return Math.round(value) + " kg/jam";
            }
        });

        // Adding Custom MarkerView for more interactive data display
        CustomMarkerView mv = new CustomMarkerView(barChart.getContext(), R.layout.custom_marker_view);
        barChart.setMarker(mv);

        // Refresh the chart
        barChart.invalidate();  // Re-draw the chart with the grouped bars
    }



}
