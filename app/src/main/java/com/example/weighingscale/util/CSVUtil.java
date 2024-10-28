package com.example.weighingscale.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.model.BatchDetail;
import com.example.weighingscale.viewmodel.BatchDetailViewModel;
import com.example.weighingscale.viewmodel.BatchViewModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CSVUtil {
    private final BatchViewModel batchViewModel;
    private final BatchDetailViewModel batchDetailViewModel;

    public CSVUtil(BatchViewModel batchViewModel, BatchDetailViewModel batchDetailViewModel) {
        this.batchViewModel = batchViewModel;
        this.batchDetailViewModel = batchDetailViewModel;
    }

    public File generateCSV(Context context) {
        File csvFile = null;
        try {
            csvFile = createCsvFile(context);
            writeCSVFile(csvFile);
            Toast.makeText(context, "CSV berhasil dibuat", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Gagal membuat CSV", Toast.LENGTH_SHORT).show();
        }
        return csvFile;
    }

    private File createCsvFile(Context context) {
        File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "ExportedCSVs");
        if (!dir.exists() && !dir.mkdirs()) {
            Log.e("CSVUtil", "Gagal membuat direktori: " + dir.getAbsolutePath());
            return null;
        }
        String currentDateTime = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault()).format(new Date());
        String fileName = "Data_Timbangan_" + currentDateTime + ".csv";
        return new File(dir, fileName);
    }

    private void writeCSVFile(File csvFile) {
        BufferedWriter bufferedWriter;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(csvFile));

            // Write Batch section
            BufferedWriter finalBufferedWriter = bufferedWriter;
            batchViewModel.getAllBatch().observeForever(batches -> {
                try {
                    if (batches != null && !batches.isEmpty()) {
                        // Write Batch header
                        finalBufferedWriter.write("BatchID,PICName,PICPhoneNumber,DateTime,StartDate,EndDate,Duration,Unit,RicePrice,WeighingLocationID,DeliveryDestinationID,TruckDriverName,TruckDriverPhoneNumber,Status");
                        finalBufferedWriter.newLine();

                        // Write Batch data
                        for (Batch batch : batches) {
                            writeBatchData(finalBufferedWriter, batch);
                        }

                        // Add a separator line for BatchDetail section
                        finalBufferedWriter.newLine();
                        finalBufferedWriter.write("BatchDetail Section");
                        finalBufferedWriter.newLine();
                    }
                } catch (IOException e) {
                    Log.e("CSVWriteError", "Error writing Batch data", e);
                }
            });

            // Write BatchDetail section
            BufferedWriter finalBufferedWriter1 = bufferedWriter;
            batchDetailViewModel.getAllBatchDetail().observeForever(batchDetails -> {
                try {
                    if (batchDetails != null && !batchDetails.isEmpty()) {
                        // Write BatchDetail header
                        finalBufferedWriter1.write("BatchID,DetailID,DateTime,Amount");
                        finalBufferedWriter1.newLine();

                        // Write BatchDetail data
                        for (BatchDetail detail : batchDetails) {
                            writeBatchDetailData(finalBufferedWriter1, detail);
                        }
                    }
                } catch (IOException e) {
                    Log.e("CSVWriteError", "Error writing BatchDetail data", e);
                } finally {
                    try {
                        finalBufferedWriter1.close();
                    } catch (IOException e) {
                        Log.e("CSVWriteError", "Error closing BufferedWriter", e);
                    }
                }
            });
        } catch (IOException e) {
            Log.e("CSVWriteError", "Error creating CSV file", e);
        }
    }

    // Function to write Batch data
    private void writeBatchData(BufferedWriter bufferedWriter, Batch batch) throws IOException {
        String id = SafeValueUtil.getString(batch.id, "");
        String picName = SafeValueUtil.getString(batch.pic_name, "");
        String picPhoneNumber = SafeValueUtil.getString(batch.pic_phone_number, "");
        String datetime = (batch.datetime != null) ? String.valueOf(batch.datetime.getTime()) : "";
        String startDate = (batch.start_date != null) ? String.valueOf(batch.start_date.getTime()) : "";
        String endDate = (batch.end_date != null) ? String.valueOf(batch.end_date.getTime()) : "";
        String duration = String.valueOf(batch.duration);
        String unit = SafeValueUtil.getString(batch.unit, "");
        String ricePrice = String.valueOf(batch.rice_price);
        String weighingLocationId = SafeValueUtil.getString(batch.weighing_location_id, "");
        String deliveryDestinationId = SafeValueUtil.getString(batch.delivery_destination_id, "");
        String truckDriverName = SafeValueUtil.getString(batch.truck_driver_name, "");
        String truckDriverPhoneNumber = SafeValueUtil.getString(batch.truck_driver_phone_number, "");
        String status = String.valueOf(batch.status);
        String lineBuilder = id + "," +
                picName + "," +
                picPhoneNumber + "," +
                datetime + "," +
                startDate + "," +
                endDate + "," +
                duration + "," +
                unit + "," +
                ricePrice + "," +
                weighingLocationId + "," +
                deliveryDestinationId + "," +
                truckDriverName + "," +
                truckDriverPhoneNumber + "," +
                status;
        bufferedWriter.write(lineBuilder);
        bufferedWriter.newLine();
    }

    // Function to write BatchDetail data
    private void writeBatchDetailData(BufferedWriter bufferedWriter, BatchDetail detail) throws IOException {
        String detailLine = SafeValueUtil.getString(detail.batch_id, "") + "," +
                            SafeValueUtil.getString(detail.id, "") + "," +
                            detail.datetime.getTime() + "," +
                            detail.amount;
        bufferedWriter.write(detailLine);
        bufferedWriter.newLine();
    }
}