package com.example.weighingscale.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.weighingscale.R;
import com.example.weighingscale.util.DateTimeUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.Date;

public class FilterFragment extends BottomSheetDialogFragment {

    private TextView dateRangeEditText;
    private Date startDate;
    private Date endDate;
    private FilterListener filterListener;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_filter, container, false);

        dateRangeEditText = view.findViewById(R.id.edit_date_range);
        Button applyButton = view.findViewById(R.id.button_apply);
        TextView resetButton = view.findViewById(R.id.button_reset);

        // get argument for value filter
        if (getArguments() != null) {
            startDate = (Date) getArguments().getSerializable("start_date");
            endDate = (Date) getArguments().getSerializable("end_date");
            updateDateRangeField();
        }

        // Set listener for date range field
        dateRangeEditText.setOnClickListener(v -> showDateRangePicker());

        applyButton.setOnClickListener(v -> applyFilter());
        resetButton.setOnClickListener(v -> clearFilter());

        return view;
    }

    // Method to show  Date Range Picker
    private void showDateRangePicker() {
        DateTimeUtil.showDateRangePicker(getParentFragmentManager(), (startDate, endDate) -> {
            // Set selected start and end dates
            this.startDate = startDate;
            this.endDate = endDate;

            // Update field with selected date range
            updateDateRangeField();
        });
    }


    // Method to update the date range field's text with formatted date range
    private void updateDateRangeField() {
        if (startDate != null && endDate != null) {
            String formattedStartDate = DateTimeUtil.formatDateTime(startDate, "yyyy-MM-dd");
            String formattedEndDate = DateTimeUtil.formatDateTime(endDate, "yyyy-MM-dd");
            dateRangeEditText.setText(String.format("%s - %s", formattedStartDate, formattedEndDate));
        }
    }

    // Apply filter logic
    private void applyFilter() {
        if (filterListener != null) {
            filterListener.onFilterApplied(startDate, endDate);
        }
        dismiss();
    }

    // Clear filter logic
    private void clearFilter() {
        startDate = null;
        endDate = null;
        dateRangeEditText.setText("");
        applyFilter(); // Apply filter with null values to clear filter
    }

    // Set filter listener
    public void setFilterListener(FilterListener listener) {
        this.filterListener = listener;
    }

    // Filter listener interface
    public interface FilterListener {
        void onFilterApplied(Date startDate, Date endDate);
    }
}
