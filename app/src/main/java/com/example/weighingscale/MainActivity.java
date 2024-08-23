package com.example.weighingscale;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.weighingscale.util.FormatterUtil;
import com.example.weighingscale.util.LogModelUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.weighingscale.databinding.ActivityMainBinding;

import java.util.Objects;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.weighingscale.util.BluetoothUtil;
import com.example.weighingscale.util.ConnectedThread;
import com.example.weighingscale.ui.shared.SharedViewModel;
import com.example.weighingscale.state.StateConnecting;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int MESSAGE_READ = 2;
    public static final int HANDLER_STATUS = 3;
    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 100;

    private SharedViewModel sharedViewModel;
    private BluetoothUtil mBluetoothUtil;
    private Handler mHandler;
    private ConnectedThread mConnectedThread;
    private BluetoothSocket mBTSocket = null;
    private MenuItem bluetoothMenuItem;

    private ActivityMainBinding binding;
    private StateConnecting stateConnecting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewModels
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        stateConnecting = new StateConnecting();

        // Binding layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_history, R.id.navigation_note)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Initialize Bluetooth
        initBluetooth();

        // Check permission WRITE_EXTERNAL_STORAGE
        checkStoragePermission();

        // Listen for destination changes
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_setting || destination.getId() == R.id.navigation_device || destination.getId() == R.id.navigation_history_detail) {
                // Enable navigation
                navView.setVisibility(View.GONE);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            } else {
                // Disable navigation
                navView.setVisibility(View.VISIBLE);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
            // Back action
            if (destination.getId() == R.id.form_note_fragment || destination.getId() == R.id.navigation_history_detail) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        });
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If permission has not been granted, ask for permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin telah diberikan, lanjutkan dengan tindakan yang membutuhkan izin ini
                Toast.makeText(this, "Izin penyimpanan diberikan", Toast.LENGTH_SHORT).show();
            } else {
                // Izin ditolak, beri tahu pengguna
                Toast.makeText(this, "Izin penyimpanan ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        bluetoothMenuItem = menu.findItem(R.id.action_device);

        // Check and update the Bluetooth menu item when the menu is created
        if (mBluetoothUtil != null && mBluetoothUtil.isBluetoothEnabled()) {
            set_indicator_bt_enable();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        if (id == R.id.action_settings) {
            navController.navigate(R.id.navigation_setting);
            return true;
        } else if (id == R.id.action_device) {
            if (!mBluetoothUtil.isBluetoothEnabled()) {
                bluetoothOn();
            }else if (stateConnecting.getStatus() == StateConnecting.BLUETOOTH_CONNECTED){
                return true;
            }else{
                // Show device list fragment to connect
                navController.navigate(R.id.navigation_device);
            }
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

    private void initBluetooth() {
        // Initialize BluetoothUtil instance
        mBluetoothUtil = BluetoothUtil.getInstance();

        // Register for Bluetooth state change broadcasts
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoothReceiver, filter);

        // Initialize handler for Bluetooth messages
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case MESSAGE_READ:
                        // TODO : Baca timbangan
                        String readMessage = new String((byte[]) msg.obj, StandardCharsets.UTF_8);
                        int weightValue = FormatterUtil.sanitizeAndConvertToInteger(readMessage);
                        sharedViewModel.setWeight(weightValue);
                        break;
                    case HANDLER_STATUS:
                         LogModelUtils.printObjectFields(msg);
                        if (msg.arg1 == 1) {
                            bluetoothMenuItem.setTitle(getString(R.string.connected) + ": " + msg.obj);
                            stateConnecting.setStatus(StateConnecting.BLUETOOTH_CONNECTED);
                            sharedViewModel.setBluetoothStatus(stateConnecting.getStatus());
                        } else {
                            bluetoothMenuItem.setTitle(R.string.connect_to_scale);
                            if (mConnectedThread != null) {
                                mConnectedThread.cancel();
                                mConnectedThread = null;
                            }
                            Toast.makeText(getApplicationContext(), getString(R.string.connection_fail), Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };

        // Check if Bluetooth is supported on this device
        if (mBluetoothUtil.getAdapter() == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.bluetooth_device_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
        super.onActivityResult(requestCode, resultCode, Data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                set_indicator_bt_enable();
            } else {
                set_indicator_bt_disable();
            }
        }
    }

    private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action != null) {
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            Toast.makeText(getApplicationContext(), getString(R.string.bluetooth_not_on), Toast.LENGTH_SHORT).show();
                            set_indicator_bt_disable();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            // Handle Bluetooth turning off
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Toast.makeText(getApplicationContext(), getString(R.string.bluetooth_turn_on), Toast.LENGTH_SHORT).show();
                            // Handle Bluetooth on
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            // Handle Bluetooth turning on
                            break;
                    }
                }
            }
        }
    };

    private void bluetoothOn() {
        if (!mBluetoothUtil.isBluetoothEnabled()) {
            mBluetoothUtil.enableBluetooth(MainActivity.this, REQUEST_ENABLE_BT);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.bluetooth_already_on), Toast.LENGTH_SHORT).show();
        }
    }

    public interface ConnectionCallback {
        void onConnected();
    }

    public void connectToDevice(String address, String name, ConnectionCallback callback) {
        bluetoothMenuItem.setTitle(R.string.connecting);
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
                    runOnUiThread(() -> Toast.makeText(getBaseContext(), getString(R.string.error_socket_create), Toast.LENGTH_SHORT).show());
                }
                try {
                    mBTSocket.connect();
                } catch (IOException e) {
                    try {
                        fail = true;
                        mBTSocket.close();
                        mHandler.obtainMessage(HANDLER_STATUS, -1, -1).sendToTarget();
                    } catch (IOException e2) {
                        runOnUiThread(() -> Toast.makeText(getBaseContext(), getString(R.string.error_socket_create), Toast.LENGTH_SHORT).show());
                    }
                }
                if (!fail) {
                    mConnectedThread = new ConnectedThread(mBTSocket, mHandler);
                    mConnectedThread.start();
                    mHandler.post(() -> {
                        mHandler.obtainMessage(HANDLER_STATUS, 1, -1, name).sendToTarget();
                        if (callback != null) {
                            callback.onConnected();
                        }
                    });
                }
            }
        }.start();
    }

    private void set_indicator_bt_enable(){
        bluetoothMenuItem.setIcon(null);
        bluetoothMenuItem.setTitle(R.string.connect_to_scale);
        stateConnecting.setStatus(StateConnecting.BLUETOOTH_ENABLED);
        sharedViewModel.setBluetoothStatus(stateConnecting.getStatus());
    }

    private void set_indicator_bt_disable(){
        bluetoothMenuItem.setIcon(R.drawable.ic_bluetooth_disabled_24dp);
        stateConnecting.setStatus(StateConnecting.BLUETOOTH_DISABLED);
        sharedViewModel.setBluetoothStatus(stateConnecting.getStatus());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the Bluetooth receiver
        unregisterReceiver(mBluetoothReceiver);

        // Clean up Bluetooth resources
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        // Nullify handler to avoid memory leaks
        mHandler = null;
    }
}
