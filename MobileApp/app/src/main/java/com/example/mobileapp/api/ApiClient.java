package com.example.mobileapp.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Use your server IP address or domain name
    private static final String BASE_URL = "http://192.168.1.12:3000/api/"; // For emulator
    // private static final String BASE_URL = "http://192.168.1.100:3000/api/"; // For real device, replace with your IP
    private static Retrofit retrofit = null;
    private static String authToken = null;
    private static final String TAG = "TeskertiAPI";

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Create logging interceptor
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                Log.d(TAG, message);
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor);

            // Add auth token interceptor
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    // If auth token is available, add it to the header
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Content-Type", "application/json");

                    if (authToken != null) {
                        requestBuilder.header("Authorization", "Bearer " + authToken);
                    }

                    Request request = requestBuilder.build();

                    // Log the request
                    Log.d(TAG, "Request URL: " + request.url());
                    Log.d(TAG, "Request Headers: " + request.headers());

                    Response response = chain.proceed(request);

                    // Log the response
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "Response Error: " + response.code() + " " + response.message());
                        try {
                            ResponseBody responseBody = response.peekBody(Long.MAX_VALUE);
                            Log.e(TAG, "Response Error Body: " + responseBody.string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading response body: " + e.getMessage());
                        }
                    }

                    return response;
                }
            });

            // Create Gson with custom type adapter for boolean values
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Boolean.class, new BooleanTypeAdapter())
                    .registerTypeAdapter(boolean.class, new BooleanTypeAdapter())
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build();

            Log.d(TAG, "Retrofit client created with base URL: " + BASE_URL);
        }
        return retrofit;
    }

    public static void setAuthToken(String token) {
        authToken = token;
        // Rebuild retrofit with new token
        retrofit = null;
        getClient();
        Log.d(TAG, "Auth token set");
    }

    public static void clearAuthToken() {
        authToken = null;
        // Rebuild retrofit without token
        retrofit = null;
        getClient();
        Log.d(TAG, "Auth token cleared");
    }
}
