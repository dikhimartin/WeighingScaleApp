package com.example.weighingscale.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
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
import java.util.List;

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
            addBatchDetailsTable(document, batchDetails);
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

    private static File createPdfFile(Context context, String batchId) throws FileNotFoundException {
        File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "ExportedPDFs");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, "Nota_Penimbangan_" + batchId + ".pdf");
    }

    private static void addTitle(Document document, String title) throws IOException {
        document.add(new Paragraph(title)
                .setFont(PdfFontFactory.createFont("Helvetica-Bold"))
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(16));
        document.add(new Paragraph("").setMarginBottom(20f)); // Menambahkan jarak setelah judul
    }

    private static void addHeaderTable(Document document, BatchDTO batch) {
        // Tabel dengan 3 kolom: Label, titik dua, dan nilai
        Table headerTable = new Table(new float[]{3, 0.5f, 5});
        headerTable.setMarginBottom(20f);

        // Baris 1: Tanggal & Waktu
        headerTable.addCell(createCell("Tanggal & Waktu", TextAlignment.LEFT));
        headerTable.addCell(createCell(":", TextAlignment.LEFT));
        headerTable.addCell(createCell(DateTimeUtil.formatDateTime(batch.getDatetime(), "dd MMMM yyyy HH:mm"), TextAlignment.LEFT));

        // Baris 2: Lokasi Penimbangan
        headerTable.addCell(createCell("Lokasi Penimbangan", TextAlignment.LEFT));
        headerTable.addCell(createCell(":", TextAlignment.LEFT));
        headerTable.addCell(createCell("Provinsi Jawa Tengah\nKabupaten Demak", TextAlignment.LEFT));

        // Baris 3: Tujuan Pengiriman
        headerTable.addCell(createCell("Tujuan Pengiriman", TextAlignment.LEFT));
        headerTable.addCell(createCell(":", TextAlignment.LEFT));
        headerTable.addCell(createCell("Provinsi Jawa Tengah\nKabupaten Demak", TextAlignment.LEFT));

        document.add(headerTable);
    }


    private static void addBatchDetailsTable(Document document, List<BatchDetail> batchDetails) {
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
            table.addCell(createCell(detail.getAmount() + " Kg"));
        }

        document.add(table);
    }

    private static void addSummary(Document document, BatchDTO batch, int totalBags) {
        // Tabel dengan 3 kolom: Label, titik dua, dan nilai
        Table summaryTable = new Table(new float[]{3, 0.5f, 5});
        summaryTable.setMarginBottom(20f);

        // Baris 1: Harga Padi (per-Kg)
        summaryTable.addCell(createCell("Harga Padi (per-Kg)", TextAlignment.LEFT));
        summaryTable.addCell(createCell(":", TextAlignment.LEFT));
        summaryTable.addCell(createCell(SafeValueUtil.formatCurrency("Rp.", batch.getRice_price()), TextAlignment.LEFT));

        // Baris 2: Total Karung (sak)
        summaryTable.addCell(createCell("Total Karung (sak)", TextAlignment.LEFT));
        summaryTable.addCell(createCell(":", TextAlignment.LEFT));
        summaryTable.addCell(createCell(totalBags + " Karung (sak)", TextAlignment.LEFT));

        // Baris 3: Total Berat
        summaryTable.addCell(createCell("Total Berat", TextAlignment.LEFT));
        summaryTable.addCell(createCell(":", TextAlignment.LEFT));
        summaryTable.addCell(createCell(batch.getTotalAmount() + " Kg", TextAlignment.LEFT));

        // Baris 4: Total Harga
        summaryTable.addCell(createCell("Total Harga", TextAlignment.LEFT));
        summaryTable.addCell(createCell(":", TextAlignment.LEFT));
        summaryTable.addCell(createCell(SafeValueUtil.formatCurrency("Rp.", (batch.getTotalAmount() * batch.getRice_price())), TextAlignment.LEFT));

        document.add(summaryTable);
    }

    private static void addFooter(Document document, BatchDTO batch) {
        Table footerTable = new Table(new float[]{1, 1});
        footerTable.setWidth(UnitValue.createPercentValue(100));

        // Penanggung Jawab (Align Left)
        Cell leftCell = new Cell();
        leftCell.add(new Paragraph("Penanggung Jawab").setBold());
        leftCell.add(new Paragraph(batch.getPicName()));
        leftCell.add(new Paragraph(batch.getPicPhoneNumber()));
        leftCell.setBorder(Border.NO_BORDER);
        leftCell.setTextAlignment(TextAlignment.LEFT);

        // Driver (Align Right)
        Cell rightCell = new Cell();
        rightCell.add(new Paragraph("Driver").setBold());
        rightCell.add(new Paragraph(batch.getTruck_driver_name()));
        rightCell.add(new Paragraph("08123456789"));
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

    public static void sharePDF(Context context, File pdfFile) {
        Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", pdfFile);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(Intent.createChooser(shareIntent, "Share PDF using"));
    }
}
