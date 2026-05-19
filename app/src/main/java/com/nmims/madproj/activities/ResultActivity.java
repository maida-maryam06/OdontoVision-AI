/*
 * File: ResultActivity.java
 * Purpose: Show score, review list, and actions to retake or view stats.
 */

package com.nmims.madproj.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nmims.madproj.R;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        com.nmims.madproj.utils.UiUtil.applyAmbientBackground(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Results");
        }

        TextView scoreText = findViewById(R.id.scoreText);
        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 0);
        scoreText.setText(score + "/" + total);

        com.google.android.material.button.MaterialButton btnRetake = findViewById(R.id.btnRetake);
        com.google.android.material.button.MaterialButton btnStats = findViewById(R.id.btnStats);
        com.google.android.material.button.MaterialButton btnHome = findViewById(R.id.btnHome);

        btnRetake.setOnClickListener(v -> {
            // Go back to category selection to retake
            android.content.Intent i = new android.content.Intent(this, QuizSelectionActivity.class);
            i.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        });
        btnStats.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, StatsActivity.class));
        });
        btnHome.setOnClickListener(v -> {
            android.content.Intent i = new android.content.Intent(this, MainActivity.class);
            i.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}


