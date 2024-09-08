package com.example.weighingscale.ui.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.weighingscale.R;

import java.util.Date;

public class ReportAverageWeighingSpeed extends Fragment {
    private ReportViewModel reportViewModel;
    Button filterButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_average_weighing_speed, container, false);
        initViews(view);
        setupViewModel();
        setupFilterButton();
        return view;
    }

    private void initViews(View view) {
        filterButton = view.findViewById(R.id.button_filter);
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
        reportViewModel.setFilter("start_date", startDate);
        reportViewModel.setFilter("end_date", endDate);
    }
}
