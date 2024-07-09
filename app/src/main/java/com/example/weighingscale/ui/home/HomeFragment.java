package com.example.weighingscale.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
        TextView textUnit = root.findViewById(R.id.text_unit);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getWeight().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String weight) {
                textAmount.setText(weight);
                textUnit.setText("Kg");
            }
        });

        // Dummy data for log
        List<String> dummyLogs = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            dummyLogs.add("Tanggal " + i + " - 10 Kg");
        }

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view_log);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new LogAdapter(dummyLogs));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Adapter for RecyclerView
    private static class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {
        private final List<String> logList;

        LogAdapter(List<String> logList) {
            this.logList = logList;
        }

        @NonNull
        @Override
        public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
            return new LogViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
            holder.bind(logList.get(position));
        }

        @Override
        public int getItemCount() {
            return logList.size();
        }

        static class LogViewHolder extends RecyclerView.ViewHolder {
            private final TextView logText;

            LogViewHolder(@NonNull View itemView) {
                super(itemView);
                logText = itemView.findViewById(R.id.log_text);
            }

            void bind(String log) {
                logText.setText(log);
            }
        }
    }
}
