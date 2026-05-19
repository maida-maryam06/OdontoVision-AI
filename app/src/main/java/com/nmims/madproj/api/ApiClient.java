/*
 * File: ApiClient.java
 * Purpose: Retrofit client configuration with logging and base URL.
 */

package com.nmims.madproj.api;

import androidx.annotation.NonNull;

import com.nmims.madproj.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static volatile Retrofit INSTANCE;

    public static Retrofit getInstance() {
        if (INSTANCE == null) {
            synchronized (ApiClient.class) {
                if (INSTANCE == null) {
                    try {
                        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                        OkHttpClient client = new OkHttpClient.Builder()
                                .addInterceptor(logging)
                                .addInterceptor(new Interceptor() {
                                    @NonNull
                                    @Override
                                    public Response intercept(@NonNull Chain chain) throws java.io.IOException {
                                        Request original = chain.request();
                                        Request.Builder builder = original.newBuilder();
                                        // Optional: API key header
                                        String apiKey = Constants.GEMINI_API_KEY;
                                        if (apiKey != null && !apiKey.isEmpty()) {
                                            builder.addHeader("Authorization", "Bearer " + apiKey);
                                        }
                                        return chain.proceed(builder.build());
                                    }
                                })
                                .connectTimeout(30, TimeUnit.SECONDS)
                                .readTimeout(30, TimeUnit.SECONDS)
                                .build();

                        String base = Constants.BASE_API_URL;
                        if (base == null || base.trim().isEmpty()) {
                            base = "https://dummy.invalid/";
                        } else if (!base.endsWith("/")) {
                            base = base + "/";
                        }

                        INSTANCE = new Retrofit.Builder()
                                .baseUrl(base)
                                .client(client)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                    } catch (Exception e) {
                        // Absolute fallback to prevent crashes
                        INSTANCE = new Retrofit.Builder()
                                .baseUrl("https://dummy.invalid/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                    }
                }
            }
        }
        return INSTANCE;
    }
}


