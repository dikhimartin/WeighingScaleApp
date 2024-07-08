package com.example.weighingscale.ui.device;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weighingscale.R;
import com.example.weighingscale.util.BluetoothUtil;
import com.example.weighingscale.MainActivity;
import java.util.Set;

public class DeviceFragment extends Fragment {

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

        // Populate paired devices list
        listPairedDevices();

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
        activity.connectToDevice(address, name);
    }

}
