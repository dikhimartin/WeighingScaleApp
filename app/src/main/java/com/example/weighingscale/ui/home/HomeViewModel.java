package com.example.weighingscale.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.state.StateBatch;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private MutableLiveData<Batch> currentBatch = new MutableLiveData<>();
    private MutableLiveData<StateBatch> batchState = new MutableLiveData<>();

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<Batch> getCurrentBatch() {
        return currentBatch;
    }

    public LiveData<StateBatch> getBatchState() {
        return batchState;
    }

}
