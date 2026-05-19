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
import com.nmims.madproj.R;
import com.nmims.madproj.utils.UiUtil;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputEditText etUsername, etPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UiUtil.applyAmbientBackground(this);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            String savedRole = getSharedPreferences("QuizApp", MODE_PRIVATE)
                    .getString("user_role", "student");
            openDashboardByRole(savedRole);
            finish();
            return;
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        MaterialButton btnLoginStudent = findViewById(R.id.btnLoginStudent);
        MaterialButton btnLoginFaculty = findViewById(R.id.btnLoginFaculty);
        MaterialButton btnGuestStudent = findViewById(R.id.btnGuestStudent);
        MaterialButton btnGuestFaculty = findViewById(R.id.btnGuestFaculty);
        MaterialButton btnSignUp = findViewById(R.id.btnSignUp);

        btnLoginStudent.setOnClickListener(v -> loginUser("student"));
        btnLoginFaculty.setOnClickListener(v -> loginUser("faculty"));

        btnGuestStudent.setOnClickListener(v -> continueAsGuest("student"));
        btnGuestFaculty.setOnClickListener(v -> continueAsGuest("faculty"));

        btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        new android.os.Handler().postDelayed(() -> {
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(this, "Use Student or Faculty Guest for demo access.", Toast.LENGTH_LONG).show();
            }
        }, 3000);
    }

    private void loginUser(String role) {
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username/Email required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password required");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        String email = username.contains("@") ? username : username + "@quizgen.com";

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            getSharedPreferences("QuizApp", MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("skip_auth", false)
                                    .putBoolean("is_logged_in", true)
                                    .putString("username", username)
                                    .putString("user_role", role)
                                    .apply();

                            if (role.equals("faculty")) {
                                Toast.makeText(this, "Welcome Faculty!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Welcome Student!", Toast.LENGTH_SHORT).show();
                            }

                            openDashboardByRole(role);
                            finish();
                        }

                    } else {
                        Exception exception = task.getException();
                        String errorMessage = "Login failed";

                        if (exception != null && exception.getMessage() != null) {
                            String error = exception.getMessage();

                            if (error.contains("CONFIGURATION_NOT_FOUND") ||
                                    error.contains("RecaptchaAction") ||
                                    error.contains("RECAPTCHA") ||
                                    error.contains("internal error")) {

                                String savedEmail = getSharedPreferences("QuizApp", MODE_PRIVATE)
                                        .getString("email", "");
                                String savedPasswordHash = getSharedPreferences("QuizApp", MODE_PRIVATE)
                                        .getString("password_hash", "");

                                if (!savedEmail.isEmpty() &&
                                        savedPasswordHash.equals(String.valueOf(password.hashCode()))) {

                                    getSharedPreferences("QuizApp", MODE_PRIVATE)
                                            .edit()
                                            .putBoolean("skip_auth", true)
                                            .putBoolean("is_logged_in", true)
                                            .putString("username", username)
                                            .putString("user_role", role)
                                            .apply();

                                    Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
                                    openDashboardByRole(role);
                                    finish();

                                } else {
                                    Toast.makeText(this, "Firebase not configured. Please register first or use guest mode.", Toast.LENGTH_LONG).show();
                                }

                                return;

                            } else if (error.contains("password") || error.contains("invalid")) {
                                errorMessage = "Invalid email or password";
                            } else if (error.contains("user-not-found")) {
                                errorMessage = "Account not found. Please register first.";
                            } else {
                                errorMessage = "Login failed: " + error;
                            }
                        }

                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void continueAsGuest(String role) {
        String username;

        if (role.equals("faculty")) {
            username = "Guest Faculty";
        } else {
            username = "Guest Student";
        }

        getSharedPreferences("QuizApp", MODE_PRIVATE)
                .edit()
                .putBoolean("skip_auth", true)
                .putString("username", username)
                .putString("user_role", role)
                .putBoolean("is_logged_in", true)
                .apply();

        if (role.equals("faculty")) {
            Toast.makeText(this, "Continuing as Faculty Guest", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Continuing as Student Guest", Toast.LENGTH_SHORT).show();
        }

        openDashboardByRole(role);
        finish();
    }

    private void openDashboardByRole(String role) {
        if (role != null && role.equals("faculty")) {
            startActivity(new Intent(this, FacultyDashboardActivity.class));
        } else {
            startActivity(new Intent(this, StudentDashboardActivity.class));
        }
    }
}