package com.example.weighingscale;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int MESSAGE_READ = 2;
    public static final int CONNECTING_STATUS = 3;

    private TextView mReadBuffer;

    private SharedViewModel sharedViewModel;
    private BluetoothUtil mBluetoothUtil;
    private Handler mHandler;
    private ConnectedThread mConnectedThread;
    private BluetoothSocket mBTSocket = null;

    private MenuItem bluetoothMenuItem;
    private ActivityMainBinding binding;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Init Bluetooth
        initViewBluetooth();
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
        bluetoothMenuItem = menu.findItem(R.id.action_bluetooth);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        if (id == R.id.action_settings) {
            navController.navigate(R.id.navigation_setting);
            return true;
        } else if (id == R.id.action_bluetooth) {
            if (!mBluetoothUtil.isBluetoothEnabled()) {
                bluetoothOn();
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

    // Initialize UI Bluetooth components
    private void initViewBluetooth() {
        // mBluetoothStatus = findViewById(R.id.bluetooth_status);
        // mReadBuffer = findViewById(R.id.read_buffer);
    }

    private void initBluetooth() {
        // Initialize BluetoothUtil instance
        mBluetoothUtil = BluetoothUtil.getInstance();

        // Initialize handler for Bluetooth messages
        mHandler = new Handler(Looper.getMainLooper()) {
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(@NonNull Message msg) {
                Log.d("LOG_SAYA(msg)", String.valueOf(msg.arg1));
                Log.d("LOG_SAYA(what)", String.valueOf(msg.what));
                switch (msg.what) {
                    case MESSAGE_READ:
                        // TODO : Baca timbangan
                         String readMessage = new String((byte[]) msg.obj, StandardCharsets.UTF_8);
                        sharedViewModel.setWeight(readMessage);
                        // mReadBuffer.setText(readMessage);
                        // Log.d("Streams Scale", readMessage);
                        break;
                    case CONNECTING_STATUS:
                        if (msg.arg1 == 1)
                            //  bluetoothMenuItem.setTitle((CharSequence) msg.obj);
                            bluetoothMenuItem.setTitle(getString(R.string.connected));
                        else if (msg.arg1 == 4)
                            Toast.makeText(getApplicationContext(), getString(R.string.device_already_connected), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), getString(R.string.connection_fail), Toast.LENGTH_SHORT).show();
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
                // mBluetoothStatus.setText(getString(R.string.sEnabled));
                bluetoothMenuItem.setIcon(null);
                bluetoothMenuItem.setTitle(R.string.connect_to_scale);
            } else {
                // mBluetoothStatus.setText(getString(R.string.sDisabled));
                bluetoothMenuItem.setIcon(R.drawable.ic_bluetooth_disabled_24dp);
            }
        }
    }

    private void bluetoothOn() {
        if (!mBluetoothUtil.isBluetoothEnabled()) {
            mBluetoothUtil.enableBluetooth(MainActivity.this, REQUEST_ENABLE_BT);
            //  mBluetoothStatus.setText(getString(R.string.BTEnable));
            Toast.makeText(getApplicationContext(), getString(R.string.bluetooth_turn_on), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.bluetooth_already_on), Toast.LENGTH_SHORT).show();
        }
    }

    // START : BLUETOOTH Select Device
    //    private final AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
    //        @Override
    //        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    //            if (!mBluetoothUtil.getAdapter().isEnabled()) {
    //                Toast.makeText(getBaseContext(), getString(R.string.BTnotOn), Toast.LENGTH_SHORT).show();
    //                return;
    //            }
    //            mBluetoothStatus.setText(getString(R.string.cConnet));
    //            String info = ((TextView) view).getText().toString();
    //            final String address = info.substring(info.length() - 17);
    //            final String name = info.substring(0, info.length() - 17);
    //            connectToDevice(address, name);
    //        }
    //    };
    // END : BLUETOOTH MODULE

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
