package com.example.teskertievents.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiClient {
    // Base URL of your backend API
    private static final String BASE_URL = "http://10.0.2.2:3000/api/v1/";
    private static Retrofit retrofit = null;
    private static String authToken = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS);

            httpClient.addInterceptor(logging);
            
            // Add auth token if available
            if (authToken != null && !authToken.isEmpty()) {
                httpClient.addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + authToken)
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                });
            }

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    public static void setAuthToken(String token) {
        authToken = token;
        // Reset retrofit instance to rebuild with new token
        retrofit = null;
    }

    public static void clearAuthToken() {
        authToken = null;
        // Reset retrofit instance
        retrofit = null;
    }
}
