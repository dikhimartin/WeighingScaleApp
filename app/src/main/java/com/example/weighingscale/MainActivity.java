package com.example.weighingscale;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.weighingscale.databinding.ActivityMainBinding;

import java.util.Objects;

// TODO : BLUETOOTH MODULE
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weighingscale.util.BluetoothUtil;
import com.example.weighingscale.util.ConnectedThread;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
// END BLUETOOTH MODULE


public class MainActivity extends AppCompatActivity {

    // TODO : BLUETOOTH MODULE
    private static final int REQUEST_ENABLE_BT = 1;
    public static final int MESSAGE_READ = 2;
    private static final int CONNECTING_STATUS = 3;

    private TextView mBluetoothStatus;
    private TextView mReadBuffer;
    private Button mScanBtn;
    private Button mOffBtn;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    private ListView mDevicesListView;
    private CheckBox mLED1;

    private BluetoothUtil mBluetoothUtil;
    private ArrayAdapter<String> mBTArrayAdapter;
    private Handler mHandler;
    private ConnectedThread mConnectedThread;
    private BluetoothSocket mBTSocket = null;
    // END BLUETOOTH MODULE

    private ActivityMainBinding binding;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // TODO : BLUETOOTH MODULE
        initView();
        initBluetooth();
        setListeners();

        // Listen for destination changes
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_setting) {
                navView.setVisibility(View.GONE);
                // Enable the up button
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            } else {
                navView.setVisibility(View.VISIBLE);
                // Disable the up button
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.setting) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_setting);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    private void initView() {
        // Initialize UI Bluetooth components
        mBluetoothStatus = findViewById(R.id.bluetooth_status);
        mReadBuffer = findViewById(R.id.read_buffer);
        mScanBtn = findViewById(R.id.scan);
        mOffBtn = findViewById(R.id.off);
        mDiscoverBtn = findViewById(R.id.discover);
        mListPairedDevicesBtn = findViewById(R.id.paired_btn);
        mLED1 = findViewById(R.id.checkbox_led_1);
        mDevicesListView = findViewById(R.id.devices_list_view);
    }

    private void initBluetooth() {
        // Initialize BluetoothUtil instance
        mBluetoothUtil = BluetoothUtil.getInstance();
        mBTArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mDevicesListView.setAdapter(mBTArrayAdapter);
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Initialize handler for Bluetooth messages
        mHandler = new Handler(Looper.getMainLooper()) {
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case MESSAGE_READ:
                        String readMessage = new String((byte[]) msg.obj, StandardCharsets.UTF_8);
                        mReadBuffer.setText(readMessage);
                        break;
                    case CONNECTING_STATUS:
                        if (msg.arg1 == 1)
                            mBluetoothStatus.setText(getString(R.string.BTConnected) + msg.obj);
                        else
                            mBluetoothStatus.setText(getString(R.string.BTconnFail));
                        break;
                }
            }
        };

        // Check if Bluetooth is supported on this device
        if (mBluetoothUtil.getAdapter() == null) {
            mBluetoothStatus.setText(getString(R.string.sBTstaNF));
            Toast.makeText(getApplicationContext(), getString(R.string.sBTdevNF), Toast.LENGTH_SHORT).show();
        }
    }

    private void setListeners() {
        // Set listeners for UI buttons
        mLED1.setOnClickListener(v -> {
            if (mConnectedThread != null)
                mConnectedThread.write("1");
        });
        mScanBtn.setOnClickListener(v -> bluetoothOn());
        mOffBtn.setOnClickListener(v -> bluetoothOff());
        mListPairedDevicesBtn.setOnClickListener(v -> listPairedDevices());
        mDiscoverBtn.setOnClickListener(v -> discover());
    }

    private void bluetoothOn() {
        if (!mBluetoothUtil.isBluetoothEnabled()) {
            mBluetoothUtil.enableBluetooth(MainActivity.this, REQUEST_ENABLE_BT);
            mBluetoothStatus.setText(getString(R.string.BTEnable));
            Toast.makeText(getApplicationContext(), getString(R.string.sBTturON), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.BTisON), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
        super.onActivityResult(requestCode, resultCode, Data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                mBluetoothStatus.setText(getString(R.string.sEnabled));
            } else {
                mBluetoothStatus.setText(getString(R.string.sDisabled));
            }
        }
    }

    private void bluetoothOff() {
        mBluetoothUtil.disableBluetooth();
        mBluetoothStatus.setText(getString(R.string.sBTdisabl));
        Toast.makeText(getApplicationContext(), "Bluetooth turned Off", Toast.LENGTH_SHORT).show();
    }

    private void discover() {
        if (mBluetoothUtil.getAdapter().isEnabled()) {
            mBTArrayAdapter.clear();
            mBluetoothUtil.startDiscovery();
            Toast.makeText(getApplicationContext(), getString(R.string.DisStart), Toast.LENGTH_SHORT).show();
            registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show();
        }
    }

    private final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                assert device != null;
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    @SuppressLint("MissingPermission")
    private void listPairedDevices() {
        mBTArrayAdapter.clear();
        Set<BluetoothDevice> pairedDevices = mBluetoothUtil.getPairedDevices();
        if (pairedDevices != null && mBluetoothUtil.getAdapter().isEnabled()) {
            for (BluetoothDevice device : pairedDevices) {
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
            Toast.makeText(getApplicationContext(), getString(R.string.show_paired_devices), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show();
        }
    }


    private final AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!mBluetoothUtil.getAdapter().isEnabled()) {
                Toast.makeText(getBaseContext(), getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show();
                return;
            }
            mBluetoothStatus.setText(getString(R.string.cConnet));
            String info = ((TextView) view).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0, info.length() - 17);
            new Thread() {
                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    boolean fail = false;
                    BluetoothDevice device = mBluetoothUtil.getAdapter().getRemoteDevice(address);
                    try {
                        mBTSocket = mBluetoothUtil.createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        runOnUiThread(() -> Toast.makeText(getBaseContext(), getString(R.string.ErrSockCrea), Toast.LENGTH_SHORT).show());
                    }
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                        } catch (IOException e2) {
                            runOnUiThread(() -> Toast.makeText(getBaseContext(), getString(R.string.ErrSockCrea), Toast.LENGTH_SHORT).show());
                        }
                    }
                    if (!fail) {
                        mConnectedThread = new ConnectedThread(mBTSocket, mHandler);
                        mConnectedThread.start();
                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name).sendToTarget();
                    }
                }
            }.start();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(blReceiver);
    }



}
