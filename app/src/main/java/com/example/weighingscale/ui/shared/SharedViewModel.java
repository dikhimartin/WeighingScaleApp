package com.example.weighingscale.ui.shared;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Integer> weight = new MutableLiveData<>();
    private final MutableLiveData<Integer> bluetoothStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> settingUpdated = new MutableLiveData<>();

    public void setWeight(int weight) {
        this.weight.setValue(weight);
    }

    public LiveData<Integer> getWeight() {
        return weight;
    }

    public void setBluetoothStatus(int status) {
        bluetoothStatus.setValue(status);
    }

    public LiveData<Integer> getBluetoothStatus() {
        return bluetoothStatus;
    }
}
