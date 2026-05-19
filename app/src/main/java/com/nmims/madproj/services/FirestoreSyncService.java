/*
 * File: FirestoreSyncService.java
 * Purpose: Sync approved questions from Firestore to local Room database.
 */

package com.nmims.madproj.services;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nmims.madproj.models.Question;
import com.nmims.madproj.repository.QuizRepository;

import java.util.ArrayList;
import java.util.List;

public class FirestoreSyncService {

    private static final String TAG = "FirestoreSync";
    private final FirebaseFirestore db;
    private final QuizRepository quizRepository;

    public FirestoreSyncService(Application application) {
        this.db = FirebaseFirestore.getInstance();
        this.quizRepository = new QuizRepository(application);
    }

    public void syncApprovedQuestions() {
        db.collection("questions")
                .whereEqualTo("approved", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Question> questions = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            try {
                                Question q = new Question(
                                        doc.getString("questionText"),
                                        doc.getString("option1"),
                                        doc.getString("option2"),
                                        doc.getString("option3"),
                                        doc.getString("option4"),
                                        doc.getLong("correctAnswer") != null ? doc.getLong("correctAnswer").intValue() : 1,
                                        doc.getString("category") != null ? doc.getString("category") : "General",
                                        doc.getString("difficulty") != null ? doc.getString("difficulty") : "Medium",
                                        doc.getString("explanation") != null ? doc.getString("explanation") : ""
                                );
                                questions.add(q);
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing question: " + e.getMessage());
                            }
                        }
                        if (!questions.isEmpty()) {
                            quizRepository.insertQuestions(questions);
                            Log.d(TAG, "Synced " + questions.size() + " questions from Firestore");
                        }
                    } else {
                        Log.e(TAG, "Sync failed", task.getException());
                    }
                });
    }

    public void listenForNewApprovedQuestions() {
        db.collection("questions")
                .whereEqualTo("approved", true)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Listen error", e);
                        return;
                    }
                    if (snapshot != null && !snapshot.isEmpty()) {
                        syncApprovedQuestions();
                    }
                });
    }
}
