package com.nmims.madproj.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nmims.madproj.R;
import com.nmims.madproj.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextInputEditText etUsername, etEmail, etPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        UiUtil.applyAmbientBackground(this);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.topBar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        MaterialButton btnCreateStudent = findViewById(R.id.btnCreateStudent);
        MaterialButton btnCreateFaculty = findViewById(R.id.btnCreateFaculty);

        btnCreateStudent.setOnClickListener(v -> registerUser("student"));
        btnCreateFaculty.setOnClickListener(v -> registerUser("faculty"));
    }

    private void registerUser(String role) {
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username required");
            return;
        }

        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Valid email required");
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(vTask -> {
                                    })
                                    .addOnFailureListener(e -> {
                                    });

                            Map<String, Object> userData = new HashMap<>();
                            userData.put("username", username);
                            userData.put("email", email);
                            userData.put("role", role);
                            userData.put("createdAt", System.currentTimeMillis());

                            db.collection("users").document(user.getUid()).set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        saveLocalSession(username, email, password, role, false);
                                        showSuccessAndOpenDashboard(role);
                                    })
                                    .addOnFailureListener(e -> {
                                        saveLocalSession(username, email, password, role, false);
                                        showSuccessAndOpenDashboard(role);
                                    });
                        }

                    } else {
                        handleRegistrationFailure(task.getException(), username, email, password, role);
                    }
                });
    }

    private void handleRegistrationFailure(Exception exception, String username, String email, String password, String role) {
        String errorMessage = "Registration failed";

        if (exception != null && exception.getMessage() != null) {
            String error = exception.getMessage();

            if (error.contains("CONFIGURATION_NOT_FOUND") ||
                    error.contains("RecaptchaAction") ||
                    error.contains("RECAPTCHA") ||
                    error.contains("internal error")) {

                Toast.makeText(this, "Firebase not configured. Creating local demo account...", Toast.LENGTH_SHORT).show();

                saveLocalSession(username, email, password, role, true);
                showSuccessAndOpenDashboard(role);
                return;

            } else if (error.contains("email-already-in-use")) {
                errorMessage = "This email is already registered. Please login instead.";
            } else if (error.contains("weak-password")) {
                errorMessage = "Password is too weak. Use a stronger password.";
            } else if (error.contains("invalid-email")) {
                errorMessage = "Invalid email address. Please check your email.";
            } else {
                errorMessage = "Registration failed: " + error;
            }
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void saveLocalSession(String username, String email, String password, String role, boolean skipAuth) {
        getSharedPreferences("QuizApp", MODE_PRIVATE)
                .edit()
                .putBoolean("skip_auth", skipAuth)
                .putString("username", username)
                .putString("email", email)
                .putString("password_hash", String.valueOf(password.hashCode()))
                .putString("user_role", role)
                .putBoolean("is_logged_in", true)
                .apply();
    }

    private void showSuccessAndOpenDashboard(String role) {
        String message;

        if (role.equals("faculty")) {
            message = "Faculty account created successfully.";
        } else {
            message = "Student account created successfully.";
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            openDashboardByRole(role);
            finish();
        }, 1200);
    }

    private void openDashboardByRole(String role) {
        if (role != null && role.equals("faculty")) {
            startActivity(new Intent(this, FacultyDashboardActivity.class));
        } else {
            startActivity(new Intent(this, StudentDashboardActivity.class));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}