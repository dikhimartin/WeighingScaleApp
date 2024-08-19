package com.example.weighingscale.ui.history;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.weighingscale.R;
import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.model.Note;
import com.example.weighingscale.ui.note.NoteViewModel;
import com.example.weighingscale.util.DateTimeUtil;
import com.example.weighingscale.util.FormatterUtil;
import com.example.weighingscale.util.SafeValueUtil;
import com.example.weighingscale.util.ValidationUtil;
import java.util.Date;

public class HistoryDetailFragment extends Fragment {

    private TextView textPICName;
    private TextView textDatetime;
    private TextView textRicePrice;
    private TextView textStartDate;
    private TextView textEndDate;
    private TextView textDuration;
    private TextView textTotalWeight;
    private TextView textTotalPrice;
    private HistoryViewModel historyViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_detail, container, false);
        initializeViews(view);
        initializeViewModel();
        setupMode(view);
        return view;
    }

    private void initializeViews(View view) {
        textPICName = view.findViewById(R.id.text_view_pic_name);
        textDatetime = view.findViewById(R.id.text_view_datetime);
        textRicePrice = view.findViewById(R.id.text_view_rice_price);
        textStartDate = view.findViewById(R.id.text_view_start_date);
        textEndDate = view.findViewById(R.id.text_view_end_date);
        textDuration = view.findViewById(R.id.text_view_duration);
        textTotalWeight = view.findViewById(R.id.text_view_total_weight);
        textTotalPrice = view.findViewById(R.id.text_view_total_price);
    }

    private void initializeViewModel() {
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
    }

    private void setupMode(View view) {
        Bundle args = getArguments();
        if (args != null && args.containsKey("batch_id")) {
            String batchID = args.getString("batch_id");
            historyViewModel.getBatchById(batchID).observe(getViewLifecycleOwner(), batch -> {
                if (batch != null) {
                    populateBatch(batch);
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void populateBatch(Batch batch) {
        // Set basic fields
        textPICName.setText(SafeValueUtil.getString(batch.pic_name, "N/A"));
        textDatetime.setText(SafeValueUtil.getFormattedDate(batch.datetime, "dd MMMM yyyy"));
        textRicePrice.setText(FormatterUtil.formatCurrency("Rp", SafeValueUtil.getDouble(batch.rice_price, 0.0)));

        // Handle start and end date, and duration
        String startDateText = SafeValueUtil.getFormattedDate(batch.start_date, "HH:mm:ss");
        String endDateText = (batch.end_date != null) ? SafeValueUtil.getFormattedDate(batch.end_date, "HH:mm:ss") : "-";
        String durationText = (batch.start_date != null && batch.end_date != null) ?
            FormatterUtil.formatDuration(batch.end_date.getTime() - batch.start_date.getTime()) : "-";

        textStartDate.setText(startDateText);
        textEndDate.setText(endDateText);
        textDuration.setText(durationText);

        // Set total weight and price
        textTotalWeight.setText(SafeValueUtil.getInt(batch.total_amount, 0) + " " + SafeValueUtil.getString(batch.unit, "kg"));
        textTotalPrice.setText(FormatterUtil.formatCurrency("Rp", SafeValueUtil.getDouble(batch.total_price, 0.0)));
    }

}
