package com.example.weighingscale.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtil {

    public static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;

    public static boolean checkBluetoothPermissions(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestBluetoothPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, REQUEST_BLUETOOTH_PERMISSIONS);
    }
}
