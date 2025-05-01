package com.example.mobileapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat API_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    private static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.getDefault());
    private static final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static String formatDate(String apiDateString) {
        if (apiDateString == null || apiDateString.isEmpty()) {
            return "";
        }

        try {
            Date date = API_FORMAT.parse(apiDateString);
            return date != null ? DISPLAY_FORMAT.format(date) : "";
        } catch (ParseException e) {
            e.printStackTrace();
            return apiDateString;
        }
    }

    public static String formatDateOnly(String apiDateString) {
        if (apiDateString == null || apiDateString.isEmpty()) {
            return "";
        }

        try {
            Date date = API_FORMAT.parse(apiDateString);
            return date != null ? DATE_ONLY_FORMAT.format(date) : "";
        } catch (ParseException e) {
            e.printStackTrace();
            return apiDateString;
        }
    }

    public static String formatDateForApi(Date date) {
        return date != null ? DATE_ONLY_FORMAT.format(date) : "";
    }
}
