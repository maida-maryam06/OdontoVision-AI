/*
 * File: QuizDatabase.java
 * Purpose: RoomDatabase singleton including DAOs for quiz app.
 */

package com.nmims.madproj.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.nmims.madproj.models.Question;
import com.nmims.madproj.models.Quiz;
import com.nmims.madproj.models.QuizResult;
import com.nmims.madproj.models.UploadedNote;

/**
 * App database using Room. Holds questions, quizzes, results, and uploaded notes.
 */
@Database(entities = {Question.class, Quiz.class, QuizResult.class, UploadedNote.class}, version = 1, exportSchema = false)
public abstract class QuizDatabase extends RoomDatabase {

    private static volatile QuizDatabase INSTANCE;
    private static final String DB_NAME = "quizgen_db";

    public abstract QuestionDao questionDao();
    public abstract QuizDao quizDao();
    public abstract QuizResultDao quizResultDao();

    /**
     * Thread-safe singleton accessor.
     */
    public static QuizDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (QuizDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    QuizDatabase.class,
                                    DB_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}


