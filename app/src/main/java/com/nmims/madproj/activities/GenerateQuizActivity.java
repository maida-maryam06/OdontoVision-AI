package com.nmims.madproj.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.nmims.madproj.R;
import com.nmims.madproj.models.Question;
import com.nmims.madproj.repository.QuizRepository;
import com.nmims.madproj.viewmodel.UploadViewModel;

public class GenerateQuizActivity extends AppCompatActivity {

    private UploadViewModel viewModel;
    private Uri selectedFile;
    private QuizRepository quizRepository;
    private ActivityResultLauncher<Intent> pickFileLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_quiz);
        com.nmims.madproj.utils.UiUtil.applyAmbientBackground(this);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.topBar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        viewModel = new ViewModelProvider(this).get(UploadViewModel.class);
        quizRepository = new QuizRepository(getApplication());

        pickFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedFile = result.getData().getData();

                        if (selectedFile != null) {
                            Toast.makeText(this, "File selected. Extracting text...", Toast.LENGTH_SHORT).show();
                            viewModel.processFile(selectedFile);
                        }
                    }
                }
        );

        MaterialButton btnGallery = findViewById(R.id.btnGallery);
        MaterialButton btnExtract = findViewById(R.id.btnExtract);
        MaterialButton btnGenerate = findViewById(R.id.btnGenerate);
        TextInputEditText etNum = findViewById(R.id.etNumQuestions);
        android.widget.TextView tvExtracted = findViewById(R.id.tvExtracted);

        btnGallery.setOnClickListener(v -> openFilePicker());

        btnExtract.setOnClickListener(v -> {
            if (selectedFile != null) {
                viewModel.processFile(selectedFile);
            } else {
                Toast.makeText(this, "Select an oral pathology image or PDF first.", Toast.LENGTH_SHORT).show();
            }
        });

        btnGenerate.setOnClickListener(v -> {
            int num = 10;

            try {
                num = Integer.parseInt(String.valueOf(etNum.getText()));
            } catch (Exception ignored) {
            }

            viewModel.generateQuiz(num, "Medium");
        });

        viewModel.getExtractedText().observe(this, text -> {
            if (text != null && !text.isEmpty()) {
                tvExtracted.setText(text);
            } else {
                tvExtracted.setText("No text extracted yet.");
            }
        });

        viewModel.getError().observe(this, err -> {
            if (err != null) {
                Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getProcessing().observe(this, processing -> {
            if (processing != null && processing) {
                Toast.makeText(this, "Processing...", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getGeneratedQuestions().observe(this, questions -> {
            if (questions == null || questions.isEmpty()) {
                return;
            }

            quizRepository.deleteQuestionsByCategory("Generated");

            for (Question q : questions) {
                if (q.getCategory() == null || q.getCategory().isEmpty()) {
                    q.setCategory("Generated");
                }

                if (q.getDifficulty() == null || q.getDifficulty().isEmpty()) {
                    q.setDifficulty("Medium");
                }
            }

            quizRepository.insertQuestions(questions);

            Toast.makeText(
                    this,
                    "Generated " + questions.size() + " odontogenic pathology questions.",
                    Toast.LENGTH_SHORT
            ).show();

            Intent i = new Intent(this, QuizActivity.class);
            i.putExtra("category", "Generated");
            startActivity(i);
        });
    }

    private void openFilePicker() {
        Intent intent;

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");

            String[] mimeTypes = {"image/*", "application/pdf"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            String[] mimeTypes = {"image/*", "application/pdf"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }

        pickFileLauncher.launch(Intent.createChooser(intent, "Upload Image/PDF to Generate Odontogenic Quiz"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}