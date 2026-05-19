/*
 * File: PdfExtractor.java
 * Purpose: Utility to extract text from PDF files.
 */

package com.nmims.madproj.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.InputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PdfExtractor {

    public interface PdfCallback {
        void onSuccess(String text);
        void onError(String errorMessage);
    }

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final String TAG = "PdfExtractor";

    private PdfExtractor() {}

    /**
     * Extract text from a PDF URI.
     */
    public static void extractTextFromPdf(@NonNull Uri pdfUri, @NonNull Context context, @NonNull PdfCallback callback) {
        EXECUTOR.execute(() -> {
            InputStream inputStream = null;
            PDDocument document = null;
            try {
                inputStream = context.getContentResolver().openInputStream(pdfUri);
                if (inputStream == null) {
                    callback.onError("Unable to open PDF file");
                    return;
                }

                document = PDDocument.load(inputStream);
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);

                if (text == null || text.trim().isEmpty()) {
                    callback.onError("No text found in PDF");
                } else {
                    callback.onSuccess(text.trim());
                }
            } catch (IOException e) {
                Log.e(TAG, "Error extracting PDF text", e);
                callback.onError("Failed to extract text: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error extracting PDF text", e);
                callback.onError("Unexpected error: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
            } finally {
                try {
                    if (document != null) {
                        document.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error closing resources", e);
                }
            }
        });
    }
}

