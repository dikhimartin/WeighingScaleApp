package com.example.weighingscale.ui.device;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.Manifest;

import com.example.weighingscale.R;
import com.example.weighingscale.util.BluetoothUtil;
import com.example.weighingscale.MainActivity;
import java.util.Set;

public class DeviceFragment extends Fragment {
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;

    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;

    private BluetoothUtil mBluetoothUtil;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_device, container, false);

        // Initialize BluetoothUtil instance
        mBluetoothUtil = BluetoothUtil.getInstance();

        // Initialize UI components
        mDevicesListView = root.findViewById(R.id.devices_list_view);
        mBTArrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        mDevicesListView.setAdapter(mBTArrayAdapter);
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Check for Bluetooth permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSIONS);
            } else {
                listPairedDevices();
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_BLUETOOTH_PERMISSIONS);
            } else {
                listPairedDevices();
            }
        }

        return root;
    }

    @SuppressLint("MissingPermission")
    private void listPairedDevices() {
        mBTArrayAdapter.clear();
        Set<BluetoothDevice> pairedDevices = mBluetoothUtil.getPairedDevices();
        if (pairedDevices != null && mBluetoothUtil.getAdapter().isEnabled()) {
            for (BluetoothDevice device : pairedDevices) {
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
            Toast.makeText(requireContext(), getString(R.string.show_paired_devices), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), getString(R.string.bluetooth_not_on), Toast.LENGTH_SHORT).show();
        }
    }

    private final AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!mBluetoothUtil.getAdapter().isEnabled()) {
                Toast.makeText(requireContext(), getString(R.string.bluetooth_not_on), Toast.LENGTH_SHORT).show();
                return;
            }
            String info = ((String) parent.getItemAtPosition(position));
            String address = info.substring(info.length() - 17);
            String name = info.substring(0, info.length() - 17);
            MainActivity mainActivity = (MainActivity) requireActivity();
            connectToDevice(address, name, mainActivity);
        }
    };

    public void connectToDevice(String address, String name, MainActivity activity) {
        activity.connectToDevice(address, name, new MainActivity.ConnectionCallback() {
            @Override
            public void onConnected() {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.navigation_home);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                listPairedDevices();
            } else {
                Toast.makeText(requireContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
