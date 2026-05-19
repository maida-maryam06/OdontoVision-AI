/*
 * File: UploadViewModel.java
 * Purpose: Manages note image processing, OCR extraction, and quiz generation.
 */

package com.nmims.madproj.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nmims.madproj.api.QuizGenerationResponse;
import com.nmims.madproj.models.Question;
import com.nmims.madproj.repository.ApiRepository;
import com.nmims.madproj.utils.TextExtractor;

import java.util.List;

public class UploadViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> processing = new MutableLiveData<>(false);
    private final MutableLiveData<String> extractedText = new MutableLiveData<>("");
    private final MutableLiveData<List<Question>> generatedQuestions = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>(null);

    private final ApiRepository apiRepository;

    public UploadViewModel(@NonNull Application application) {
        super(application);
        this.apiRepository = new ApiRepository();
    }

    public LiveData<Boolean> getProcessing() { return processing; }
    public LiveData<String> getExtractedText() { return extractedText; }
    public LiveData<List<Question>> getGeneratedQuestions() { return generatedQuestions; }
    public LiveData<String> getError() { return error; }

    public void processImage(Uri imageUri) {
        processFile(imageUri);
    }

    public void processFile(Uri fileUri) {
        processing.setValue(true);
        TextExtractor.extractTextFromFile(fileUri, getApplication(), new TextExtractor.TextCallback() {
            @Override
            public void onSuccess(String text) {
                extractedText.postValue(text);
                processing.postValue(false);
            }

            @Override
            public void onError(String errorMessage) {
                error.postValue(errorMessage);
                processing.postValue(false);
            }
        });
    }

    public void generateQuiz(int numQuestions, String difficulty) {
        String text = extractedText.getValue();
        if (text == null || text.trim().isEmpty()) {
            error.setValue("No text extracted to generate quiz");
            return;
        }
        processing.setValue(true);
        apiRepository.generateQuizFromText(text, numQuestions, difficulty, new ApiRepository.ApiCallback() {
            @Override
            public void onSuccess(QuizGenerationResponse response) {
                generatedQuestions.postValue(response.getQuestions());
                processing.postValue(false);
            }

            @Override
            public void onError(String errorMessage) {
                error.postValue(errorMessage);
                processing.postValue(false);
            }
        });
    }
}


