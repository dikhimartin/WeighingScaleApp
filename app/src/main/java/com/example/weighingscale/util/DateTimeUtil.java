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

    public static void showDateRangePicker(FragmentManager fragmentManager, OnDateRangeSelectedListener onDateRangeSelected) {
        MaterialDatePicker.Builder<?> builder = MaterialDatePicker.Builder.dateRangePicker();
        MaterialDatePicker<?> picker = builder.build();

        picker.addOnPositiveButtonClickListener(selection -> {
            if (selection instanceof androidx.core.util.Pair) {
                @SuppressWarnings("unchecked")
                androidx.core.util.Pair<Long, Long> dateRange = (androidx.core.util.Pair<Long, Long>) selection;
                Calendar calendar = Calendar.getInstance(); // Local timezone

                // Set start date (00:00:00)
                calendar.setTimeInMillis(dateRange.first);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date startDate = calendar.getTime();

                // Set end date (23:59:59)
                calendar.setTimeInMillis(dateRange.second);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
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

  /**
     * Format date range into a readable string based on the start and end dates.
     * The output will vary depending on whether the dates are the same or different
     * in terms of day, month, and year.
     *
     * @param startDate The start date of the range
     * @param endDate   The end date of the range
     * @return A formatted date range string
     */
    public static String formatDateRange(Date startDate, Date endDate) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        SimpleDateFormat fullFormat = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());

        // If dates are the same, return a single date
        if (startDate.equals(endDate)) {
            return fullFormat.format(startDate);
        }

        String startDay = dayFormat.format(startDate);
        String startMonth = monthFormat.format(startDate);
        String startYear = yearFormat.format(startDate);

        String endDay = dayFormat.format(endDate);
        String endMonth = monthFormat.format(endDate);
        String endYear = yearFormat.format(endDate);

        // Same day, same month, same year
        if (startDay.equals(endDay) && startMonth.equals(endMonth) && startYear.equals(endYear)) {
            return fullFormat.format(startDate);
        }

        // Different day, same month, same year
        if (!startDay.equals(endDay) && startMonth.equals(endMonth) && startYear.equals(endYear)) {
            return startDay + " - " + endDay + " " + startMonth + " " + startYear;
        }

        // Different month, same year
        if (!startMonth.equals(endMonth) && startYear.equals(endYear)) {
            return startDay + " " + startMonth + " - " + endDay + " " + endMonth + " " + startYear;
        }

        // Different year
        return startDay + " " + startMonth + " " + startYear + " - " + endDay + " " + endMonth + " " + endYear;
    }

    /**
     * Formats the given duration in milliseconds into a human-readable string with hours, minutes, and seconds.
     * This is the default version that includes hours, minutes, and seconds by default.
     *
     * @param durationMillis The duration in milliseconds to be formatted.
     * @return A human-readable string representing the duration in "X Jam Y Menit Z Detik".
     */
    public static String formatDuration(long durationMillis) {
        return formatDuration(durationMillis, true, true, true); // Default: show hours, minutes, and seconds
    }

    /**
     * Formats the given duration in milliseconds into a human-readable string.
     * Allows customization to show only hours, minutes, or seconds.
     *
     * @param durationMillis The duration in milliseconds to be formatted.
     * @param showHours Flag to include hours in the output (true to display hours).
     * @param showMinutes Flag to include minutes in the output (true to display minutes).
     * @param showSeconds Flag to include seconds in the output (true to display seconds).
     * @return A human-readable string representing the duration.
     */
    public static String formatDuration(long durationMillis, boolean showHours, boolean showMinutes, boolean showSeconds) {
        if (durationMillis <= 0) {
            return "-"; // Return "-" if the duration is non-positive
        }

        // Calculate total seconds from milliseconds
        long totalSeconds = durationMillis / 1000;
        long minutes = totalSeconds / 60; // Total minutes
        long seconds = totalSeconds % 60; // Remaining seconds
        long hours = minutes / 60; // Total hours
        minutes = minutes % 60; // Remaining minutes

        // StringBuilder to build the output string
        StringBuilder formattedDuration = new StringBuilder();

        // Append hours if enabled
        if (showHours && hours > 0) {
            formattedDuration.append(hours).append(" Jam ");
        }

        // Append minutes if enabled
        if (showMinutes && (minutes > 0 || hours > 0)) { // Show 0 minutes to keep format consistent if hours exist
            formattedDuration.append(minutes).append(" Menit ");
        }

        // Append seconds if enabled
        if (showSeconds && (seconds > 0 || (!showHours && !showMinutes))) { // Show seconds if no other units exist
            formattedDuration.append(seconds).append(" Detik");
        }

        // Return the formatted string
        return formattedDuration.toString().trim(); // Trim any trailing spaces
    }



}
