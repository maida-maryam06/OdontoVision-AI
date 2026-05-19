/*
 * File: GradingEngine.java
 * Purpose: Evaluate quiz responses and generate feedback/analytics.
 */

package com.nmims.madproj.utils;

import com.nmims.madproj.models.Question;
import com.nmims.madproj.models.QuizResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GradingEngine {

    private GradingEngine() {}

    public static QuizResult gradeQuiz(int quizId, String userId, List<Question> questions, List<Integer> userAnswers, long timeTakenMs) {
        if (questions == null || questions.isEmpty()) return new QuizResult();
        int correct = 0;
        int wrong = 0;
        for (int i = 0; i < questions.size(); i++) {
            int user = (i < userAnswers.size()) ? userAnswers.get(i) : 0;
            if (user > 0 && user == questions.get(i).getCorrectAnswer()) {
                correct++;
            } else if (user > 0) {
                wrong++;
            }
        }
        int score = correct;
        QuizResult result = new QuizResult(quizId, userId, score, questions.size(), timeTakenMs, correct, wrong, System.currentTimeMillis());
        return result;
    }

    /**
     * Provide per-question explanation list. If explanation is empty, craft a basic one.
     */
    public static List<String> generateExplanation(List<Question> questions) {
        List<String> explanations = new ArrayList<>();
        if (questions == null) return explanations;
        for (Question q : questions) {
            String exp = q.getExplanation();
            if (exp == null || exp.trim().isEmpty()) {
                exp = "Review the underlying concept. The correct option is #" + q.getCorrectAnswer();
            }
            explanations.add(exp);
        }
        return explanations;
    }

    /**
     * Identify weak areas by counting incorrect attempts per category.
     */
    public static List<String> identifyWeakAreas(List<Question> questions, List<Integer> userAnswers) {
        Map<String, Integer> mistakesByCategory = new HashMap<>();
        if (questions == null) return new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            int user = (i < userAnswers.size()) ? userAnswers.get(i) : 0;
            Question q = questions.get(i);
            if (user > 0 && user != q.getCorrectAnswer()) {
                String cat = q.getCategory() != null ? q.getCategory() : "General";
                mistakesByCategory.put(cat, mistakesByCategory.getOrDefault(cat, 0) + 1);
            }
        }
        // Simple ranking by mistake count
        List<String> weakAreas = new ArrayList<>(mistakesByCategory.keySet());
        weakAreas.sort((a, b) -> mistakesByCategory.get(b) - mistakesByCategory.get(a));
        return weakAreas;
    }
}


