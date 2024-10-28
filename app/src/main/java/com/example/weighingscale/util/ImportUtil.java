package com.example.weighingscale.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.model.BatchDetail;
import com.example.weighingscale.viewmodel.BatchViewModel;
import com.example.weighingscale.viewmodel.BatchDetailViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Utility class for importing data from a CSV file into Batch and BatchDetail records.
 */
public class ImportUtil {

    private final BatchViewModel batchViewModel; // ViewModel for Batch operations
    private final BatchDetailViewModel batchDetailViewModel; // ViewModel for BatchDetail operations
    private final Context context; // Application context

    /**
     * Constructor to initialize ImportUtil with necessary ViewModels and context.
     *
     * @param batchViewModel The BatchViewModel for handling batch operations.
     * @param batchDetailViewModel The BatchDetailViewModel for handling batch detail operations.
     * @param context The application context.
     */
    public ImportUtil(BatchViewModel batchViewModel, BatchDetailViewModel batchDetailViewModel, Context context) {
        this.batchViewModel = batchViewModel;
        this.batchDetailViewModel = batchDetailViewModel;
        this.context = context;
    }

    /**
     * Imports CSV data for Batch and BatchDetail records.
     *
     * @param uri The URI of the CSV file to be imported.
     * @throws IOException if an error occurs while reading the CSV file.
     */
    public void importCSV(Uri uri) throws IOException {
        // Step 1: Initialize input stream and reader to read CSV data
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        boolean isBatchDetailSection = false;  // Tracks section of CSV (Batch or BatchDetail)
        boolean isFirstLine = true;  // Used to skip header line
        List<Batch> batchList = new ArrayList<>();  // Temporary storage for Batch entries
        List<BatchDetail> batchDetailList = new ArrayList<>();  // Temporary storage for BatchDetail entries

        // Step 2: Read all data from CSV into temporary lists
        while ((line = reader.readLine()) != null) {
            // Skip any empty lines
            if (line.isEmpty()) continue;

            // Skip header row
            if (isFirstLine) {
                isFirstLine = false;
                continue;
            }

            // Detect transition to BatchDetail section
            if (line.startsWith("BatchDetail Section")) {
                isBatchDetailSection = true;
                continue;
            }

            // Split line into columns for processing
            String[] columns = line.split(",");

            if (isBatchDetailSection) {
                // Process BatchDetail rows
                processBatchDetail(columns, batchDetailList);
            } else {
                // Process Batch rows
                processBatch(columns, batchList);
            }
        }

        // Close the reader after reading all data
        reader.close();

        // Step 3: Insert all Batch records into the database if they have valid UUIDs
        insertBatchData(batchList);

        // Step 4: Insert all BatchDetail records into the database if they have valid UUIDs
        insertBatchDetailData(batchDetailList);

        // Notify user upon successful data import
        Toast.makeText(context, "Data successfully imported from CSV", Toast.LENGTH_SHORT).show();
    }

    /**
     * Processes a row from the BatchDetail section of the CSV and adds it to the list.
     *
     * @param columns The split CSV line.
     * @param batchDetailList The list to which the BatchDetail will be added.
     */
    private void processBatchDetail(String[] columns, List<BatchDetail> batchDetailList) {
        if (ValidationUtil.isLongNumeric(columns[2]) && ValidationUtil.isLongNumeric(columns[3])) {
            BatchDetail detail = new BatchDetail();
            detail.batch_id = columns[0];
            detail.id = columns[1];
            detail.datetime = new Date(Long.parseLong(columns[2]));
            detail.amount = Integer.parseInt(columns[3]);
            batchDetailList.add(detail);  // Add valid BatchDetail to list
        } else {
            Log.e("CSV Import Error", "Invalid values in BatchDetail columns.");
        }
    }

    /**
     * Processes a row from the Batch section of the CSV and adds it to the list.
     *
     * @param columns The split CSV line.
     * @param batchList The list to which the Batch will be added.
     */
    private void processBatch(String[] columns, List<Batch> batchList) {
        if (ValidationUtil.isLongNumeric(columns[3]) && ValidationUtil.isLongNumeric(columns[4]) &&
            ValidationUtil.isLongNumeric(columns[8]) &&
            (columns[5].isEmpty() || ValidationUtil.isLongNumeric(columns[5])) &&
            (columns[6].isEmpty() || ValidationUtil.isLongNumeric(columns[6]))) {

            Batch batch = new Batch();
            batch.id = columns[0];
            batch.pic_name = columns[1];
            batch.pic_phone_number = columns[2];
            batch.datetime = new Date(Long.parseLong(columns[3]));
            batch.start_date = new Date(Long.parseLong(columns[4]));
            batch.end_date = FormatterUtil.parseDate(columns[5]);
            batch.duration = FormatterUtil.parseLong(columns[6]);
            batch.unit = columns[7];
            batch.rice_price = Long.parseLong(columns[8]);
            batch.weighing_location_id = columns[9];
            batch.delivery_destination_id = columns[10];
            batch.truck_driver_name = columns[11];
            batch.truck_driver_phone_number = columns[12];
            Log.d("LOG_BATCH", "Batch -> Status : " + columns[13]);
            batch.status = Integer.parseInt(columns[13]);
            batchList.add(batch);  // Add valid Batch to list
        } else {
            Log.e("CSV Import Error", "Invalid values in Batch columns.");
        }
    }

    /**
     * Inserts Batch records into the database using the BatchViewModel.
     *
     * @param batchList The list of Batch records to be inserted.
     */
    private void insertBatchData(List<Batch> batchList) {
        for (Batch batch : batchList) {
            Log.d("LOG_BATCH", "Batch -> Status (After) : " + batch.status);
            if (ValidationUtil.isValidUUID(batch.id)) {
                batchViewModel.importData(batch);
            }
        }
    }

    /**
     * Inserts BatchDetail records into the database using the BatchDetailViewModel.
     *
     * @param batchDetailList The list of BatchDetail records to be inserted.
     */
    private void insertBatchDetailData(List<BatchDetail> batchDetailList) {
        for (BatchDetail detail : batchDetailList) {
            if (ValidationUtil.isValidUUID(detail.batch_id)) {
                batchDetailViewModel.importData(detail);
            }
        }
    }
}
