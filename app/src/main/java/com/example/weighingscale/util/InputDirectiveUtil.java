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
                        long parsed = cleanString.isEmpty() ? 0 : Long.parseLong(cleanString);
                        String formatted = NumberFormat.getNumberInstance(new Locale("id", "ID")).format(parsed);
                        formatted = "Rp. " + formatted;
                        current = formatted;
                        editText.setText(formatted);
                        editText.setSelection(formatted.length());
                    } catch (NumberFormatException e) {
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
            return cleanString.isEmpty() ? 0 : Long.parseLong(cleanString);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
