/*
 * File: ChatbotActivity.java
 * Purpose: Simple chat UI that can be wired to Gemini via ApiRepository when configured.
 */

package com.nmims.madproj.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.nmims.madproj.R;
import com.nmims.madproj.utils.UiUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class ChatbotActivity extends AppCompatActivity {

    private LinearLayout messagesContainer;
    private EditText inputMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        UiUtil.applyAmbientBackground(this);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.topBar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        messagesContainer = findViewById(R.id.messagesContainer);
        inputMessage = findViewById(R.id.inputMessage);
        MaterialButton btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(v -> sendMessage());

        addMessage("assistant", "Hi! I am your OdontoPath AI Tutor. Ask me anything about odontogenic cysts, odontogenic tumors, oral pathology notes, or quizzes.");
    }

    private void sendMessage() {
        String prompt = inputMessage.getText() != null ? inputMessage.getText().toString().trim() : "";
        if (TextUtils.isEmpty(prompt)) return;
        addMessage("you", prompt);
        inputMessage.setText("");

        String oralPathologyPrompt =
                "You are OdontoPath AI Tutor for dental students. " +
                        "Answer only in simple oral pathology language. " +
                        "Keep the answer short and mobile-friendly. " +
                        "Do not use markdown symbols like ###, **, *, or bullet stars. " +
                        "Use plain text only. " +
                        "Give answer in this format:\n" +
                        "1. Definition\n" +
                        "2. Key features\n" +
                        "3. Diagnosis\n" +
                        "4. Treatment\n" +
                        "5. One exam point\n\n" +
                        "Question: " + prompt;

        // Show loading
        TextView loadingMsg = addMessage("assistant", "Thinking...");
        
        // Call Gemini API on background thread
        new Thread(() -> {
            try {
                android.util.Log.d("Chatbot", "Sending request to Gemini API...");
                String response = callGeminiAPI(oralPathologyPrompt);
                android.util.Log.d("Chatbot", "Got response: " + (response != null ? response.substring(0, Math.min(100, response.length())) : "null"));
                runOnUiThread(() -> {
                    messagesContainer.removeView(loadingMsg);
                    if (response != null && !response.isEmpty() && !response.startsWith("Error:")) {
                        addMessage("assistant", response);
                    } else {
                        addMessage("assistant", response != null ? response : "Sorry, I couldn't process that. Please try again.");
                    }
                });
            } catch (Exception e) {
                android.util.Log.e("Chatbot", "Error in sendMessage", e);
                runOnUiThread(() -> {
                    messagesContainer.removeView(loadingMsg);
                    addMessage("assistant", "Error: " + e.getMessage());
                });
            }
        }).start();
    }

    private String callGeminiAPI(String prompt) {
        try {
            String apiKey = com.nmims.madproj.utils.Constants.GEMINI_API_KEY;
            if (apiKey == null || apiKey.isEmpty() || apiKey.equals("YOUR_API_KEY")) {
                return "Please configure GEMINI_API_KEY in Constants.java";
            }

            // First, get available models
            String availableModel = getAvailableModel(apiKey);
            if (availableModel == null || availableModel.isEmpty()) {
                return "Could not find an available Gemini model. Please check your API key.";
            }

            // Use the available model
            String urlString = "https://generativelanguage.googleapis.com/v1beta/" + availableModel + ":generateContent?key=" + apiKey;
            android.util.Log.d("Chatbot", "Using model: " + availableModel);
            URL url = new URL(urlString);
            
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // Build request body
            JSONObject requestBody = new JSONObject();
            JSONObject part = new JSONObject();
            part.put("text", prompt);
            JSONObject contentObj = new JSONObject();
            contentObj.put("parts", new org.json.JSONArray().put(part));
            requestBody.put("contents", new org.json.JSONArray().put(contentObj));

            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            BufferedReader in;
            if (responseCode == 200) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            conn.disconnect();

            if (responseCode == 200) {
                JSONObject jsonResponse = new JSONObject(response.toString());
                if (jsonResponse.has("candidates") && jsonResponse.getJSONArray("candidates").length() > 0) {
                    JSONObject candidate = jsonResponse.getJSONArray("candidates").getJSONObject(0);
                    if (candidate.has("content")) {
                        JSONObject responseContent = candidate.getJSONObject("content");
                        if (responseContent.has("parts") && responseContent.getJSONArray("parts").length() > 0) {
                            return responseContent.getJSONArray("parts").getJSONObject(0).getString("text");
                        }
                    }
                }
            }
            
            // Return error message
            try {
                JSONObject errorResponse = new JSONObject(response.toString());
                if (errorResponse.has("error") && errorResponse.getJSONObject("error").has("message")) {
                    return "Error: " + errorResponse.getJSONObject("error").getString("message");
                }
            } catch (Exception ignored) {}
            return "API Error: " + responseCode + " - " + response.toString();
        } catch (java.net.SocketTimeoutException e) {
            return "Error: Request timed out. Check your internet connection.";
        } catch (java.net.UnknownHostException e) {
            return "Error: Cannot reach API. Check your internet connection.";
        } catch (Exception e) {
            return "Error: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
        }
    }

    private String getAvailableModel(String apiKey) {
        try {
            // Call ListModels API
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
                android.util.Log.d("Chatbot", "ListModels response: " + response.toString().substring(0, Math.min(500, response.length())));
                if (jsonResponse.has("models")) {
                    org.json.JSONArray models = jsonResponse.getJSONArray("models");
                    // First, try to find flash model
                    for (int i = 0; i < models.length(); i++) {
                        JSONObject model = models.getJSONObject(i);
                        String name = model.getString("name");
                        if (name.contains("flash")) {
                            android.util.Log.d("Chatbot", "Found model: " + name);
                            return name;
                        }
                    }
                    // If flash not found, try any gemini model
                    for (int i = 0; i < models.length(); i++) {
                        JSONObject model = models.getJSONObject(i);
                        String name = model.getString("name");
                        if (name.contains("gemini") && !name.contains("embedding") && !name.contains("embed")) {
                            android.util.Log.d("Chatbot", "Found model: " + name);
                            return name;
                        }
                    }
                }
            }
            android.util.Log.w("Chatbot", "ListModels returned: " + responseCode);
        } catch (Exception e) {
            android.util.Log.e("Chatbot", "Error getting available models", e);
        }
        
        // Fallback to known working models
        return "models/gemini-1.5-flash";
    }
    private String cleanBotResponse(String text) {
        if (text == null) return "";

        return text
                .replace("###", "")
                .replace("##", "")
                .replace("#", "")
                .replace("**", "")
                .replace("*", "")
                .trim();
    }

    private TextView addMessage(String role, String text) {
        TextView tv = new TextView(this);
        tv.setText(("you".equals(role) ? "You: " : "AI: ") + text);
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setPadding(16, 12, 16, 12);
        tv.setTextSize(16);
        if ("you".equals(role)) {
            tv.setBackgroundColor(0x33000000);
        }
        messagesContainer.addView(tv);
        
        // Scroll to bottom
        messagesContainer.post(() -> {
            android.widget.ScrollView scrollView = (android.widget.ScrollView) messagesContainer.getParent();
            if (scrollView != null) {
                scrollView.fullScroll(android.view.View.FOCUS_DOWN);
            }
        });
        
        return tv;
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}


