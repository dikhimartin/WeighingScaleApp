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

    /**
     * Displays a date and time picker dialog.
     * Once the user selects a date and time, it formats the selected datetime
     * into a string with the format "yyyy-MM-dd HH:mm" and sets it to the provided TextView.
     *
     * Example usage:
     * DateTimeUtil.showDateTimePicker(getSupportFragmentManager(), textViewDateTime);
     *
     * The method first shows a date picker, and after the date is selected,
     * it shows a time picker. The selected date and time are then combined
     * and formatted to "yyyy-MM-dd HH:mm" format and displayed in the provided TextView.
     *
     * @param fragmentManager The FragmentManager used to show the dialog.
     * @param tvDateTime The TextView where the selected date and time will be displayed.
     */
    public static void showDateTimePicker(FragmentManager fragmentManager, TextView tvDateTime) {
        // Create and show the date picker dialog
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal")
                .build();

        datePicker.show(fragmentManager, "DATE_PICKER");

        // Listener for when a date is selected
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Set the selected date to the calendar with UTC time zone
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create and show the time picker dialog with the current time
            Calendar currentTime = Calendar.getInstance();
            int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
            int currentMinute = currentTime.get(Calendar.MINUTE);

            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(currentHour)
                    .setMinute(currentMinute)
                    .setTitleText("Pilih Waktu")
                    .build();

            timePicker.show(fragmentManager, "TIME_PICKER");

            // Listener for when a time is selected
            timePicker.addOnPositiveButtonClickListener(timeSelection -> {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                // Combine the selected date and time into a single Calendar object
                Calendar dateTime = Calendar.getInstance();
                dateTime.set(year, month, day, hour, minute);

                // Format the combined date and time to "yyyy-MM-dd HH:mm" format
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String formattedDateTime = sdf.format(dateTime.getTime());

                // Set the formatted datetime string to the TextView
                tvDateTime.setText(formattedDateTime);
            });
        });
    }

    /**
     * Shows a Material Date Range Picker dialog.
     * Once the user selects a date range, it invokes the provided callback with the selected start and end dates.
     *
     * @param fragmentManager The FragmentManager used to show the dialog.
     * @param onDateRangeSelected Callback invoked when the date range is selected, passing the start and end dates.
     */
    public static void showDateRangePicker(FragmentManager fragmentManager, OnDateRangeSelectedListener onDateRangeSelected) {
        MaterialDatePicker.Builder<?> builder = MaterialDatePicker.Builder.dateRangePicker();
        MaterialDatePicker<?> picker = builder.build();

        picker.addOnPositiveButtonClickListener(selection -> {
            // `selection` will be a Pair<Long, Long> where the first value is start date and the second is end date
            if (selection instanceof androidx.core.util.Pair) {
                @SuppressWarnings("unchecked")
                androidx.core.util.Pair<Long, Long> dateRange = (androidx.core.util.Pair<Long, Long>) selection;
                Calendar calendar = Calendar.getInstance(); // Local timezone

                // Set start date
                calendar.setTimeInMillis(dateRange.first);
                Date startDate = calendar.getTime();

                // Set end date
                calendar.setTimeInMillis(dateRange.second);
                Date endDate = calendar.getTime();

                // Invoke the callback with the selected start and end dates
                onDateRangeSelected.onDateRangeSelected(startDate, endDate);
            }
        });

        picker.show(fragmentManager, picker.toString());
    }

    /**
     * Interface to handle the date range selection callback.
     */
    public interface OnDateRangeSelectedListener {
        void onDateRangeSelected(Date startDate, Date endDate);
    }


    /**
     * Parses a string in the format "yyyy-MM-dd HH:mm" into a Date object.
     *
     * Example usage:
     * Date date = DateTimeUtil.parseDateTime("2024-08-18 18:37");
     *
     * Output:
     * A Date object representing the datetime "2024-08-18 18:37".
     *
     * If the input string is invalid or cannot be parsed, the current date and time will be returned.
     *
     * @param dateTime The string representing the date and time to be parsed in the format "yyyy-MM-dd HH:mm".
     * @return A Date object corresponding to the input string, or the current date and time if parsing fails.
     */
    public static Date parseDateTime(String dateTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            return sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * Formats a Date object into a string based on the provided pattern.
     *
     * Example usage:
     * String formattedDate = DateTimeUtil.formatDateTime(currentData.getDatetime(), "dd/MM/yyyy HH:mm");
     *
     * Output:
     * A string representing the date in the specified format.
     *
     * The output will use the default locale.
     *
     * @param date The Date object to be formatted.
     * @param pattern The pattern describing the date and time format.
     * @return A formatted string representing the date in the specified pattern.
     */
    public static String formatDateTime(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sdf.format(date);
    }
}
