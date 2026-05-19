/*
 * File: Question.java
 * Purpose: Room entity representing a quiz question with options, metadata, and explanation.
 */

package com.nmims.madproj.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

/**
 * Represents a single multiple-choice question stored in the local Room database.
 */
@Entity(tableName = "questions")
public class Question {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String questionText;

    @NonNull
    private String option1;

    @NonNull
    private String option2;

    @NonNull
    private String option3;

    @NonNull
    private String option4;

    /**
     * Index (1-4) indicating which option is correct.
     */
    private int correctAnswer;

    private String category; // e.g., "Java", "Android", "DSA"

    private String difficulty; // e.g., "Easy", "Medium", "Hard"

    private String explanation; // Optional explanation for the correct answer

    /**
     * Default no-args constructor required by Room.
     */
    public Question() {
        this.questionText = "";
        this.option1 = "";
        this.option2 = "";
        this.option3 = "";
        this.option4 = "";
        this.correctAnswer = 1;
        this.category = null;
        this.difficulty = null;
        this.explanation = null;
    }

    /**
     * Full constructor for building a Question instance.
     *
     * @param questionText   the question text
     * @param option1        first option
     * @param option2        second option
     * @param option3        third option
     * @param option4        fourth option
     * @param correctAnswer  correct option index (1-4)
     * @param category       category name
     * @param difficulty     difficulty level
     * @param explanation    explanation text
     */
    @Ignore
    public Question(@NonNull String questionText,
                    @NonNull String option1,
                    @NonNull String option2,
                    @NonNull String option3,
                    @NonNull String option4,
                    int correctAnswer,
                    String category,
                    String difficulty,
                    String explanation) {
        this.questionText = questionText;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctAnswer = clampCorrectAnswer(correctAnswer);
        this.category = category;
        this.difficulty = difficulty;
        this.explanation = explanation;
    }

    private int clampCorrectAnswer(int value) {
        // Ensure valid range [1,4]
        if (value < 1) {
            return 1;
        }
        if (value > 4) {
            return 4;
        }
        return value;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(@NonNull String questionText) {
        this.questionText = questionText;
    }

    @NonNull
    public String getOption1() {
        return option1;
    }

    public void setOption1(@NonNull String option1) {
        this.option1 = option1;
    }

    @NonNull
    public String getOption2() {
        return option2;
    }

    public void setOption2(@NonNull String option2) {
        this.option2 = option2;
    }

    @NonNull
    public String getOption3() {
        return option3;
    }

    public void setOption3(@NonNull String option3) {
        this.option3 = option3;
    }

    @NonNull
    public String getOption4() {
        return option4;
    }

    public void setOption4(@NonNull String option4) {
        this.option4 = option4;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = clampCorrectAnswer(correctAnswer);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}


