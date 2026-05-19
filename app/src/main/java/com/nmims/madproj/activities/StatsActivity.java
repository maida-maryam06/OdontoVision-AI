package com.nmims.madproj.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.nmims.madproj.R;
import com.nmims.madproj.adapters.QuizHistoryAdapter;
import com.nmims.madproj.models.Quiz;
import com.nmims.madproj.repository.QuizRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class StatsActivity extends AppCompatActivity {

    private LinearLayout studentListContainer;
    private TextView tvSelectedStudent;
    private TextView tvTotalAttempts;
    private TextView tvAverageScore;
    private TextView tvHighestScore;
    private TextView tvLowestScore;
    private TextView tvFacultyInterpretation;

    private final List<Quiz> allQuizzes = new ArrayList<>();
    private final List<Quiz> filteredQuizzes = new ArrayList<>();
    private QuizHistoryAdapter adapter;

    private String selectedStudent = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        com.nmims.madproj.utils.UiUtil.applyAmbientBackground(this);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.topBar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Student Performance Report");
            }
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        studentListContainer = findViewById(R.id.studentListContainer);
        tvSelectedStudent = findViewById(R.id.tvSelectedStudent);
        tvTotalAttempts = findViewById(R.id.tvTotalAttempts);
        tvAverageScore = findViewById(R.id.tvAverageScore);
        tvHighestScore = findViewById(R.id.tvHighestScore);
        tvLowestScore = findViewById(R.id.tvLowestScore);
        tvFacultyInterpretation = findViewById(R.id.tvFacultyInterpretation);

        RecyclerView recycler = findViewById(R.id.recyclerHistory);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new QuizHistoryAdapter(filteredQuizzes, quiz -> {});
        recycler.setAdapter(adapter);

        QuizRepository repo = new QuizRepository(getApplication());

        repo.getAllQuizzes().observe(this, quizzes -> {
            allQuizzes.clear();

            if (quizzes != null) {
                allQuizzes.addAll(quizzes);
            }

            buildStudentButtons();

            if (selectedStudent == null || selectedStudent.trim().isEmpty()) {
                Set<String> students = extractStudents(allQuizzes);
                if (!students.isEmpty()) {
                    selectedStudent = students.iterator().next();
                }
            }

            filterByStudent(selectedStudent);
        });
    }

    private void buildStudentButtons() {
        studentListContainer.removeAllViews();

        Set<String> students = extractStudents(allQuizzes);

        if (students.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No student quiz attempts found yet.");
            empty.setTextColor(0xFFE0E0E0);
            empty.setTextSize(15);
            empty.setPadding(12, 12, 12, 12);
            studentListContainer.addView(empty);
            return;
        }

        for (String student : students) {
            MaterialButton btn = new MaterialButton(this);
            btn.setText(student);
            btn.setAllCaps(false);
            btn.setOnClickListener(v -> {
                selectedStudent = student;
                filterByStudent(student);
            });

            studentListContainer.addView(btn);
        }
    }

    private Set<String> extractStudents(List<Quiz> quizzes) {
        Set<String> students = new LinkedHashSet<>();

        for (Quiz quiz : quizzes) {
            String student = getStudentNameFromQuiz(quiz);

            if (student != null && !student.trim().isEmpty()) {
                students.add(student);
            }
        }

        return students;
    }

    private void filterByStudent(String studentName) {
        filteredQuizzes.clear();

        if (studentName == null || studentName.trim().isEmpty()) {
            adapter.notifyDataSetChanged();
            updatePerformanceSummary(filteredQuizzes, "");
            return;
        }

        for (Quiz quiz : allQuizzes) {
            String quizStudent = getStudentNameFromQuiz(quiz);

            if (studentName.equals(quizStudent)) {
                filteredQuizzes.add(quiz);
            }
        }

        adapter.notifyDataSetChanged();
        updatePerformanceSummary(filteredQuizzes, studentName);
    }

    private String getStudentNameFromQuiz(Quiz quiz) {
        if (quiz == null || quiz.getTitle() == null) {
            return "Unknown Student";
        }

        String title = quiz.getTitle();

        if (title.contains("|")) {
            return title.split("\\|")[0].trim();
        }

        return "Unknown Student";
    }

    private void updatePerformanceSummary(List<Quiz> quizzes, String studentName) {
        if (studentName == null || studentName.trim().isEmpty()) {
            tvSelectedStudent.setText("Selected Student: None");
        } else {
            tvSelectedStudent.setText("Selected Student: " + studentName);
        }

        if (quizzes == null || quizzes.isEmpty()) {
            tvTotalAttempts.setText("Total Attempts: 0");
            tvAverageScore.setText("Average Score: 0%");
            tvHighestScore.setText("Highest Score: 0%");
            tvLowestScore.setText("Lowest Score: 0%");
            tvFacultyInterpretation.setText(
                    "Faculty Interpretation: No quiz attempts available for this student yet."
            );
            return;
        }

        int totalAttempts = quizzes.size();
        double totalPercentage = 0;
        double highest = 0;
        double lowest = 100;

        String weakestCategory = "Not identified";
        double weakestScore = 100;

        for (Quiz quiz : quizzes) {
            double percentage = getQuizPercentage(quiz);

            totalPercentage += percentage;

            if (percentage > highest) {
                highest = percentage;
            }

            if (percentage < lowest) {
                lowest = percentage;
            }

            if (percentage < weakestScore) {
                weakestScore = percentage;
                weakestCategory = quiz.getCategory() != null ? quiz.getCategory() : "Recent Quiz";
            }
        }

        double average = totalPercentage / totalAttempts;

        tvTotalAttempts.setText("Total Attempts: " + totalAttempts);
        tvAverageScore.setText("Average Score: " + formatPercent(average));
        tvHighestScore.setText("Highest Score: " + formatPercent(highest));
        tvLowestScore.setText("Lowest Score: " + formatPercent(lowest));

        String interpretation;

        if (average >= 80) {
            interpretation =
                    "Faculty Interpretation: " + studentName + " is performing strongly. " +
                            "The student shows good understanding of odontogenic oral pathology concepts. " +
                            "Faculty can now move this student toward case-based and diagnostic image-based questions.";
        } else if (average >= 60) {
            interpretation =
                    "Faculty Interpretation: " + studentName + " has satisfactory performance but needs reinforcement. " +
                            "Faculty should provide targeted revision and additional MCQs.";
        } else {
            interpretation =
                    "Faculty Interpretation: " + studentName + " needs focused support. " +
                            "Faculty should recommend reading notes, AI tutor revision, and repeated practice quizzes.";
        }

        interpretation += "\n\nWeak Area Indicator: Lowest performance appears in " +
                weakestCategory + " with approximately " + formatPercent(weakestScore) + ".";

        tvFacultyInterpretation.setText(interpretation);
    }

    private double getQuizPercentage(Quiz quiz) {
        int score = quiz.getScore();
        int total = quiz.getTotalQuestions();

        if (total <= 0) {
            return 0;
        }

        return ((double) score / (double) total) * 100.0;
    }

    private String formatPercent(double value) {
        return String.format(Locale.US, "%.1f%%", value);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}