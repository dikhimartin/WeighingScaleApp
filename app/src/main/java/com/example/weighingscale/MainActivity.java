package com.example.weighingscale;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
import android.widget.TextView;
import android.widget.Toast;

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

    private SharedViewModel sharedViewModel;
    private BluetoothUtil mBluetoothUtil;
    private Handler mHandler;
    private ConnectedThread mConnectedThread;
    private BluetoothSocket mBTSocket = null;
    private MenuItem bluetoothMenuItem;

    private ActivityMainBinding binding;
    private StateConnecting stateConnecting;

    private TextView mReadBuffer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        stateConnecting = new StateConnecting();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_data, R.id.navigation_note)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Init Bluetooth
        initBluetooth();

        // Listen for destination changes
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_setting || destination.getId() == R.id.navigation_device) {
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

    // Initialize UI
    private void initView() {
        // bluetoothMenuItem = findViewById(R.id.action_bluetooth);
        // mBluetoothStatus = findViewById(R.id.bluetooth_status);
        // mReadBuffer = findViewById(R.id.read_buffer);
    }

    private void initBluetooth() {
        // Initialize BluetoothUtil instance
        mBluetoothUtil = BluetoothUtil.getInstance();

        // Initialize handler for Bluetooth messages
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case MESSAGE_READ:
                        // TODO : Baca timbangan
                        String readMessage = new String((byte[]) msg.obj, StandardCharsets.UTF_8);
                        sharedViewModel.setWeight(readMessage);
                        // mReadBuffer.setText(readMessage);
                        // Log.d("Streams Scale", readMessage);
                        Log.d("LOG_SAY(readMessage)", readMessage);
                        Log.d("LOG_SAY(STAT)", String.valueOf(stateConnecting.getStatus()));
                        break;
                    case HANDLER_STATUS:
                        if (msg.arg1 == 1) {
                            bluetoothMenuItem.setTitle(getString(R.string.connected) + ": " + msg.obj);
                            stateConnecting.setStatus(StateConnecting.BLUETOOTH_CONNECTED);
                        } else if (msg.arg1 == 4) {
                            Toast.makeText(getApplicationContext(), getString(R.string.device_already_connected), Toast.LENGTH_SHORT).show();
                        } else {
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

    private void bluetoothOn() {
        if (!mBluetoothUtil.isBluetoothEnabled()) {
            mBluetoothUtil.enableBluetooth(MainActivity.this, REQUEST_ENABLE_BT);
            Toast.makeText(getApplicationContext(), getString(R.string.bluetooth_turn_on), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.bluetooth_already_on), Toast.LENGTH_SHORT).show();
        }
    }

    public void connectToDevice(String address, String name) {
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
                    mHandler.obtainMessage(HANDLER_STATUS, 1, -1, name).sendToTarget();
                }
            }
        }.start();
    }

    private void set_indicator_bt_enable(){
        bluetoothMenuItem.setIcon(null);
        bluetoothMenuItem.setTitle(R.string.connect_to_scale);
        stateConnecting.setStatus(StateConnecting.BLUETOOTH_ENABLED);
    }

    private void set_indicator_bt_disable(){
        bluetoothMenuItem.setIcon(R.drawable.ic_bluetooth_disabled_24dp);
        stateConnecting.setStatus(StateConnecting.BLUETOOTH_DISABLED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up Bluetooth resources
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        // Nullify handler to avoid memory leaks
        mHandler = null;
    }
}
