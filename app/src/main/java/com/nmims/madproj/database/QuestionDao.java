/*
 * File: QuestionDao.java
 * Purpose: DAO for question CRUD and query operations.
 */

package com.nmims.madproj.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.nmims.madproj.models.Question;

import java.util.List;

@Dao
public interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuestion(Question question);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuestions(List<Question> questions);

    @Query("SELECT * FROM questions ORDER BY id ASC")
    LiveData<List<Question>> getAllQuestions();

    @Query("SELECT * FROM questions WHERE category = :category ORDER BY id ASC")
    LiveData<List<Question>> getQuestionsByCategory(String category);

    @Query("SELECT * FROM questions WHERE difficulty = :difficulty ORDER BY id ASC")
    LiveData<List<Question>> getQuestionsByDifficulty(String difficulty);

    @Query("DELETE FROM questions")
    void deleteAll();

    @Query("DELETE FROM questions WHERE category = :category")
    void deleteByCategory(String category);

    @Query("SELECT COUNT(*) FROM questions")
    int getCount();

    @Query("SELECT COUNT(*) FROM questions WHERE category = :category")
    int getCountByCategory(String category);

    @Query("SELECT DISTINCT category FROM questions WHERE category IS NOT NULL AND category != '' ORDER BY category ASC")
    List<String> getDistinctCategories();
}


