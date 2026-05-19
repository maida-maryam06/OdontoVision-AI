/*
 * File: TextExtractor.java
 * Purpose: Utility to perform OCR using ML Kit Text Recognition.
 */

package com.nmims.madproj.utils;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class TextExtractor {

    public interface TextCallback {
        void onSuccess(String text);
        void onError(String errorMessage);
    }

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private TextExtractor() {}

    /**
     * Extract text from a file URI (image or PDF).
     * Automatically detects file type and uses appropriate extraction method.
     */
    public static void extractTextFromFile(@NonNull Uri fileUri, @NonNull Context context, @NonNull TextCallback callback) {
        String mimeType = context.getContentResolver().getType(fileUri);
        
        if (mimeType != null && mimeType.equals("application/pdf")) {
            // Extract from PDF
            PdfExtractor.extractTextFromPdf(fileUri, context, new PdfExtractor.PdfCallback() {
                @Override
                public void onSuccess(String text) {
                    callback.onSuccess(text);
                }

                @Override
                public void onError(String errorMessage) {
                    callback.onError(errorMessage);
                }
            });
        } else {
            // Extract from image using OCR
            extractTextFromImage(fileUri, context, callback);
        }
    }

    /**
     * Extract text from an image URI using ML Kit OCR.
     */
    public static void extractTextFromImage(@NonNull Uri imageUri, @NonNull Context context, @NonNull TextCallback callback) {
        EXECUTOR.execute(() -> {
            try {
                InputImage image = InputImage.fromFilePath(context, imageUri);
                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                        .process(image)
                        .addOnSuccessListener(result -> callback.onSuccess(result.getText()))
                        .addOnFailureListener(e -> callback.onError(e.getMessage() != null ? e.getMessage() : "OCR failed"));
            } catch (Exception e) {
                callback.onError(e.getMessage() != null ? e.getMessage() : "Unable to open image");
            }
        });
    }
}


