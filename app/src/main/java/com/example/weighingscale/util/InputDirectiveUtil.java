package com.example.weighingscale.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;
import java.util.Locale;

public class InputDirectiveUtil {

    public static void applyCurrencyFormat(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {

            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    editText.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[Rp,.\\s]", "");

                    try {
                        long parsed = cleanString.isEmpty() ? 0 : Long.parseLong(cleanString); // Changed to long
                        String formatted = NumberFormat.getCurrencyInstance(new Locale("id", "ID")).format(parsed);

                        // Add "Rp." prefix and remove other occurrences of "Rp" from formatting
                        formatted = "Rp. " + formatted.replaceAll("[Rp\\s]", "");

                        current = formatted;
                        editText.setText(formatted);
                        editText.setSelection(formatted.length());
                    } catch (NumberFormatException e) {
                        // In case of error, just reset the text to avoid crashes
                        editText.setText(current);
                        editText.setSelection(current.length());
                    }

                    editText.addTextChangedListener(this);
                }
            }
        });
    }

    public static long getCurrencyValue(EditText editText) {
        String cleanString = editText.getText().toString().replaceAll("[Rp,.\\s]", "");
        try {
            return cleanString.isEmpty() ? 0 : Long.parseLong(cleanString);  // Return as long
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
