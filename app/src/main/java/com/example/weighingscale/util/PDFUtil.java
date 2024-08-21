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
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class PDFUtil {

    public static File generatePDF(Context context, BatchDTO batch, List<BatchDetail> batchDetails) {
        File pdfFile = null;
        try {
            // Buat direktori
            File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "ExportedPDFs");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Tentukan lokasi file PDF
            pdfFile = new File(dir, "Nota_Penimbangan_" + batch.getID() + ".pdf");

            // Membuat PdfWriter instance yang terhubung ke file
            PdfWriter writer = new PdfWriter(pdfFile);

            // Membuat PdfDocument menggunakan PdfWriter
            PdfDocument pdfDocument = new PdfDocument(writer);

            // Membuat instance Document dengan PdfDocument
            Document document = new Document(pdfDocument);

            // Tambahkan konten PDF
            document.add(new Paragraph("NOTA PENIMBANGAN").setFont(PdfFontFactory.createFont("Helvetica-Bold")).setTextAlignment(TextAlignment.CENTER).setFontSize(16));
            document.add(new Paragraph("Penanggung Jawab: " + batch.getPicName()));
            document.add(new Paragraph("Tanggal & Waktu: " + batch.getDatetime()));
            document.add(new Paragraph("Lokasi Penimbangan: " + batch.getTitle()));
            document.add(new Paragraph("Driver: " + batch.getTruck_driver_name()));

            // Tabel untuk BatchDetails
            Table table = new Table(3);
            table.addHeaderCell("No.");
            table.addHeaderCell("Tanggal");
            table.addHeaderCell("Banyaknya");

            for (int i = 0; i < batchDetails.size(); i++) {
                BatchDetail detail = batchDetails.get(i);
                table.addCell(String.valueOf(i + 1));
                table.addCell(detail.getDatetime().toString());
                table.addCell(detail.getAmount() + " Kg");
            }

            document.add(table);

            // Total Section
            document.add(new Paragraph("Harga Padi (per-Kg): Rp. " + batch.getRice_price()));
            document.add(new Paragraph("Total Karung (sak): " + batchDetails.size() + " Karung (sak)"));
            document.add(new Paragraph("Total Berat: " + batch.getTotalAmount() + " Kg"));
            document.add(new Paragraph("Total Harga: Rp. 0"));
            //  document.add(new Paragraph("Total Harga: Rp. " + batch.getTotalPrice()));

            document.close();

            Toast.makeText(context, "PDF berhasil dibuat", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "Gagal membuat PDF", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
        }

        return pdfFile;
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