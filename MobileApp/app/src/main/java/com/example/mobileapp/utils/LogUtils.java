package com.example.mobileapp.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public class LogUtils {
    private static final String TAG = "TeskertiAPI";
    private static final boolean DEBUG = true;

    public static void logResponse(String response) {
        if (DEBUG) {
            try {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String prettyJson = gson.toJson(JsonParser.parseString(response));

                // Split the output by newline to avoid log truncation
                String[] lines = prettyJson.split("\n");
                Log.d(TAG, "API Response:");
                for (String line : lines) {
                    Log.d(TAG, line);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                Log.d(TAG, "Raw response: " + response);
            }
        }
    }

    public static void logError(String message, Throwable t) {
        if (DEBUG) {
            Log.e(TAG, message + ": " + t.getMessage());
            t.printStackTrace();
        }
    }

    public static void logRequest(String endpoint, Object request) {
        if (DEBUG) {
            try {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String prettyJson = gson.toJson(request);

                Log.d(TAG, "API Request to " + endpoint + ":");
                String[] lines = prettyJson.split("\n");
                for (String line : lines) {
                    Log.d(TAG, line);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error logging request: " + e.getMessage());
            }
        }
    }

    public static void debug(String message) {
        if (DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static void error(String message) {
        if (DEBUG) {
            Log.e(TAG, message);
        }
    }

    public static void info(String message) {
        if (DEBUG) {
            Log.i(TAG, message);
        }
    }

    public static void d(String tag, String message) {
        if (DEBUG) {
            Log.d(TAG + ":" + tag, message);
        }
    }
}
