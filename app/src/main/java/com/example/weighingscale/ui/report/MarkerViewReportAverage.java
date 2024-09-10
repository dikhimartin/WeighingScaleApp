package com.example.weighingscale.ui.report;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.example.weighingscale.R;
import com.example.weighingscale.data.dto.BatchDTO;
import com.example.weighingscale.util.FormatterUtil;
import com.example.weighingscale.util.SafeValueUtil;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.List;

@SuppressLint("ViewConstructor")
public class MarkerViewReportAverage extends MarkerView {
    private final TextView valueTanggalPenimbangan;
    private final TextView valueDurasiPenimbangan;
    private final TextView valueJamMulai;
    private final TextView valueJamBerakhir;
    private final TextView valueLokasiPenimbanganProvinsi;
    private final TextView valueLokasiPenimbanganKota;
    private final TextView valueTujuanPengirimanProvinsi;
    private final TextView valueTujuanPengirimanKota;
    private final List<BatchDTO> batchEntries;

    public MarkerViewReportAverage(Context context, int layoutResource, List<BatchDTO> batchEntries) {
        super(context, layoutResource);
        this.batchEntries = batchEntries;

        // Initialize TextViews
        valueTanggalPenimbangan = findViewById(R.id.valueTanggalPenimbangan);
        valueDurasiPenimbangan = findViewById(R.id.valueDurasiPenimbangan);
        valueJamMulai = findViewById(R.id.valueJamMulai);
        valueJamBerakhir = findViewById(R.id.valueJamBerakhir);
        valueLokasiPenimbanganProvinsi = findViewById(R.id.valueLokasiPenimbanganProvinsi);
        valueLokasiPenimbanganKota = findViewById(R.id.valueLokasiPenimbanganKota);
        valueTujuanPengirimanProvinsi = findViewById(R.id.valueTujuanPengirimanProvinsi);
        valueTujuanPengirimanKota = findViewById(R.id.valueTujuanPengirimanKota);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        BarEntry barEntry = (BarEntry) e;
        int batchIndex = (int) barEntry.getX();

        BatchDTO batch = batchEntries.get(batchIndex);

        // Format data
        String textDatetime = SafeValueUtil.getFormattedDate(batch.datetime, "dd MMMM yyyy");
        String durationFormat = FormatterUtil.formatDuration(batch.duration);
        String textStartDate = SafeValueUtil.getFormattedDate(batch.start_date, "dd/MM/yyyy HH:mm:ss");
        String textEndDate = SafeValueUtil.getFormattedDate(batch.end_date, "dd/MM/yyyy HH:mm:ss");

        // Set locations
        String weighingLocationCity = getNonEmptyText(setLocationText(
            batch.weighing_location_city_type,
            batch.weighing_location_city_name
        ));
        String weighingLocationProvince = getNonEmptyText(setLocationText(
            "Provinsi",
            batch.weighing_location_province_name
        ));
        String deliveryDestinationCity = getNonEmptyText(setLocationText(
            batch.delivery_destination_city_type,
            batch.delivery_destination_city_name
        ));
        String deliveryDestinationProvince = getNonEmptyText(setLocationText(
            "Provinsi",
            batch.delivery_destination_province_name
        ));

        // Set content to TextViews
        valueTanggalPenimbangan.setText(getNonEmptyText(textDatetime));
        valueDurasiPenimbangan.setText(getNonEmptyText(durationFormat));
        valueJamMulai.setText(getNonEmptyText(textStartDate));
        valueJamBerakhir.setText(getNonEmptyText(textEndDate));
        valueLokasiPenimbanganProvinsi.setText(weighingLocationProvince);
        valueLokasiPenimbanganKota.setText(weighingLocationCity);
        valueTujuanPengirimanProvinsi.setText(deliveryDestinationProvince);
        valueTujuanPengirimanKota.setText(deliveryDestinationCity);

        super.refreshContent(e, highlight);
    }

    // Return "-" if text is empty or null
    private String getNonEmptyText(String text) {
        return (text == null || text.isEmpty()) ? "-" : text;
    }

    // Combine location type and name
    private String setLocationText(String type, String name) {
        if (type != null && name != null) {
            return type + " " + name;
        } else if (name != null) {
            return name;
        } else {
            return "-";
        }
    }

    public void updateData(List<BatchDTO> newBatch) {
        this.batchEntries.clear();
        this.batchEntries.addAll(newBatch);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-((float) getWidth() / 2), -getHeight()); // Center marker above the point
    }
}
