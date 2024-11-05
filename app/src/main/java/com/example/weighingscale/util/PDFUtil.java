package com.example.weighingscale.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.weighingscale.data.dto.BatchDTO;
import com.example.weighingscale.data.model.BatchDetail;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PDFUtil {
    public static File generatePDF(Context context, BatchDTO batch, List<BatchDetail> batchDetails) {
        File pdfFile = null;
        try {
            pdfFile = createPdfFile(context, batch.getID());

            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            addTitle(document, "NOTA PENIMBANGAN");
            addHeaderTable(document, batch);
            addBatchDetailsTable(document, batch, batchDetails);
            addSummary(document, batch, batchDetails.size());
            addFooter(document, batch);

            document.close();

            Toast.makeText(context, "PDF berhasil dibuat", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Gagal membuat PDF", Toast.LENGTH_SHORT).show();
        }

        return pdfFile;
    }

    private static File createPdfFile(Context context, String batchId) {
        File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "ExportedPDFs");
        if (!dir.exists() && !dir.mkdirs()) {
            Log.e("PDFUtil", "Gagal membuat direktori: " + dir.getAbsolutePath());
            return null;
        }
        String currentDate = new SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(new Date());
        String fileName = "Nota_Penimbangan_" + currentDate + "_" + batchId + ".pdf";
        return new File(dir, fileName);
    }

    private static void addTitle(Document document, String title) throws IOException {
        document.add(new Paragraph(title)
                .setFont(PdfFontFactory.createFont("Helvetica-Bold"))
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(16));
        document.add(new Paragraph("").setMarginBottom(25f));
    }

    private static void addHeaderTable(Document document, BatchDTO batch) {
        // Tabel dengan 3 kolom: Label, titik dua, dan nilai
        Table headerTable = new Table(new float[]{3, 0.5f, 5});
        headerTable.setMarginBottom(20f);

        // Baris 1: Tanggal & Waktu
        headerTable.addCell(createCell("Tanggal & Waktu", TextAlignment.LEFT));
        headerTable.addCell(createCell(":", TextAlignment.LEFT));
        headerTable.addCell(createCell(DateTimeUtil.formatDateTime(batch.datetime, "dd MMMM yyyy HH:mm"), TextAlignment.LEFT));

        // Baris 2: Lokasi Penimbangan
        headerTable.addCell(createCell("Lokasi Penimbangan", TextAlignment.LEFT));
        headerTable.addCell(createCell(":", TextAlignment.LEFT));
        headerTable.addCell(createCell(formatLocation(batch.weighing_location_province_name, batch.weighing_location_city_type, batch.weighing_location_city_name), TextAlignment.LEFT));

        // Baris 3: Tujuan Pengiriman
        headerTable.addCell(createCell("Tujuan Pengiriman", TextAlignment.LEFT));
        headerTable.addCell(createCell(":", TextAlignment.LEFT));
        headerTable.addCell(createCell(formatLocation(batch.delivery_destination_province_name, batch.delivery_destination_city_type, batch.delivery_destination_city_name), TextAlignment.LEFT));

        document.add(headerTable);
    }

    private static void addBatchDetailsTable(Document document, BatchDTO batch, List<BatchDetail> batchDetails) {
        Table table = new Table(new float[]{1, 4, 3});
        table.setMarginBottom(20f);
        table.setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(createCell("No."));
        table.addHeaderCell(createCell("Tanggal"));
        table.addHeaderCell(createCell("Banyaknya"));

        for (int i = 0; i < batchDetails.size(); i++) {
            BatchDetail detail = batchDetails.get(i);
            table.addCell(createCell(String.valueOf(i + 1)));
            table.addCell(createCell(DateTimeUtil.formatDateTime(detail.getDatetime(), "dd/MM/yyyy HH:mm")));
            table.addCell(createCell(detail.getAmount() + " " + batch.unit));
        }

        document.add(table);
    }

    private static void addSummary(Document document, BatchDTO batch, int totalBags) {
        // Tabel dengan 3 kolom: Label, titik dua, dan nilai
        Table summaryTable = new Table(new float[]{3, 0.5f, 5});
        summaryTable.setMarginBottom(20f);

        // Baris 1: Harga Padi (per-unit)
        String unit = batch.getUnit() != null ? batch.getUnit() : "Kg";
        summaryTable.addCell(createCell("Harga Padi (per-" + unit + ")", TextAlignment.LEFT));
        summaryTable.addCell(createCell(":", TextAlignment.LEFT));
        summaryTable.addCell(createCell(SafeValueUtil.formatCurrency("Rp.", batch.getRice_price()), TextAlignment.LEFT));

        // Baris 2: Total Item
        summaryTable.addCell(createCell("Total Item", TextAlignment.LEFT));
        summaryTable.addCell(createCell(":", TextAlignment.LEFT));
        summaryTable.addCell(createCell(totalBags + " Item", TextAlignment.LEFT));

        // Baris 3: Total Berat
        summaryTable.addCell(createCell("Total Berat", TextAlignment.LEFT));
        summaryTable.addCell(createCell(":", TextAlignment.LEFT));
        summaryTable.addCell(createCell(WeighingUtils.convertWeight(batch.getTotalAmount(), unit, true), TextAlignment.LEFT));

        // Baris 4: Total Harga
        summaryTable.addCell(createCell("Total Harga", TextAlignment.LEFT));
        summaryTable.addCell(createCell(":", TextAlignment.LEFT));
        summaryTable.addCell(createCell(SafeValueUtil.formatCurrency("Rp.", Math.round(batch.getTotalAmount() * batch.getRice_price())), TextAlignment.LEFT));

        document.add(summaryTable);
    }


    private static void addFooter(Document document, BatchDTO batch) {
        Table footerTable = new Table(new float[]{1, 1});
        footerTable.setWidth(UnitValue.createPercentValue(100));

        // Penanggung Jawab (Align Left)
        Cell leftCell = new Cell();
        leftCell.add(new Paragraph("Penanggung Jawab").setBold());
        leftCell.add(new Paragraph(SafeValueUtil.getString(batch.pic_name, "N/A")));
        leftCell.add(new Paragraph(SafeValueUtil.getString(batch.pic_phone_number, "N/A")));
        leftCell.setBorder(Border.NO_BORDER);
        leftCell.setTextAlignment(TextAlignment.LEFT);

        // Driver (Align Right)
        Cell rightCell = new Cell();
        rightCell.add(new Paragraph("Driver").setBold());
        rightCell.add(new Paragraph(SafeValueUtil.getString(batch.truck_driver_name, "N/A")));
        rightCell.add(new Paragraph(SafeValueUtil.getString(batch.truck_driver_phone_number, "N/A")));
        rightCell.setBorder(Border.NO_BORDER);
        rightCell.setTextAlignment(TextAlignment.RIGHT);

        footerTable.addCell(leftCell);
        footerTable.addCell(rightCell);

        document.add(footerTable);
    }

    private static Cell createCell(String content) {
        return new Cell().add(new Paragraph(content));
    }

    private static Cell createCell(String content, TextAlignment alignment) {
        return new Cell().add(new Paragraph(content)).setTextAlignment(alignment).setBorder(Border.NO_BORDER);
    }

    private static String formatLocation(String provinceName, String cityType, String cityName) {
        if (provinceName == null || cityType == null || cityName == null ||
            provinceName.isEmpty() || cityType.isEmpty() || cityName.isEmpty()) {
            return "-";
        }
        return "Provinsi " + provinceName + "\n" + cityType + " " + cityName;
    }
}
