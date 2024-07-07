package com.example.weighingscale;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.weighingscale.util.BluetoothUtil;
import com.example.weighingscale.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BluetoothUtil bluetoothUtil;
    private MenuItem bluetoothMenuItem;

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

        bluetoothUtil = new BluetoothUtil(this);
        bluetoothUtil.getIsConnected().observe(this, isConnected -> {
            if (bluetoothMenuItem != null) {
                bluetoothMenuItem.setIcon(isConnected ? R.drawable.ic_bluetooth_connected_24dp : R.drawable.ic_bluetooth_disabled_24dp);
            }
        });

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
        bluetoothMenuItem = menu.findItem(R.id.action_bluetooth);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.setting) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_setting);
            return true;
        } else if (id == R.id.action_bluetooth) {
            // Launch DeviceFragment to show available devices
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, new DeviceFragment())
                    .addToBackStack(null)
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                bluetoothUtil.startDiscovery();
            }
        }
    }

}
