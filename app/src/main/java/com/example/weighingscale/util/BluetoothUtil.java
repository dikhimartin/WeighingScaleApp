package com.example.weighingscale.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class BluetoothUtil {
    private static final String TAG = BluetoothUtil.class.getSimpleName();
    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter mBTAdapter;

    private static BluetoothUtil instance;

    private BluetoothUtil() {
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothUtil getInstance() {
        if (instance == null) {
            instance = new BluetoothUtil();
        }
        return instance;
    }

    public BluetoothAdapter getAdapter() {
        return mBTAdapter;
    }

    public boolean isBluetoothEnabled() {
        return mBTAdapter != null && mBTAdapter.isEnabled();
    }

    @SuppressLint("MissingPermission")
    public void enableBluetooth(Activity activity, int requestCode) {
        if (mBTAdapter != null && !mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, requestCode);
        }
    }

    @SuppressLint("MissingPermission")
    public void disableBluetooth() {
        if (mBTAdapter != null && mBTAdapter.isEnabled()) {
            mBTAdapter.disable();
        }
    }

    @SuppressLint("MissingPermission")
    public Set<BluetoothDevice> getPairedDevices() {
        if (mBTAdapter != null) {
            return mBTAdapter.getBondedDevices();
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    public void startDiscovery() {
        if (mBTAdapter != null && !mBTAdapter.isDiscovering()) {
            mBTAdapter.startDiscovery();
        }
    }

    @SuppressLint("MissingPermission")
    public void cancelDiscovery() {
        if (mBTAdapter != null && mBTAdapter.isDiscovering()) {
            mBTAdapter.cancelDiscovery();
        }
    }

    @SuppressLint("MissingPermission")
    public BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
        }
        return device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }
}
