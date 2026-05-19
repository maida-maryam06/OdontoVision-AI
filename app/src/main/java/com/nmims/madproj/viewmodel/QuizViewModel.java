/*
 * File: QuizViewModel.java
 * Purpose: Holds quiz state, exposes questions and scoring via LiveData.
 */

package com.nmims.madproj.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nmims.madproj.models.Question;
import com.nmims.madproj.repository.QuizRepository;

import java.util.ArrayList;
import java.util.List;

public class QuizViewModel extends AndroidViewModel {

    private final QuizRepository repository;
    private final MutableLiveData<List<Question>> questionsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Integer> currentIndex = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> scoreLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> quizCompleted = new MutableLiveData<>(false);
    private final MutableLiveData<Long> remainingMillis = new MutableLiveData<>(0L);

    private final List<Integer> userAnswers = new ArrayList<>(); // stores 1-4 or 0 if unanswered

    public QuizViewModel(@NonNull Application application) {
        super(application);
        this.repository = new QuizRepository(application);
    }

    public LiveData<List<Question>> getQuestions() { return questionsLiveData; }
    public LiveData<Integer> getCurrentIndex() { return currentIndex; }
    public LiveData<Integer> getScore() { return scoreLiveData; }
    public LiveData<Boolean> isQuizCompleted() { return quizCompleted; }
    public LiveData<Long> getRemainingMillis() { return remainingMillis; }

    public void loadQuestions(String category) {
        repository.getQuestionsByCategory(category).observeForever(questions -> {
            if (questions == null) {
                questions = new ArrayList<>();
            }
            questionsLiveData.setValue(questions);
            userAnswers.clear();
            // Ensure userAnswers list matches questions size
            for (int i = 0; i < questions.size(); i++) {
                userAnswers.add(0);
            }
            // Reset to first question
            currentIndex.setValue(0);
            scoreLiveData.setValue(0);
            quizCompleted.setValue(false);
            startTimer(questions);
        });
    }

    public void submitAnswer(int optionIndex1To4) {
        List<Question> questions = questionsLiveData.getValue();
        Integer index = currentIndex.getValue();
        if (questions == null || index == null || index < 0 || index >= questions.size()) return;
        userAnswers.set(index, optionIndex1To4);
    }

    public void nextQuestion() {
        Integer index = currentIndex.getValue();
        List<Question> questions = questionsLiveData.getValue();
        if (index == null || questions == null || questions.isEmpty()) return;
        
        // Ensure index is within bounds
        if (index >= questions.size()) {
            index = questions.size() - 1;
            currentIndex.setValue(index);
            return;
        }
        
        // Move to next question if not at last
        if (index < questions.size() - 1) {
            currentIndex.setValue(index + 1);
        } else {
            // Already at last question - could auto-submit or just stay
            // For now, stay at last question
        }
    }
    
    public List<Integer> getUserAnswers() {
        return new ArrayList<>(userAnswers);
    }

    public void resetToFirstQuestion() {
        currentIndex.setValue(0);
    }

    public void previousQuestion() {
        Integer index = currentIndex.getValue();
        if (index == null) return;
        if (index > 0) currentIndex.setValue(index - 1);
    }

    public void calculateScore() {
        List<Question> questions = questionsLiveData.getValue();
        if (questions == null) return;
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            int user = (i < userAnswers.size()) ? userAnswers.get(i) : 0;
            if (user > 0 && user == questions.get(i).getCorrectAnswer()) score++;
        }
        scoreLiveData.setValue(score);
        quizCompleted.setValue(true);
        remainingMillis.setValue(0L);
    }

    public void resetQuiz() {
        List<Question> questions = questionsLiveData.getValue();
        if (questions != null) {
            userAnswers.clear();
            for (int i = 0; i < questions.size(); i++) userAnswers.add(0);
        }
        currentIndex.setValue(0);
        scoreLiveData.setValue(0);
        quizCompleted.setValue(false);
        startTimer(questionsLiveData.getValue());
    }

    private void startTimer(List<Question> questions) {
        // 30 seconds per question; minimum 1 minute if list empty
        long totalSeconds = (questions == null || questions.isEmpty()) ? 60 : questions.size() * 30L;
        final long[] remaining = { totalSeconds * 1000L };
        remainingMillis.setValue(remaining[0]);

        // Simple ticking thread (avoid CountDownTimer to keep dependency light here)
        new Thread(() -> {
            try {
                while (remaining[0] > 0 && Boolean.FALSE.equals(quizCompleted.getValue())) {
                    Thread.sleep(1000);
                    remaining[0] -= 1000;
                    remainingMillis.postValue(Math.max(0L, remaining[0]));
                }
                if (remaining[0] <= 0 && Boolean.FALSE.equals(quizCompleted.getValue())) {
                    calculateScore();
                }
            } catch (InterruptedException ignored) {
            }
        }).start();
    }
}


