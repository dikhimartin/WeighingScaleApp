package com.example.weighingscale.util;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class BluetoothUtil {

    public static final int REQUEST_ENABLE_BT = 1;
    public static final int REQUEST_DISCOVER_DEVICES = 2;

    private BluetoothAdapter bluetoothAdapter;
    private MutableLiveData<Boolean> isConnected = new MutableLiveData<>(false);

    public BluetoothUtil(Context context) {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public LiveData<Boolean> getIsConnected() {
        return isConnected;
    }

    public void enableBluetooth(Activity activity) {
        if (bluetoothAdapter == null) {
            Toast.makeText(activity, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
        } else if (!bluetoothAdapter.isEnabled()) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN)
                    == PackageManager.PERMISSION_GRANTED) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.BLUETOOTH_ADMIN},
                        REQUEST_ENABLE_BT);
            }
        }
    }

    public void connectToDevice(BluetoothDevice device) {
        // Implement the connection logic here
        // Update isConnected status based on connection status
    }

    public void disconnectFromDevice() {
        // Implement the disconnection logic here
        // Update isConnected status based on disconnection status
    }

    public void startDiscovery(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            bluetoothAdapter.startDiscovery();
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_DISCOVER_DEVICES);
        }
    }

    public void cancelDiscovery(Activity activity) {
        bluetoothAdapter.cancelDiscovery();
    }
}
