package com.example.weighingscale.ui.batch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BatchViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public BatchViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is data fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}