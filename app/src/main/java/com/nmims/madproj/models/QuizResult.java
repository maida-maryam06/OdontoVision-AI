/*
 * File: QuizResult.java
 * Purpose: Room entity representing a user's result for a specific quiz.
 */

package com.nmims.madproj.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Stores per-quiz attempt metrics and enables analytics.
 */
@Entity(tableName = "quiz_results",
        foreignKeys = @ForeignKey(
                entity = Quiz.class,
                parentColumns = "id",
                childColumns = "quizId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = {"quizId"})})
public class QuizResult {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int quizId;

    @NonNull
    private String userId;

    private int score;

    private int totalQuestions;

    private long timeTaken; // milliseconds

    private int correctAnswers;

    private int wrongAnswers;

    private long timestamp; // when the attempt finished

    /**
     * Default constructor required by Room.
     */
    public QuizResult() {
        this.userId = "anonymous";
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Full constructor.
     */
    @Ignore
    public QuizResult(int quizId,
                      @NonNull String userId,
                      int score,
                      int totalQuestions,
                      long timeTaken,
                      int correctAnswers,
                      int wrongAnswers,
                      long timestamp) {
        this.quizId = quizId;
        this.userId = userId;
        this.score = Math.max(0, score);
        this.totalQuestions = Math.max(0, totalQuestions);
        this.timeTaken = Math.max(0L, timeTaken);
        this.correctAnswers = Math.max(0, correctAnswers);
        this.wrongAnswers = Math.max(0, wrongAnswers);
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = Math.max(0, score);
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = Math.max(0, totalQuestions);
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = Math.max(0L, timeTaken);
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = Math.max(0, correctAnswers);
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = Math.max(0, wrongAnswers);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}


