package com.example.teskertievents.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    private static final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.getDefault());
    
    public static String formatDate(String apiDateString) {
        try {
            Date date = API_DATE_FORMAT.parse(apiDateString);
            return DISPLAY_DATE_FORMAT.format(date);
        } catch (ParseException e) {
            // Try alternative format
            try {
                SimpleDateFormat altFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = altFormat.parse(apiDateString);
                return DISPLAY_DATE_FORMAT.format(date);
            } catch (ParseException ex) {
                return apiDateString; // Return original if parsing fails
            }
        }
    }
}
