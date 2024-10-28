package com.example.weighingscale.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.weighingscale.data.model.Batch;
import com.example.weighingscale.data.model.BatchDetail;
import com.example.weighingscale.data.repository.BatchDetailRepository;
import com.example.weighingscale.data.repository.BatchRepository;
import com.example.weighingscale.viewmodel.BatchDetailViewModel;
import com.example.weighingscale.viewmodel.BatchViewModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(csvFile));

            // Write Batch section
            batchViewModel.getAllBatch(new BatchRepository.Callback<List<Batch>>() {
                @Override
                public void onResult(List<Batch> batches) {
                    try {
                        // Write Batch header regardless of data presence
                        bufferedWriter.write("BatchID,PICName,PICPhoneNumber,DateTime,StartDate,EndDate,Duration,Unit,RicePrice,WeighingLocationID,DeliveryDestinationID,TruckDriverName,TruckDriverPhoneNumber,Status");
                        bufferedWriter.newLine();

                        if (batches != null && !batches.isEmpty()) {
                            for (Batch batch : batches) {
                                writeBatchData(bufferedWriter, batch);
                            }
                        }

                        // Write separator and BatchDetail section header
                        bufferedWriter.newLine();
                        bufferedWriter.write("BatchDetail Section");
                        bufferedWriter.newLine();

                        // Fetch and write BatchDetail data
                        writeBatchDetailSection(bufferedWriter);

                    } catch (IOException e) {
                        Log.e("CSVWriteError", "Error writing Batch section", e);
                    }
                }
            });

        } catch (IOException e) {
            Log.e("CSVWriteError", "Error creating CSV file", e);
        }
    }

    // Method to write BatchDetail data in a clean structure
    private void writeBatchDetailSection(BufferedWriter bufferedWriter) {
        batchDetailViewModel.getAllBatchDetail(new BatchDetailRepository.Callback<List<BatchDetail>>() {
            @Override
            public void onResult(List<BatchDetail> batchDetails) {
                try {
                    // Write BatchDetail header regardless of data presence
                    bufferedWriter.write("BatchID,DetailID,DateTime,Amount");
                    bufferedWriter.newLine();

                    if (batchDetails != null && !batchDetails.isEmpty()) {
                        for (BatchDetail detail : batchDetails) {
                            writeBatchDetailData(bufferedWriter, detail);
                        }
                    }

                } catch (IOException e) {
                    Log.e("CSVWriteError", "Error writing BatchDetail section", e);
                } finally {
                    // Close BufferedWriter after all data is written
                    try {
                        bufferedWriter.close();
                    } catch (IOException e) {
                        Log.e("CSVWriteError", "Error closing BufferedWriter", e);
                    }
                }
            }
        });
    }

    // Function to write Batch data
    private void writeBatchData(BufferedWriter bufferedWriter, Batch batch) throws IOException {
        String datetime = batch.datetime != null ? String.valueOf(batch.datetime.getTime()) : "";
        String startDate = batch.start_date != null ? String.valueOf(batch.start_date.getTime()) : "";
        String endDate = batch.end_date != null ? String.valueOf(batch.end_date.getTime()) : "";

        String line = SafeValueUtil.getString(batch.id, "") + "," +
                      SafeValueUtil.getString(batch.pic_name,"") + "," +
                      SafeValueUtil.getString(batch.pic_phone_number,"") + "," +
                      datetime + "," +
                      startDate + "," +
                      endDate + "," +
                      batch.duration + "," +
                      SafeValueUtil.getString(batch.unit,"") + "," +
                      batch.rice_price + "," +
                      SafeValueUtil.getString(batch.weighing_location_id,"") + "," +
                      SafeValueUtil.getString(batch.delivery_destination_id,"") + "," +
                      SafeValueUtil.getString(batch.truck_driver_name,"") + "," +
                      SafeValueUtil.getString(batch.truck_driver_phone_number,"") + "," +
                      batch.status;
        bufferedWriter.write(line);
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