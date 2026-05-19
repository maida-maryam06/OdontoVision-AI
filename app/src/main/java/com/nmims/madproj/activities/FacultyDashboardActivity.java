package com.nmims.madproj.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.nmims.madproj.R;

public class FacultyDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_dashboard);
        com.nmims.madproj.utils.UiUtil.applyAmbientBackground(this);

        MaterialButton btnUploadPdf = findViewById(R.id.btnUploadPdf);
        MaterialButton btnGenerateQuiz = findViewById(R.id.btnGenerateQuiz);
        MaterialButton btnQuizSolutions = findViewById(R.id.btnQuizSolutions);
        MaterialButton btnStudentReport = findViewById(R.id.btnStudentReport);
        MaterialButton btnQuizHistory = findViewById(R.id.btnQuizHistory);

        btnUploadPdf.setOnClickListener(v ->
                startActivity(new Intent(this, UploadNoteActivity.class))
        );

        btnGenerateQuiz.setOnClickListener(v ->
                startActivity(new Intent(this, GenerateQuizActivity.class))
        );

        btnStudentReport.setOnClickListener(v ->
                startActivity(new Intent(this, StatsActivity.class))
        );

        btnQuizHistory.setOnClickListener(v ->
                startActivity(new Intent(this, StatsActivity.class))
        );

        btnQuizSolutions.setOnClickListener(v ->
                startActivity(new Intent(this, QuizSolutionUploadActivity.class))
        );
    }
}