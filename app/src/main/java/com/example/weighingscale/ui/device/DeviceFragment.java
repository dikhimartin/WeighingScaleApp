package com.example.weighingscale.ui.device;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weighingscale.R;
import com.example.weighingscale.util.BluetoothUtil;

import java.util.ArrayList;

public class DeviceFragment extends Fragment {

    private BluetoothUtil bluetoothUtil;
    private ArrayAdapter<String> deviceListAdapter;
    private ArrayList<String> deviceList = new ArrayList<>();
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        listView = view.findViewById(R.id.device_list);

        bluetoothUtil = new BluetoothUtil(getContext());
        deviceListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, deviceList);
        listView.setAdapter(deviceListAdapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String deviceInfo = deviceList.get(position);
            String deviceAddress = deviceInfo.substring(deviceInfo.length() - 17);
            BluetoothDevice device = bluetoothUtil.getBluetoothAdapter().getRemoteDevice(deviceAddress);
            bluetoothUtil.connectToDevice(device);
        });

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getContext().registerReceiver(receiver, filter);

        bluetoothUtil.startDiscovery();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(receiver);
        bluetoothUtil.cancelDiscovery(this);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                @SuppressLint("MissingPermission") String deviceName = device.getName();
                String deviceAddress = device.getAddress();
                String deviceInfo = deviceName + "\n" + deviceAddress;
                deviceList.add(deviceInfo);
                deviceListAdapter.notifyDataSetChanged();
            }
        }
    };
}
