package com.example.weighingscale.ui.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.weighingscale.R;

public class ReportFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        // Initialize CardViews
        CardView cardAverageWeighingSpeed = view.findViewById(R.id.card_report_average);

        // Set click listeners
        cardAverageWeighingSpeed.setOnClickListener(v ->
            Navigation.findNavController(v).navigate(R.id.action_reportFragment_to_reportAverageWeighingSpeed)
        );

        return view;
    }
}
