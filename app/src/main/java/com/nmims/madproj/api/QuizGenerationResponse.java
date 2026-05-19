/*
 * File: QuizGenerationResponse.java
 * Purpose: Response model for AI quiz generation, containing questions and status.
 */

package com.nmims.madproj.api;

import com.nmims.madproj.models.Question;

import java.util.List;

public class QuizGenerationResponse {
    private List<Question> questions;
    private String status;
    private String message;

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


