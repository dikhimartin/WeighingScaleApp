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
import com.example.weighingscale.ui.setting.SettingViewModel;
import com.example.weighingscale.ui.shared.EntityAdapter;
import com.example.weighingscale.ui.shared.LocationViewModel;
import com.example.weighingscale.ui.shared.SelectOptionWrapper;
import com.example.weighingscale.ui.shared.SharedAdapter;
import com.example.weighingscale.util.FormatterUtil;
import com.example.weighingscale.util.InputDirectiveUtil;
import com.example.weighingscale.util.SafeValueUtil;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private SettingViewModel settingViewModel;

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
        MaterialCardView cardSummary = view.findViewById(R.id.card_summary);
        cardSummary.setOnClickListener(v -> showUpdateBatchDialog(currentBatch));
    }

    private void initializeViewModel() {
        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        settingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);
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
        // Inflate the custom dialog view
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_input_batch, null);

        // Initialize views
        LinearLayout layoutSetting = dialogView.findViewById(R.id.layout_setting);
        TextInputEditText etPICName = dialogView.findViewById(R.id.et_pic_name);
        TextInputEditText etPICPhoneNumber = dialogView.findViewById(R.id.et_pic_phone_number);
        TextInputEditText etTruckDriver = dialogView.findViewById(R.id.et_truck_driver);
        TextInputEditText etTruckDriverPhoneNumber = dialogView.findViewById(R.id.et_truck_driver_phone_number);
        AutoCompleteTextView actvUnit = dialogView.findViewById(R.id.actv_unit);
        TextInputEditText etRicePrice = dialogView.findViewById(R.id.et_rice_price);
        AutoCompleteTextView selectLocProvince = dialogView.findViewById(R.id.select_weighing_location_province);
        AutoCompleteTextView selectLocCity = dialogView.findViewById(R.id.select_weighing_location_city);
        AutoCompleteTextView selectDestProvince = dialogView.findViewById(R.id.select_destination_province);
        AutoCompleteTextView selectDestCity = dialogView.findViewById(R.id.select_destination_city);

        // Show setting layout
        layoutSetting.setVisibility(View.VISIBLE);

        // Apply currency format
        InputDirectiveUtil.applyCurrencyFormat(etRicePrice);

        // Setup unit AutoCompleteTextView
        settingViewModel.getUnitOptions().observe(getViewLifecycleOwner(), units -> {
            SharedAdapter adapter = new SharedAdapter(requireContext(), units);
            actvUnit.setAdapter(adapter);
        });

        // Autofill data from the batch
        etPICName.setText(batch.pic_name);
        etPICPhoneNumber.setText(batch.pic_phone_number);
        etTruckDriver.setText(batch.truck_driver_name);
        etTruckDriverPhoneNumber.setText(batch.truck_driver_phone_number);
        actvUnit.setText(settingViewModel.getUnitDisplayText(batch.unit), false);
        etRicePrice.setText(String.valueOf(batch.rice_price));

        selectLocProvince.setText(batch.weighing_location_province_name);
        selectLocCity.setText(batch.weighing_location_city_type + " " + batch.weighing_location_city_name);
        selectDestProvince.setText(batch.delivery_destination_province_name);
        selectDestCity.setText(batch.delivery_destination_city_type + " " + batch.delivery_destination_city_name);

        // Setup AutoCompleteTextView adapters
        setupOptionLocation(selectLocProvince, selectLocCity);
        setupOptionLocation(selectDestProvince, selectDestCity);

        // Store selected city IDs
        final String[] deliveryDestinationID = new String[1];
        final String[] weighingLocationID = new String[1];

        selectDestCity.setOnItemClickListener((parent, view, position, id) -> {
            SelectOptionWrapper selectedCity = (SelectOptionWrapper) parent.getAdapter().getItem(position);
            if (selectedCity != null) {
                deliveryDestinationID[0] = selectedCity.getId();
            }
        });

        selectLocCity.setOnItemClickListener((parent, view, position, id) -> {
            SelectOptionWrapper selectedCity = (SelectOptionWrapper) parent.getAdapter().getItem(position);
            if (selectedCity != null) {
                weighingLocationID[0] = selectedCity.getId();
            }
        });

        // Create and show the dialog
        new AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Mengubah data batch muatan")
            .setPositiveButton("Ubah", (dialog, id) -> {
                // Update batch data
                batch.pic_name = Objects.requireNonNull(etPICName.getText()).toString();
                batch.pic_phone_number = Objects.requireNonNull(etPICPhoneNumber.getText()).toString();
                batch.truck_driver_name = Objects.requireNonNull(etTruckDriver.getText()).toString();
                batch.truck_driver_phone_number = Objects.requireNonNull(etTruckDriverPhoneNumber.getText()).toString();
                batch.rice_price = InputDirectiveUtil.getCurrencyValue(etRicePrice);
                batch.unit = settingViewModel.getUnitValue(Objects.requireNonNull(actvUnit.getText()).toString().trim());

                if (deliveryDestinationID[0] != null) {
                    batch.delivery_destination_id = deliveryDestinationID[0];
                }
                if (weighingLocationID[0] != null) {
                    batch.weighing_location_id = weighingLocationID[0];
                }

                // Save the updated batch through ViewModel
                historyViewModel.updateBatch(batch);
                Toast.makeText(requireContext(), "Data batch muatan telah diubah", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Batal", (dialog, id) -> dialog.dismiss())
            .create()
            .show();
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
