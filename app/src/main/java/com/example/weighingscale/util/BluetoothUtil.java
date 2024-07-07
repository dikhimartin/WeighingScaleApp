package com.example.weighingscale.util;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class BluetoothUtil {

    private static final String TAG = "BluetoothUtil";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice connectedDevice;

    public BluetoothUtil() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public boolean isConnected() {
        return bluetoothSocket != null && bluetoothSocket.isConnected();
    }

    @SuppressLint("MissingPermission")
    public void connectToDevice(BluetoothDevice device) {
        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            connectedDevice = device;
            Log.d(TAG, "Connected to " + device.getName());
        } catch (IOException e) {
            Log.e(TAG, "Failed to connect to device", e);
            disconnect();
        }
    }

    public void disconnect() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
                bluetoothSocket = null;
                connectedDevice = null;
                Log.d(TAG, "Disconnected from device");
            } catch (IOException e) {
                Log.e(TAG, "Failed to disconnect", e);
            }
        }
    }

    @SuppressLint("MissingPermission")
    public Set<BluetoothDevice> getPairedDevices() {
        return bluetoothAdapter.getBondedDevices();
    }

    @SuppressLint("MissingPermission")
    public void requestEnableBluetooth(Context context) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        context.startActivity(enableBtIntent);
    }

    public BluetoothDevice getConnectedDevice() {
        return connectedDevice;
    }
}
