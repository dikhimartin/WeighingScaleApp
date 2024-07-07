package com.example.weighingscale.ui.device;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.weighingscale.util.BluetoothUtil;

import java.util.ArrayList;
import java.util.Set;

public class DeviceListActivity extends ListActivity {

    private BluetoothUtil bluetoothUtil;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothUtil = new BluetoothUtil();

        Set<BluetoothDevice> pairedDevices = bluetoothUtil.getPairedDevices();
        ArrayList<String> deviceList = new ArrayList<>();

        for (BluetoothDevice device : pairedDevices) {
            deviceList.add(device.getName() + "\n" + device.getAddress());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceList);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        String address = item.substring(item.length() - 17);
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        bluetoothUtil.connectToDevice(device);
        finish();
    }
}
