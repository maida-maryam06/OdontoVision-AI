/*
 * File: QuizGenerationRequest.java
 * Purpose: Request payload for AI quiz generation.
 */

package com.nmims.madproj.api;

/**
 * Encapsulates text input and generation parameters.
 */
public class QuizGenerationRequest {
    private String noteText;
    private int numQuestions;
    private String difficulty;

    public QuizGenerationRequest(String noteText, int numQuestions, String difficulty) {
        this.noteText = noteText;
        this.numQuestions = Math.max(1, numQuestions);
        this.difficulty = difficulty;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public int getNumQuestions() {
        return numQuestions;
    }

    public void setNumQuestions(int numQuestions) {
        this.numQuestions = Math.max(1, numQuestions);
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}


