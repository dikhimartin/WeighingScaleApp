package com.example.weighingscale.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.weighingscale.data.dto.BatchDTO;
import com.example.weighingscale.data.dto.ReportDTO;
import com.example.weighingscale.data.local.database.AppDatabase;
import com.example.weighingscale.data.local.database.dao.BatchDao;
import com.example.weighingscale.util.LogModelUtils;
import com.example.weighingscale.util.SafeValueUtil;
import com.example.weighingscale.util.WeighingUtils;

import java.util.Calendar;
import java.util.Date;

public class ReportRepository {
    private final BatchDao batchDao;
    public ReportRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        this.batchDao = database.batchDao();
    }
}
