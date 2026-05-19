/*
 * File: QuizResultDao.java
 * Purpose: DAO for quiz results analytics and retrieval.
 */

package com.nmims.madproj.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.nmims.madproj.models.QuizResult;

import java.util.List;

@Dao
public interface QuizResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertResult(QuizResult result);

    @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC")
    LiveData<List<QuizResult>> getAllResults();

    @Query("SELECT * FROM quiz_results WHERE quizId = :quizId ORDER BY timestamp DESC")
    LiveData<List<QuizResult>> getResultsByQuiz(int quizId);

    @Query("SELECT AVG(score) FROM quiz_results")
    LiveData<Double> getAverageScore();
}


