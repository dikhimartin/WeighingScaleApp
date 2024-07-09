package com.example.weighingscale.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weighingscale.R;
import com.example.weighingscale.databinding.FragmentHomeBinding;
import com.example.weighingscale.ui.shared.SharedViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView textAmount = root.findViewById(R.id.text_amount);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getWeight().observe(getViewLifecycleOwner(), weight -> textAmount.setText(weight));

        // Setup RecyclerView with dummy data
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_log);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new LogAdapter(getDummyLogData()));

        return root;
    }

    private List<LogEntry> getDummyLogData() {
        List<LogEntry> logEntries = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            logEntries.add(new LogEntry("Tanggal " + (i + 1), "10 Kg"));
        }
        return logEntries;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // LogEntry model class
    public static class LogEntry {
        private final String date;
        private final String weight;

        public LogEntry(String date, String weight) {
            this.date = date;
            this.weight = weight;
        }

        public String getDate() {
            return date;
        }

        public String getWeight() {
            return weight;
        }
    }

    // RecyclerView Adapter
    public static class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {
        private final List<LogEntry> logEntries;

        public LogAdapter(List<LogEntry> logEntries) {
            this.logEntries = logEntries;
        }

        @NonNull
        @Override
        public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
            return new LogViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
            LogEntry logEntry = logEntries.get(position);
            holder.dateTextView.setText(logEntry.getDate());
            holder.weightTextView.setText(logEntry.getWeight());
        }

        @Override
        public int getItemCount() {
            return logEntries.size();
        }

        // ViewHolder for RecyclerView items
        public static class LogViewHolder extends RecyclerView.ViewHolder {
            TextView dateTextView;
            TextView weightTextView;

            public LogViewHolder(@NonNull View itemView) {
                super(itemView);
                dateTextView = itemView.findViewById(R.id.text_view_date);
                weightTextView = itemView.findViewById(R.id.text_view_weight);
            }
        }
    }
}
