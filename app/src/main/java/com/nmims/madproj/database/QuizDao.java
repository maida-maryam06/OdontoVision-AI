/*
 * File: QuizDao.java
 * Purpose: DAO for quiz CRUD operations.
 */

package com.nmims.madproj.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.nmims.madproj.models.Quiz;

import java.util.List;

@Dao
public interface QuizDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertQuiz(Quiz quiz);

    @Query("SELECT * FROM quizzes ORDER BY timestamp DESC")
    LiveData<List<Quiz>> getAllQuizzes();

    @Query("SELECT * FROM quizzes WHERE id = :quizId LIMIT 1")
    LiveData<Quiz> getQuizById(int quizId);

    @Delete
    void deleteQuiz(Quiz quiz);
}


