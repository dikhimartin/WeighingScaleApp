package com.example.weighingscale.ui.shared;

import android.content.Context;
import android.widget.AutoCompleteTextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.example.weighingscale.data.model.City;
import com.example.weighingscale.data.model.Province;

import java.util.ArrayList;
import java.util.List;

public class LocationUtil {

    public static void setupOptionLocation(Context context, LifecycleOwner lifecycleOwner,
                                           AutoCompleteTextView selectProvince, AutoCompleteTextView selectCity,
                                           LiveData<List<Province>> provincesLiveData,
                                           LocationViewModel locationViewModel) {

        provincesLiveData.observe(lifecycleOwner, provinces -> {
            if (provinces != null && !provinces.isEmpty()) {
                List<SelectOptionWrapper> provinceWrappers = new ArrayList<>();
                for (Province province : provinces) {
                    provinceWrappers.add(new SelectOptionWrapper(province.getID(), province.getName()));
                }
                EntityAdapter provinceAdapter = new EntityAdapter(context, provinceWrappers);
                selectProvince.setAdapter(provinceAdapter);

                selectProvince.setOnItemClickListener((parent, view, position, id) -> {
                    SelectOptionWrapper selectedWrapper = (SelectOptionWrapper) parent.getAdapter().getItem(position);
                    if (selectedWrapper != null) {
                        locationViewModel.setSelectedProvinceId(selectedWrapper.getId());
                    }
                });
            }
        });

        locationViewModel.getCities().observe(lifecycleOwner, cities -> {
            if (cities != null && !cities.isEmpty()) {
                List<SelectOptionWrapper> cityWrappers = new ArrayList<>();
                for (City city : cities) {
                    cityWrappers.add(new SelectOptionWrapper(city.getID(), city.getType() + " " + city.getName()));
                }
                EntityAdapter cityAdapter = new EntityAdapter(context, cityWrappers);
                selectCity.setAdapter(cityAdapter);
            }
        });
    }
}
