package com.example.weighingscale.ui.history;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weighingscale.R;
import com.example.weighingscale.data.dto.BatchDTO;
import com.example.weighingscale.data.model.City;
import com.example.weighingscale.data.model.Province;
import com.example.weighingscale.ui.shared.EntityAdapter;
import com.example.weighingscale.ui.shared.LocationViewModel;
import com.example.weighingscale.ui.shared.SelectOptionWrapper;
import com.example.weighingscale.util.DateTimeUtil;
import com.example.weighingscale.util.FormatterUtil;
import com.example.weighingscale.util.SafeValueUtil;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

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
    private LocationViewModel locationViewModel;
    private MaterialCardView cardSummary;

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

        // Initialize listener
        cardSummary = view.findViewById(R.id.card_summary);
        cardSummary.setOnClickListener(v -> showUpdateBatchDialog(currentBatch));
    }

    private void initializeViewModel() {
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
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
        String startDateText = SafeValueUtil.getFormattedDate(currentBatch.start_date, "dd/MM/yyyy HH:mm:ss");
        String endDateText = (currentBatch.end_date != null) ? SafeValueUtil.getFormattedDate(currentBatch.end_date, "dd/MM/yyyy HH:mm:ss") : "-";
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

    @SuppressLint("SetTextI18n")
    private void showUpdateBatchDialog(BatchDTO batch) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input_batch, null);

        TextInputEditText etPICName = dialogView.findViewById(R.id.et_pic_name);
        TextInputEditText etPICPhoneNumber = dialogView.findViewById(R.id.et_pic_phone_number);
        TextInputEditText etTruckDriver = dialogView.findViewById(R.id.et_truck_driver);
        TextInputEditText etTruckDriverPhoneNumber = dialogView.findViewById(R.id.et_truck_driver_phone_number);
        TextInputEditText tvDatetime = dialogView.findViewById(R.id.tv_datetime);
        AutoCompleteTextView selectLocProvince = dialogView.findViewById(R.id.select_weighing_location_province);
        AutoCompleteTextView selectLocCity = dialogView.findViewById(R.id.select_weighing_location_city);
        AutoCompleteTextView selectDestProvince = dialogView.findViewById(R.id.select_destination_province);
        AutoCompleteTextView selectDestCity = dialogView.findViewById(R.id.select_destination_city);

        // Autofill data from batch
        etPICName.setText(batch.pic_name);
        etPICPhoneNumber.setText(batch.pic_phone_number);
        etTruckDriver.setText(batch.truck_driver_name);
        etTruckDriverPhoneNumber.setText(batch.truck_driver_phone_number);

        // Autofill datetime
        String dateTime = DateTimeUtil.formatDateTime(batch.datetime, "yyyy-MM-dd HH:mm:ss");
        tvDatetime.setText(dateTime);

        // Setup AutoCompleteTextView Weighing Location with adapter
        setupOptionLocation(selectLocProvince, selectLocCity);

        // Setup AutoCompleteTextView Destination with adapter
        setupOptionLocation(selectDestProvince, selectDestCity);

        // Set up datetime picker
        tvDatetime.setOnClickListener(view -> DateTimeUtil.showDateTimePicker(getChildFragmentManager(), tvDatetime));

        // Variable to store selected city's ID
        final String[] deliveryDestinationID = new String[1];
        selectDestCity.setOnItemClickListener((parent, view, position, id) -> {
            SelectOptionWrapper selectedCity = (SelectOptionWrapper) parent.getAdapter().getItem(position);
            if (selectedCity != null) {
                deliveryDestinationID[0] = selectedCity.getId();
            }
        });

        final String[] weighingLocationID = new String[1];
        selectLocCity.setOnItemClickListener((parent, view, position, id) -> {
            SelectOptionWrapper selectedCity = (SelectOptionWrapper) parent.getAdapter().getItem(position);
            if (selectedCity != null) {
                weighingLocationID[0] = selectedCity.getId();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView)
            .setTitle("Mengubah data batch muatan")
            .setPositiveButton("Ubah", (dialog, id) -> {
                String picName = etPICName.getText().toString();
                String picPhoneNumber = etPICPhoneNumber.getText().toString();
                String truckDriver = etTruckDriver.getText().toString();
                String truckDriverPhoneNumber = etTruckDriverPhoneNumber.getText().toString();

                batch.pic_name = picName;
                batch.pic_phone_number = picPhoneNumber;
                batch.truck_driver_name = truckDriver;
                batch.truck_driver_phone_number = truckDriverPhoneNumber;
                if (deliveryDestinationID[0] != null) {
                    batch.delivery_destination_id = deliveryDestinationID[0];
                }
                if (weighingLocationID[0] != null) {
                    batch.weighing_location_id = weighingLocationID[0];
                }

                // Save the updated batch
                // historyViewModel.updateBatch(batch);
                Toast.makeText(requireContext(), "Data batch muatan telah diubah", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Batal", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupOptionLocation(AutoCompleteTextView selectProvince, AutoCompleteTextView selectCity) {
        locationViewModel.getAllProvinces().observe(getViewLifecycleOwner(), provinces -> {
            if (provinces != null && !provinces.isEmpty()) {
                List<SelectOptionWrapper> provinceWrappers = new ArrayList<>();
                for (Province province : provinces) {
                    provinceWrappers.add(new SelectOptionWrapper(province.getID(), province.getName()));
                }
                EntityAdapter provinceAdapter = new EntityAdapter(requireContext(), provinceWrappers);
                selectProvince.setAdapter(provinceAdapter);

                selectProvince.setOnItemClickListener((parent, view, position, id) -> {
                    SelectOptionWrapper selectedWrapper = (SelectOptionWrapper) parent.getAdapter().getItem(position);
                    if (selectedWrapper != null) {
                        LocationViewModel.getCitiesByProvinceId(selectedWrapper.getId()).observe(getViewLifecycleOwner(), cities -> {
                            if (cities != null && !cities.isEmpty()) {
                                List<SelectOptionWrapper> cityWrappers = new ArrayList<>();
                                for (City city : cities) {
                                    cityWrappers.add(new SelectOptionWrapper(city.getID(), city.getType() + " " + city.getName()));
                                }
                                EntityAdapter cityAdapter = new EntityAdapter(requireContext(), cityWrappers);
                                selectCity.setAdapter(cityAdapter);
                            }
                        });
                    }
                });
            }
        });
    }

}
