/*
 * File: ApiRepository.java
 * Purpose: Repository to handle AI quiz generation API interactions using Gemini API.
 */

package com.nmims.madproj.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.nmims.madproj.api.QuizGenerationResponse;
import com.nmims.madproj.models.Question;
import com.nmims.madproj.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Bridges ViewModels to the AI quiz generation API using Gemini.
 */
public class ApiRepository {

    public interface ApiCallback {
        void onSuccess(QuizGenerationResponse response);
        void onError(String errorMessage);
    }

    private static final String TAG = "ApiRepository";
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public ApiRepository() {
    }

    /**
     * Generate a quiz from free-form text using Gemini API.
     */
    public void generateQuizFromText(String text, int numQuestions, String difficulty, @NonNull ApiCallback callback) {
        EXECUTOR.execute(() -> {
            try {
                String apiKey = Constants.GEMINI_API_KEY;

                if (apiKey == null ||
                        apiKey.trim().isEmpty() ||
                        apiKey.equals("YOUR_API_KEY") ||
                        apiKey.equals("paste_your_api") ||
                        apiKey.equals("your-actual-api-key-here")) {

                    callback.onError("GEMINI_API_KEY not configured in Constants.java");
                    return;
                }

                if (text == null || text.trim().isEmpty()) {
                    callback.onError("No text found. Please upload a clear PDF/image or extract text first.");
                    return;
                }

                int safeNumQuestions = numQuestions;
                if (safeNumQuestions <= 0) {
                    safeNumQuestions = 10;
                }

                String safeDifficulty = difficulty;
                if (safeDifficulty == null || safeDifficulty.trim().isEmpty()) {
                    safeDifficulty = "Medium";
                }

                String model = getAvailableGeminiModel(apiKey);

                if (model == null || model.trim().isEmpty()) {
                    callback.onError("Could not find an available Gemini model. Please check your API key.");
                    return;
                }

                String prompt = createQuizPrompt(text, safeNumQuestions, safeDifficulty);

                String response = callGeminiAPI(apiKey, model, prompt);

                List<Question> questions = parseQuizResponse(response, safeDifficulty);

                if (questions.isEmpty()) {
                    String shortResponse = response != null
                            ? response.substring(0, Math.min(300, response.length()))
                            : "No response";

                    callback.onError("Failed to generate questions. Gemini response: " + shortResponse);
                } else {
                    QuizGenerationResponse quizResponse = new QuizGenerationResponse();
                    quizResponse.setQuestions(questions);
                    callback.onSuccess(quizResponse);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error generating quiz", e);
                callback.onError("Error: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
            }
        });
    }

    private String getAvailableGeminiModel(String apiKey) {
        try {
            URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models?key=" + apiKey);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();

                String line;

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }

                in.close();
                conn.disconnect();

                JSONObject jsonResponse = new JSONObject(response.toString());

                if (jsonResponse.has("models")) {
                    JSONArray models = jsonResponse.getJSONArray("models");

                    for (int i = 0; i < models.length(); i++) {
                        JSONObject model = models.getJSONObject(i);
                        String name = model.optString("name", "");

                        if (name.contains("gemini") &&
                                name.contains("flash") &&
                                !name.contains("embedding") &&
                                !name.contains("embed")) {

                            Log.d(TAG, "Selected Gemini model: " + name);
                            return name;
                        }
                    }

                    for (int i = 0; i < models.length(); i++) {
                        JSONObject model = models.getJSONObject(i);
                        String name = model.optString("name", "");

                        if (name.contains("gemini") &&
                                !name.contains("embedding") &&
                                !name.contains("embed")) {

                            Log.d(TAG, "Selected Gemini model: " + name);
                            return name;
                        }
                    }
                }

            } else {
                Log.w(TAG, "List models failed with response code: " + responseCode);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting available Gemini models", e);
        }

        return "models/gemini-1.5-flash";
    }

    private String createQuizPrompt(String text, int numQuestions, String difficulty) {
        return "You are an oral pathology tutor for dental students. " +
                "Your task is to generate high-quality MCQs related to odontogenic cysts, odontogenic tumors, and oral pathology. " +
                "Use simple, exam-oriented language suitable for dental students. " +
                "Focus on definition, etiology, clinical features, radiographic features, histopathology, diagnosis, differential diagnosis, treatment, and MCQs. " +
                "Do not generate Java, Android, DSA, computer science, programming, or unrelated questions. " +
                "Generate exactly " + numQuestions + " multiple choice quiz questions based on the following text. " +
                "Difficulty level: " + difficulty + ". " +
                "For each question, provide:\n" +
                "1. Question text\n" +
                "2. 4 options: option1, option2, option3, option4\n" +
                "3. Correct answer as a number only: 1, 2, 3, or 4\n" +
                "4. Brief explanation\n\n" +
                "Return ONLY a JSON array in this exact format:\n" +
                "[{\"question\":\"...\",\"option1\":\"...\",\"option2\":\"...\",\"option3\":\"...\",\"option4\":\"...\",\"correct\":1,\"explanation\":\"...\"}]\n\n" +
                "Text to analyze:\n" + text;
    }

    private String callGeminiAPI(String apiKey, String model, String prompt) throws Exception {
        String urlString = "https://generativelanguage.googleapis.com/v1beta/" + model + ":generateContent?key=" + apiKey;

        URL url = new URL(urlString);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);
        conn.setDoOutput(true);
        conn.setDoInput(true);

        JSONObject requestBody = new JSONObject();

        JSONObject part = new JSONObject();
        part.put("text", prompt);

        JSONArray parts = new JSONArray();
        parts.put(part);

        JSONObject content = new JSONObject();
        content.put("parts", parts);

        JSONArray contents = new JSONArray();
        contents.put(content);

        requestBody.put("contents", contents);

        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(requestBody.toString());
        writer.flush();
        writer.close();

        int responseCode = conn.getResponseCode();

        BufferedReader reader;

        if (responseCode == 200) {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder response = new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        conn.disconnect();

        if (responseCode == 200) {
            return response.toString();
        } else {
            throw new Exception("API returned " + responseCode + ": " + response.toString());
        }
    }

    private List<Question> parseQuizResponse(String response, String defaultDifficulty) {
        List<Question> questions = new ArrayList<>();

        try {
            JSONObject jsonResponse = new JSONObject(response);

            if (!jsonResponse.has("candidates")) {
                return questions;
            }

            JSONArray candidates = jsonResponse.getJSONArray("candidates");

            if (candidates.length() == 0) {
                return questions;
            }

            JSONObject candidate = candidates.getJSONObject(0);

            if (!candidate.has("content")) {
                return questions;
            }

            JSONObject content = candidate.getJSONObject("content");

            if (!content.has("parts")) {
                return questions;
            }

            JSONArray parts = content.getJSONArray("parts");

            if (parts.length() == 0) {
                return questions;
            }

            JSONObject firstPart = parts.getJSONObject(0);

            if (!firstPart.has("text")) {
                return questions;
            }

            String text = firstPart.getString("text");

            text = text.replace("```json", "")
                    .replace("```", "")
                    .trim();

            int jsonStart = text.indexOf("[");
            int jsonEnd = text.lastIndexOf("]");

            if (jsonStart < 0 || jsonEnd <= jsonStart) {
                return questions;
            }

            String jsonArrayText = text.substring(jsonStart, jsonEnd + 1);

            JSONArray questionsArray = new JSONArray(jsonArrayText);

            for (int i = 0; i < questionsArray.length(); i++) {
                JSONObject q = questionsArray.getJSONObject(i);

                String questionText = q.optString("question", "");
                String option1 = q.optString("option1", q.optString("A", ""));
                String option2 = q.optString("option2", q.optString("B", ""));
                String option3 = q.optString("option3", q.optString("C", ""));
                String option4 = q.optString("option4", q.optString("D", ""));

                int correct = q.optInt("correct", q.optInt("correctAnswer", 1));
                String explanation = q.optString("explanation", "");

                if (questionText.trim().isEmpty() ||
                        option1.trim().isEmpty() ||
                        option2.trim().isEmpty() ||
                        option3.trim().isEmpty() ||
                        option4.trim().isEmpty()) {
                    continue;
                }

                correct = Math.max(1, Math.min(4, correct));

                Question question = new Question(
                        questionText,
                        option1,
                        option2,
                        option3,
                        option4,
                        correct,
                        "OdontoPath Generated",
                        defaultDifficulty,
                        explanation
                );

                questions.add(question);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error parsing quiz response", e);
        }

        return questions;
    }
}