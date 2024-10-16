package com.example.weighingscale.ui.shared;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Double> weight = new MutableLiveData<>();  // Changed from Integer to Double
    private final MutableLiveData<Integer> bluetoothStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> settingUpdated = new MutableLiveData<>();

    public void setWeight(double weight) {  // Updated to double
        this.weight.setValue(weight);
    }

    public LiveData<Double> getWeight() {  // Updated return type
        return weight;
    }

    public void setBluetoothStatus(int status) {
        bluetoothStatus.setValue(status);
    }

    public LiveData<Integer> getBluetoothStatus() {
        return bluetoothStatus;
    }
}
