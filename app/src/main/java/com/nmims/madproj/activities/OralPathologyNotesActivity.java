package com.nmims.madproj.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.nmims.madproj.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class OralPathologyNotesActivity extends AppCompatActivity {

    private LinearLayout studentPdfListContainer;
    private TextView tvEmptyStudentPdfList;

    private static final String PREF_NAME = "OdontoPathNotes";
    private static final String KEY_PDF_LIST = "faculty_uploaded_pdfs";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oral_pathology_notes);
        com.nmims.madproj.utils.UiUtil.applyAmbientBackground(this);

        studentPdfListContainer = findViewById(R.id.studentPdfListContainer);
        tvEmptyStudentPdfList = findViewById(R.id.tvEmptyStudentPdfList);

        loadStudentPdfList();
    }

    private void loadStudentPdfList() {
        studentPdfListContainer.removeAllViews();

        JSONArray pdfArray = getPdfArray();

        if (pdfArray.length() == 0) {
            tvEmptyStudentPdfList.setVisibility(View.VISIBLE);
            return;
        }

        tvEmptyStudentPdfList.setVisibility(View.GONE);

        for (int i = 0; i < pdfArray.length(); i++) {
            try {
                JSONObject pdfObject = pdfArray.getJSONObject(i);
                String name = pdfObject.optString("name", "Lecture PDF");
                String uriString = pdfObject.optString("uri", "");

                TextView title = new TextView(this);
                title.setText((i + 1) + ". " + name);
                title.setTextColor(0xFFFFFFFF);
                title.setTextSize(16);
                title.setPadding(8, 14, 8, 6);
                studentPdfListContainer.addView(title);

                MaterialButton openButton = new MaterialButton(this);
                openButton.setText("Open Lecture PDF");
                openButton.setAllCaps(false);
                openButton.setOnClickListener(v -> openPdf(Uri.parse(uriString)));
                studentPdfListContainer.addView(openButton);

            } catch (Exception ignored) {
            }
        }
    }

    private void openPdf(Uri pdfUri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Open Faculty Lecture PDF"));

        } catch (Exception e) {
            Toast.makeText(this, "No PDF viewer found or file access expired.", Toast.LENGTH_LONG).show();
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
}