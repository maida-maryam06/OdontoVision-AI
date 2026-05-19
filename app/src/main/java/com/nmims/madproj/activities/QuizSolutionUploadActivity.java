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

public class QuizSolutionUploadActivity extends AppCompatActivity {

    private LinearLayout solutionListContainer;
    private TextView tvEmptySolutionList;
    private ActivityResultLauncher<Intent> pickSolutionLauncher;

    private static final String PREF_NAME = "OdontoPathSolutions";
    private static final String KEY_SOLUTION_LIST = "faculty_uploaded_solutions";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_solution_upload);
        com.nmims.madproj.utils.UiUtil.applyAmbientBackground(this);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.topBar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        MaterialButton btnUploadSolution = findViewById(R.id.btnUploadSolution);
        solutionListContainer = findViewById(R.id.solutionListContainer);
        tvEmptySolutionList = findViewById(R.id.tvEmptySolutionList);

        pickSolutionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedFile = result.getData().getData();

                        if (selectedFile != null) {
                            saveFacultyUploadedSolution(selectedFile);
                            loadFacultyUploadedSolutions();
                        }
                    }
                }
        );

        btnUploadSolution.setOnClickListener(v -> openSolutionPicker());

        loadFacultyUploadedSolutions();
    }

    private void openSolutionPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        pickSolutionLauncher.launch(Intent.createChooser(intent, "Upload Quiz Solution PDF"));
    }

    private void saveFacultyUploadedSolution(Uri fileUri) {
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
            JSONArray solutionArray = getSolutionArray();

            for (int i = 0; i < solutionArray.length(); i++) {
                JSONObject existingSolution = solutionArray.getJSONObject(i);
                String existingUri = existingSolution.optString("uri", "");

                if (existingUri.equals(fileUriString)) {
                    Toast.makeText(this, "This solution PDF is already uploaded.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            JSONObject solutionObject = new JSONObject();
            solutionObject.put("name", fileName);
            solutionObject.put("uri", fileUriString);

            solutionArray.put(solutionObject);
            saveSolutionArray(solutionArray);

            Toast.makeText(this, "Quiz solution uploaded successfully.", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Failed to save solution: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadFacultyUploadedSolutions() {
        solutionListContainer.removeAllViews();

        JSONArray solutionArray = getSolutionArray();

        if (solutionArray.length() == 0) {
            tvEmptySolutionList.setVisibility(View.VISIBLE);
            return;
        }

        tvEmptySolutionList.setVisibility(View.GONE);

        for (int i = 0; i < solutionArray.length(); i++) {
            try {
                JSONObject solutionObject = solutionArray.getJSONObject(i);

                String name = solutionObject.optString("name", "Quiz Solution PDF");
                String uriString = solutionObject.optString("uri", "");

                final String solutionUriString = uriString;
                final int solutionIndex = i;

                TextView title = new TextView(this);
                title.setText((i + 1) + ". " + name);
                title.setTextColor(0xFFFFFFFF);
                title.setTextSize(16);
                title.setPadding(8, 14, 8, 6);
                solutionListContainer.addView(title);

                MaterialButton openButton = new MaterialButton(this);
                openButton.setText("Open Solution");
                openButton.setAllCaps(false);
                openButton.setOnClickListener(v -> openSolutionPdf(Uri.parse(solutionUriString)));
                solutionListContainer.addView(openButton);

                MaterialButton deleteButton = new MaterialButton(this);
                deleteButton.setText("Delete Solution");
                deleteButton.setAllCaps(false);
                deleteButton.setOnClickListener(v -> deleteSolution(solutionIndex));
                solutionListContainer.addView(deleteButton);

            } catch (Exception ignored) {
            }
        }
    }

    private void deleteSolution(int indexToDelete) {
        try {
            JSONArray oldArray = getSolutionArray();
            JSONArray newArray = new JSONArray();

            for (int i = 0; i < oldArray.length(); i++) {
                if (i != indexToDelete) {
                    newArray.put(oldArray.getJSONObject(i));
                }
            }

            saveSolutionArray(newArray);
            loadFacultyUploadedSolutions();

            Toast.makeText(
                    this,
                    "Solution deleted. It will also disappear from student result screen.",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (Exception e) {
            Toast.makeText(this, "Failed to delete solution.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openSolutionPdf(Uri pdfUri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Open Quiz Solution"));

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "No PDF viewer found or file access expired.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private JSONArray getSolutionArray() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_SOLUTION_LIST, "[]");

        try {
            return new JSONArray(json);
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    private void saveSolutionArray(JSONArray solutionArray) {
        getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .edit()
                .putString(KEY_SOLUTION_LIST, solutionArray.toString())
                .apply();
    }

    private String getFileName(Uri uri) {
        String result = "Quiz Solution PDF";

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