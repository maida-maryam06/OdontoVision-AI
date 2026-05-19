package com.nmims.madproj.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.nmims.madproj.R;

public class StudentDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        com.nmims.madproj.utils.UiUtil.applyAmbientBackground(this);

        MaterialButton btnSampleQuiz = findViewById(R.id.btnSampleQuiz);
        MaterialButton btnAskTutor = findViewById(R.id.btnAskTutor);
        MaterialButton btnStudentPerformance = findViewById(R.id.btnStudentPerformance);
        MaterialButton btnReadNotes = findViewById(R.id.btnReadNotes);
        MaterialButton btnARLearning = findViewById(R.id.btnARLearning);

        btnSampleQuiz.setOnClickListener(v ->
                startActivity(new Intent(this, QuizSelectionActivity.class))
        );

        btnAskTutor.setOnClickListener(v ->
                startActivity(new Intent(this, ChatbotActivity.class))
        );

        btnStudentPerformance.setOnClickListener(v ->
                startActivity(new Intent(this, StatsActivity.class))
        );

        btnReadNotes.setOnClickListener(v ->
                startActivity(new Intent(this, OralPathologyNotesActivity.class))
        );

        btnARLearning.setOnClickListener(v ->
                startActivity(new Intent(this, ARLearningActivity.class))
        );
    }
}