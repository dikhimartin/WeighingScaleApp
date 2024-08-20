package com.example.weighingscale.ui.history;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weighingscale.R;
import com.example.weighingscale.data.dto.BatchDTO;
import com.example.weighingscale.util.FormatterUtil;
import com.example.weighingscale.util.SafeValueUtil;

public class HistoryDetailFragment extends Fragment {

    private TextView textPICName;
    private TextView textDriver;
    private TextView textDatetime;
    private TextView textRicePrice;
    private TextView textStartDate;
    private TextView textEndDate;
    private TextView textDuration;
    private TextView textTotalWeight;
    private TextView textTotalPrice;
    private TextView textTotalItems;

    private LinearLayout layoutWeighingLocation;
    private TextView textWeighingLocationProvince;
    private TextView textWeighingLocationCity;

    private LinearLayout layoutDeliveryDestination;
    private TextView textDeliveryDestinationProvince;
    private TextView textDeliveryDestinationCity;

    private BatchDTO currentBatch;
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
        textDriver = view.findViewById(R.id.text_view_driver);
        textDatetime = view.findViewById(R.id.text_view_datetime);
        textRicePrice = view.findViewById(R.id.text_view_rice_price);
        textStartDate = view.findViewById(R.id.text_view_start_date);
        textEndDate = view.findViewById(R.id.text_view_end_date);
        textDuration = view.findViewById(R.id.text_view_duration);
        textTotalWeight = view.findViewById(R.id.text_view_total_weight);
        textTotalPrice = view.findViewById(R.id.text_view_total_price);
        textTotalItems = view.findViewById(R.id.text_view_total_item);

        layoutWeighingLocation = view.findViewById(R.id.til_weighing_location);
        textWeighingLocationProvince = view.findViewById(R.id.text_view_weighing_location_province);
        textWeighingLocationCity = view.findViewById(R.id.text_view_weighing_location_city);

        layoutDeliveryDestination = view.findViewById(R.id.til_delivery_destination);
        textDeliveryDestinationProvince = view.findViewById(R.id.text_view_delivery_destination_province);
        textDeliveryDestinationCity = view.findViewById(R.id.text_view_delivery_destination_city);
    }

    private void initializeViewModel() {
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
    }

    private void setupMode(View view) {
        Bundle args = getArguments();
        if (args != null && args.containsKey("batch_id")) {
            String batchID = args.getString("batch_id");
            assert batchID != null;
             historyViewModel.getBatchByID(batchID).observe(getViewLifecycleOwner(), batchWithCity -> {
                if (batchWithCity != null) {
                    currentBatch = batchWithCity;
                    populateBatch();
                    batchDetailList(view);
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void populateBatch() {
        // Set basic fields
        textPICName.setText(SafeValueUtil.getString(currentBatch.pic_name, "N/A"));
        textDriver.setText(SafeValueUtil.getString(currentBatch.truck_driver_name, "N/A"));
        textDatetime.setText(SafeValueUtil.getFormattedDate(currentBatch.datetime, "dd MMMM yyyy"));
        textRicePrice.setText(SafeValueUtil.formatCurrency("Rp", currentBatch.rice_price));

        // Set Weighing Location
         boolean isWeighingLocationEmpty = currentBatch.weighing_location_city_type == null &&
                              currentBatch.weighing_location_city_name == null &&
                              currentBatch.weighing_location_province_name == null;
        layoutWeighingLocation.setVisibility(isWeighingLocationEmpty ? View.GONE : View.VISIBLE);
        setLocationText(
            textWeighingLocationCity,
            currentBatch.weighing_location_city_type,
            currentBatch.weighing_location_city_name
        );
        setLocationText(
            textWeighingLocationProvince,
            "Provinsi",
            currentBatch.weighing_location_province_name
        );

        // Set Delivery Location
         boolean isDeliveryLocationEmpty = currentBatch.delivery_destination_city_type == null &&
                              currentBatch.delivery_destination_city_name == null &&
                              currentBatch.delivery_destination_province_name == null;
        layoutDeliveryDestination.setVisibility(isDeliveryLocationEmpty ? View.GONE : View.VISIBLE);
        setLocationText(
            textDeliveryDestinationCity,
            currentBatch.delivery_destination_city_type,
            currentBatch.delivery_destination_city_name
        );
        setLocationText(
            textDeliveryDestinationProvince,
            "Provinsi",
            currentBatch.delivery_destination_province_name
        );

        // Handle start and end date, and duration
        String startDateText = SafeValueUtil.getFormattedDate(currentBatch.start_date, "HH:mm:ss");
        String endDateText = (currentBatch.end_date != null) ? SafeValueUtil.getFormattedDate(currentBatch.end_date, "HH:mm:ss") : "-";
        String durationText = (currentBatch.start_date != null && currentBatch.end_date != null) ?
        FormatterUtil.formatDuration(currentBatch.end_date.getTime() - currentBatch.start_date.getTime()) : "-";

        textStartDate.setText(startDateText);
        textEndDate.setText(endDateText);
        textDuration.setText(durationText);

        // Set total weight and price
        textTotalWeight.setText(currentBatch.total_amount + " " + SafeValueUtil.getString(currentBatch.unit, "kg"));
        textTotalPrice.setText(SafeValueUtil.formatCurrency("Rp", currentBatch.total_price));
    }

     @SuppressLint("SetTextI18n")
     private void batchDetailList(View view){
        String batchID = currentBatch.id;
        // Adapter log list
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_log);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        HistoryDetailAdapter adapter = new HistoryDetailAdapter(requireContext());
        recyclerView.setAdapter(adapter);
        historyViewModel.getBatchDetails(batchID).observe(getViewLifecycleOwner(), data -> {
            adapter.submitList(data);
            int totalItems = data.size();
            textTotalItems.setText(totalItems +" "+ getString(R.string.bag));
        });
    }

    private void setLocationText(TextView textView, String type, String name) {
        String formattedText = (type != null && name != null)
            ? String.format("%s %s", SafeValueUtil.getString(type, ""), SafeValueUtil.getString(name, "N/A"))
            : "";
        textView.setText(formattedText);
        textView.setVisibility((type != null && name != null) ? View.VISIBLE : View.GONE);
    }
}
