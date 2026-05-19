package com.nmims.madproj.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.nmims.madproj.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class UploadNoteActivity extends AppCompatActivity {

    private LinearLayout pdfListContainer;
    private TextView tvEmptyPdfList;
    private ActivityResultLauncher<Intent> pickFileLauncher;

    private static final String PREF_NAME = "OdontoPathNotes";
    private static final String KEY_PDF_LIST = "faculty_uploaded_pdfs";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_note);
        com.nmims.madproj.utils.UiUtil.applyAmbientBackground(this);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.topBar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        MaterialButton btnUploadLecture = findViewById(R.id.btnUploadLecture);
        pdfListContainer = findViewById(R.id.pdfListContainer);
        tvEmptyPdfList = findViewById(R.id.tvEmptyPdfList);

        pickFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedFile = result.getData().getData();

                        if (selectedFile != null) {
                            saveFacultyUploadedPdf(selectedFile);
                            loadFacultyUploadedPdfs();
                        }
                    }
                }
        );

        btnUploadLecture.setOnClickListener(v -> openFilePicker());

        loadFacultyUploadedPdfs();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        pickFileLauncher.launch(Intent.createChooser(intent, "Upload Oral Pathology Lecture PDF"));
    }

    private void saveFacultyUploadedPdf(Uri fileUri) {
        if (fileUri == null) {
            return;
        }

        try {
            final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            getContentResolver().takePersistableUriPermission(fileUri, takeFlags);
        } catch (Exception ignored) {
        }

        String fileName = getFileName(fileUri);
        String fileUriString = fileUri.toString();

        try {
            JSONArray pdfArray = getPdfArray();

            for (int i = 0; i < pdfArray.length(); i++) {
                JSONObject existingPdf = pdfArray.getJSONObject(i);
                String existingUri = existingPdf.optString("uri", "");

                if (existingUri.equals(fileUriString)) {
                    Toast.makeText(this, "This PDF is already uploaded.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            JSONObject pdfObject = new JSONObject();
            pdfObject.put("name", fileName);
            pdfObject.put("uri", fileUriString);

            pdfArray.put(pdfObject);
            savePdfArray(pdfArray);

            Toast.makeText(this, "Lecture PDF uploaded successfully.", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Failed to save PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadFacultyUploadedPdfs() {
        pdfListContainer.removeAllViews();

        JSONArray pdfArray = getPdfArray();

        if (pdfArray.length() == 0) {
            tvEmptyPdfList.setVisibility(View.VISIBLE);
            return;
        }

        tvEmptyPdfList.setVisibility(View.GONE);

        for (int i = 0; i < pdfArray.length(); i++) {
            try {
                JSONObject pdfObject = pdfArray.getJSONObject(i);

                String name = pdfObject.optString("name", "Lecture PDF");
                String uriString = pdfObject.optString("uri", "");

                final String pdfUriString = uriString;
                final int pdfIndex = i;

                TextView title = new TextView(this);
                title.setText((i + 1) + ". " + name);
                title.setTextColor(0xFFFFFFFF);
                title.setTextSize(16);
                title.setPadding(8, 14, 8, 6);
                pdfListContainer.addView(title);

                MaterialButton openButton = new MaterialButton(this);
                openButton.setText("Open PDF");
                openButton.setAllCaps(false);
                openButton.setOnClickListener(v -> openPdf(Uri.parse(pdfUriString)));
                pdfListContainer.addView(openButton);

                MaterialButton deleteButton = new MaterialButton(this);
                deleteButton.setText("Delete PDF");
                deleteButton.setAllCaps(false);
                deleteButton.setOnClickListener(v -> deletePdf(pdfIndex));
                pdfListContainer.addView(deleteButton);

            } catch (Exception ignored) {
            }
        }
    }

    private void deletePdf(int indexToDelete) {
        try {
            JSONArray oldArray = getPdfArray();
            JSONArray newArray = new JSONArray();

            for (int i = 0; i < oldArray.length(); i++) {
                if (i != indexToDelete) {
                    newArray.put(oldArray.getJSONObject(i));
                }
            }

            savePdfArray(newArray);
            loadFacultyUploadedPdfs();

            Toast.makeText(
                    this,
                    "PDF deleted. It will also disappear from student side.",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (Exception e) {
            Toast.makeText(this, "Failed to delete PDF.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPdf(Uri pdfUri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Open PDF"));

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "No PDF viewer found or file access expired.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private JSONArray getPdfArray() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_PDF_LIST, "[]");

        try {
            return new JSONArray(json);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    private void savePdfArray(JSONArray pdfArray) {
        getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .edit()
                .putString(KEY_PDF_LIST, pdfArray.toString())
                .apply();
    }

    private String getFileName(Uri uri) {
        String result = "Oral Pathology Lecture PDF";

        try {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            if (cursor != null) {
                try {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                    if (nameIndex >= 0 && cursor.moveToFirst()) {
                        result = cursor.getString(nameIndex);
                    }
                } finally {
                    cursor.close();
                }
            }
        } catch (Exception ignored) {
        }

        return result;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}