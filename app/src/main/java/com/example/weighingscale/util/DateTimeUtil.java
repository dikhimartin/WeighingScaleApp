package com.example.weighingscale.util;

import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtil {

    public static void showDateTimePicker(FragmentManager fragmentManager, TextView tvDateTime) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal")
                .build();

        datePicker.show(fragmentManager, "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Pilih Waktu")
                    .build();

            timePicker.show(fragmentManager, "TIME_PICKER");

            timePicker.addOnPositiveButtonClickListener(timeSelection -> {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                Calendar dateTime = Calendar.getInstance();
                dateTime.set(year, month, day, hour, minute);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String formattedDateTime = sdf.format(dateTime.getTime());
                tvDateTime.setText(formattedDateTime);
            });
        });
    }

    public static Date parseDateTime(String dateTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            return sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }
}
