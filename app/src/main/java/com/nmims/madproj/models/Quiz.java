/*
 * File: Quiz.java
 * Purpose: Room entity representing a quiz session metadata.
 */

package com.nmims.madproj.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

/**
 * Represents a quiz with title, category, and performance summary fields.
 */
@Entity(tableName = "quizzes")
public class Quiz {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String title;

    private String category;

    private long timestamp;

    private int totalQuestions;

    private int score;

    /**
     * Default no-args constructor required by Room.
     */
    public Quiz() {
        this.title = "";
        this.category = null;
        this.timestamp = System.currentTimeMillis();
        this.totalQuestions = 0;
        this.score = 0;
    }

    /**
     * Full constructor.
     */
    @Ignore
    public Quiz(@NonNull String title,
                String category,
                long timestamp,
                int totalQuestions,
                int score) {
        this.title = title;
        this.category = category;
        this.timestamp = timestamp;
        this.totalQuestions = Math.max(0, totalQuestions);
        this.score = Math.max(0, score);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = Math.max(0, totalQuestions);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = Math.max(0, score);
    }
}


