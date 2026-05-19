/*
 * File: QuizRepository.java
 * Purpose: Repository mediating between ViewModels and Room/Remote sources.
 */

package com.nmims.madproj.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.nmims.madproj.database.QuestionDao;
import com.nmims.madproj.database.QuizDao;
import com.nmims.madproj.database.QuizDatabase;
import com.nmims.madproj.database.QuizResultDao;
import com.nmims.madproj.models.Question;
import com.nmims.madproj.models.Quiz;
import com.nmims.madproj.utils.Constants;
import com.nmims.madproj.utils.PreloadedQuestions;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Provides reactive access to questions and handles background writes.
 */
public class QuizRepository {

    private final QuestionDao questionDao;
    private final QuizDao quizDao;
    private final QuizResultDao quizResultDao;
    private final ExecutorService ioExecutor;

    public QuizRepository(Application application) {
        QuizDatabase db = QuizDatabase.getInstance(application);
        this.questionDao = db.questionDao();
        this.quizDao = db.quizDao();
        this.quizResultDao = db.quizResultDao();
        this.ioExecutor = Executors.newSingleThreadExecutor();

        // Seed preloaded questions if categories are empty
        ensurePreloadedQuestions();
    }

    public LiveData<List<Question>> getAllQuestions() {
        return questionDao.getAllQuestions();
    }

    public LiveData<List<Question>> getQuestionsByCategory(String category) {
        return questionDao.getQuestionsByCategory(category);
    }

    public LiveData<List<Question>> getQuestionsByDifficulty(String difficulty) {
        return questionDao.getQuestionsByDifficulty(difficulty);
    }

    public void insertQuestions(List<Question> questions) {
        if (questions == null || questions.isEmpty()) return;
        ioExecutor.execute(() -> questionDao.insertQuestions(questions));
    }

    public void deleteQuestionsByCategory(String category) {
        ioExecutor.execute(() -> questionDao.deleteByCategory(category));
    }

    public int getCountByCategory(String category) {
        try {
            return questionDao.getCountByCategory(category);
        } catch (Exception e) {
            return 0;
        }
    }

    public void ensurePreloadedQuestions() {
        // Ensure preloaded questions exist for each category
        ioExecutor.execute(() -> {
            try {
                int javaCount = questionDao.getCountByCategory(Constants.CATEGORY_JAVA);
                int androidCount = questionDao.getCountByCategory(Constants.CATEGORY_ANDROID);
                int dsaCount = questionDao.getCountByCategory(Constants.CATEGORY_DSA);
                
                List<Question> toInsert = new java.util.ArrayList<>();
                
                // Only add if category is empty
                if (javaCount == 0) {
                    toInsert.addAll(PreloadedQuestions.getJavaQuestions());
                }
                if (androidCount == 0) {
                    toInsert.addAll(PreloadedQuestions.getAndroidQuestions());
                }
                if (dsaCount == 0) {
                    toInsert.addAll(PreloadedQuestions.getDSAQuestions());
                }
                
                if (!toInsert.isEmpty()) {
                    questionDao.insertQuestions(toInsert);
                }
            } catch (Exception e) {
                // Log error if needed
            }
        });
    }

    public LiveData<List<Quiz>> getAllQuizzes() {
        return quizDao.getAllQuizzes();
    }

    public void insertQuiz(Quiz quiz) {
        ioExecutor.execute(() -> quizDao.insertQuiz(quiz));
    }

    public List<String> getDistinctCategories() {
        try {
            return questionDao.getDistinctCategories();
        } catch (Exception e) {
            // Fallback to default categories if database query fails
            return java.util.Arrays.asList(
                Constants.CATEGORY_JAVA,
                Constants.CATEGORY_ANDROID,
                Constants.CATEGORY_DSA
            );
        }
    }
}


