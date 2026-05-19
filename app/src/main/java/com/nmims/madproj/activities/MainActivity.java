/*
 * File: MainActivity.java
 * Purpose: Entry screen to navigate to quiz selection, upload, and stats.
 */

package com.nmims.madproj.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.nmims.madproj.R;
import com.nmims.madproj.repository.QuizRepository;
import com.nmims.madproj.services.FirestoreSyncService;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        com.nmims.madproj.utils.UiUtil.applyAmbientBackground(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ensure preloaded questions are seeded
        QuizRepository quizRepository = new QuizRepository(getApplication());
        quizRepository.ensurePreloadedQuestions();

        // Check if user is logged in or skipped auth
        FirebaseUser currentUser = mAuth.getCurrentUser();
        boolean skipAuth = getSharedPreferences("QuizApp", MODE_PRIVATE).getBoolean("skip_auth", false);
        
        // If no user and no skip auth, redirect to login
        if (currentUser == null && !skipAuth) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        
        // Auto-set guest mode if explicitly skipped
        if (skipAuth && currentUser == null) {
            // Guest mode is already set, no need to do anything
        }

        // Welcome message with username
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        if (tvWelcome != null) {
            if (skipAuth && currentUser == null) {
                // Guest user
                String guestName = getSharedPreferences("QuizApp", MODE_PRIVATE).getString("username", "Guest");
                tvWelcome.setText("Hello, " + guestName);
            } else if (currentUser != null) {
                // Firebase user - try to get username from Firestore
                db.collection("users").document(currentUser.getUid()).get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                DocumentSnapshot doc = task.getResult();
                                String username = doc.getString("username");
                                if (username != null) {
                                    tvWelcome.setText("Hello, " + username);
                                } else {
                                    tvWelcome.setText("Hello, " + (currentUser.getEmail() != null ? currentUser.getEmail().split("@")[0] : "User"));
                                }
                            } else {
                                tvWelcome.setText("Hello, " + (currentUser.getEmail() != null ? currentUser.getEmail().split("@")[0] : "User"));
                            }
                        });
            }
        }

        MaterialButton btnPreloaded = findViewById(R.id.btnPreloaded);
        MaterialButton btnGenerate = findViewById(R.id.btnGenerate);
        MaterialButton btnStats = findViewById(R.id.btnStats);
        MaterialButton btnChat = findViewById(R.id.btnChat);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);

        btnPreloaded.setText("Student Mode");
        btnGenerate.setText("Faculty Mode");
        btnStats.setText("Student Performance Report");
        btnChat.setText("Ask OdontoPath AI Tutor");

        btnPreloaded.setOnClickListener(v ->
                startActivity(new Intent(this, StudentDashboardActivity.class))
        );

        btnGenerate.setOnClickListener(v ->
                startActivity(new Intent(this, FacultyDashboardActivity.class))
        );

        btnStats.setOnClickListener(v ->
                startActivity(new Intent(this, StatsActivity.class))
        );
        
        if (btnChat != null) {
            btnChat.setOnClickListener(v -> startActivity(new Intent(this, ChatbotActivity.class)));
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                // Logout from Firebase if logged in
                if (currentUser != null) {
                    mAuth.signOut();
                }
                // Clear ALL local session data
                getSharedPreferences("QuizApp", MODE_PRIVATE)
                        .edit()
                        .clear()
                        .apply();
                
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                // Redirect to LoginActivity
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });
        }

        // Sync Firestore questions on app start
        new FirestoreSyncService(getApplication()).syncApprovedQuestions();

        Toast.makeText(this, "Welcome to OdontoPath AI Tutor!", Toast.LENGTH_SHORT).show();
    }
}


