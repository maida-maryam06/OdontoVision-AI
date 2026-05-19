/*
 * File: QuizApiService.java
 * Purpose: Retrofit interface for AI quiz generation endpoints.
 */

package com.nmims.madproj.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface QuizApiService {

    @Headers({
            "Content-Type: application/json"
    })
    @POST("v1/quiz/generate")
    Call<QuizGenerationResponse> generateQuiz(@Body QuizGenerationRequest request);
}


