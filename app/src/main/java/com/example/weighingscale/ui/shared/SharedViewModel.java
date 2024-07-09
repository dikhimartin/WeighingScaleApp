package com.example.weighingscale.ui.shared;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> weight = new MutableLiveData<>();

    public void setWeight(String weight) {
        this.weight.setValue(weight);
    }

    public LiveData<String> getWeight() {
        return weight;
    }
}
