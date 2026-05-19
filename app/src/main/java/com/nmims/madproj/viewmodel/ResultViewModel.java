/*
 * File: ResultViewModel.java
 * Purpose: Handles saving quiz results and exposing analytics metrics.
 */

package com.nmims.madproj.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nmims.madproj.database.QuizDatabase;
import com.nmims.madproj.database.QuizResultDao;
import com.nmims.madproj.models.QuizResult;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResultViewModel extends AndroidViewModel {

    private final QuizResultDao resultDao;
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<String> error = new MutableLiveData<>(null);

    public ResultViewModel(@NonNull Application application) {
        super(application);
        resultDao = QuizDatabase.getInstance(application).quizResultDao();
    }

    public LiveData<String> getError() { return error; }

    public LiveData<List<QuizResult>> getResults() {
        return resultDao.getAllResults();
    }

    public LiveData<Double> getAverageScore() {
        return resultDao.getAverageScore();
    }

    public void saveResult(QuizResult result) {
        if (result == null) {
            error.setValue("Invalid result");
            return;
        }
        ioExecutor.execute(() -> resultDao.insertResult(result));
    }
}


