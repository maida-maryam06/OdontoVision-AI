/*
 * File: QuizActivity.java
 * Purpose: Presents questions, handles answers, and calculates score.
 */

package com.nmims.madproj.activities;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.nmims.madproj.R;
import com.nmims.madproj.models.Question;
import com.nmims.madproj.models.Quiz;
import com.nmims.madproj.repository.QuizRepository;
import com.nmims.madproj.viewmodel.QuizViewModel;

import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private QuizViewModel viewModel;
    private TextView questionText, timerText;
    private RadioGroup optionsGroup;
    private RadioButton opt1, opt2, opt3, opt4;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        com.nmims.madproj.utils.UiUtil.applyAmbientBackground(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quiz");
        }

        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);
        String category = getIntent().getStringExtra("category");
        viewModel.loadQuestions(category != null ? category : "Java");

        questionText = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        opt1 = findViewById(R.id.option1);
        opt2 = findViewById(R.id.option2);
        opt3 = findViewById(R.id.option3);
        opt4 = findViewById(R.id.option4);
        progressBar = findViewById(R.id.progressBar);
        timerText = findViewById(R.id.timerText);

        MaterialButton btnNext = findViewById(R.id.btnNext);
        MaterialButton btnPrev = findViewById(R.id.btnPrev);
        MaterialButton btnSubmit = findViewById(R.id.btnSubmit);

        viewModel.getQuestions().observe(this, this::renderQuestion);
        viewModel.getCurrentIndex().observe(this, idx -> renderQuestion(viewModel.getQuestions().getValue()));
        viewModel.getRemainingMillis().observe(this, millis -> timerText.setText(formatTime(millis)));

        btnNext.setOnClickListener(v -> {
            storeCurrentAnswer();
            viewModel.nextQuestion();
        });
        btnPrev.setOnClickListener(v -> {
            storeCurrentAnswer();
            viewModel.previousQuestion();
        });
        btnSubmit.setOnClickListener(v -> {
            storeCurrentAnswer();
            viewModel.calculateScore();
        });

        viewModel.isQuizCompleted().observe(this, completed -> {
            if (completed != null && completed) {
                Integer score = viewModel.getScore().getValue();
                List<Question> qs = viewModel.getQuestions().getValue();
                int total = qs != null ? qs.size() : 0;

                // persist a Quiz summary for history
                // persist a Quiz summary for history with student name
                try {
                    QuizRepository repo = new QuizRepository(getApplication());

                    String selectedCategory = getIntent().getStringExtra("category");
                    if (selectedCategory == null || selectedCategory.trim().isEmpty()) {
                        selectedCategory = "General";
                    }

                    String studentName = getSharedPreferences("QuizApp", MODE_PRIVATE)
                            .getString("username", "Unknown Student");

                    if (studentName == null || studentName.trim().isEmpty()) {
                        studentName = "Unknown Student";
                    }

                    String quizTitle = studentName + " | Quiz - " + selectedCategory;

                    Quiz quiz = new Quiz(
                            quizTitle,
                            selectedCategory,
                            System.currentTimeMillis(),
                            total,
                            score != null ? score : 0
                    );

                    repo.insertQuiz(quiz);

                } catch (Exception ignored) {
                }

                android.content.Intent i = new android.content.Intent(this, ResultActivity.class);
                i.putExtra("score", score != null ? score : 0);
                i.putExtra("total", total);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void storeCurrentAnswer() {
        int checked = optionsGroup.getCheckedRadioButtonId();
        int answer = 0;
        if (checked == R.id.option1) answer = 1;
        else if (checked == R.id.option2) answer = 2;
        else if (checked == R.id.option3) answer = 3;
        else if (checked == R.id.option4) answer = 4;
        viewModel.submitAnswer(answer);
    }

    private void renderQuestion(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            return;
        }
        Integer idx = viewModel.getCurrentIndex().getValue();
        if (idx == null || idx < 0 || idx >= questions.size()) {
            // Reset to valid index if out of bounds
            if (questions.size() > 0) {
                viewModel.resetToFirstQuestion();
                idx = 0;
            } else {
                return;
            }
        }
        
        Question q = questions.get(idx);
        questionText.setText(q.getQuestionText());
        opt1.setText(q.getOption1());
        opt2.setText(q.getOption2());
        opt3.setText(q.getOption3());
        opt4.setText(q.getOption4());
        progressBar.setMax(questions.size());
        progressBar.setProgress(idx + 1);
        
        // Restore previously selected answer if any
        optionsGroup.clearCheck();
        List<Integer> userAnswers = viewModel.getUserAnswers();
        if (userAnswers != null && idx < userAnswers.size() && userAnswers.get(idx) > 0) {
            int selected = userAnswers.get(idx);
            if (selected == 1) optionsGroup.check(R.id.option1);
            else if (selected == 2) optionsGroup.check(R.id.option2);
            else if (selected == 3) optionsGroup.check(R.id.option3);
            else if (selected == 4) optionsGroup.check(R.id.option4);
        }
    }

    private String formatTime(long millis) {
        long totalSeconds = millis / 1000L;
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        String mm = (minutes < 10 ? "0" : "") + minutes;
        String ss = (seconds < 10 ? "0" : "") + seconds;
        return mm + ":" + ss;
    }
}


