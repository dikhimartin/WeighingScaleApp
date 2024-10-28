package com.example.weighingscale.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;

public class ShareUtil {

    public void shareFile(Context context, File file) {
        Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Set the MIME type based on file extension
        String mimeType = getMimeType(file.getName());
        shareIntent.setType(mimeType);

        context.startActivity(Intent.createChooser(shareIntent, "Share File using"));
    }

    private String getMimeType(String fileName) {
        if (fileName.endsWith(".csv")) {
            return "text/csv";
        } else if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        }
        return "*/*";
    }
}
