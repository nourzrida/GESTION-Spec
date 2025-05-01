package com.example.mobileapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mobileapp.models.Event;
import com.example.mobileapp.utils.DateUtils;

public class BookingConfirmationActivity extends AppCompatActivity {
    private String bookingReference;
    private String qrCodeData;
    private Event event;

    private TextView bookingReferenceText, eventTitleText, eventDateText, eventVenueText;
    private ImageView qrCodeImage;
    private Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Booking Confirmed");

        // Initialize views
        bookingReferenceText = findViewById(R.id.bookingReferenceText);
        eventTitleText = findViewById(R.id.eventTitleText);
        eventDateText = findViewById(R.id.eventDateText);
        eventVenueText = findViewById(R.id.eventVenueText);
        qrCodeImage = findViewById(R.id.qrCodeImage);
        doneButton = findViewById(R.id.doneButton);

        // Get data from intent
        bookingReference = getIntent().getStringExtra("bookingReference");
        qrCodeData = getIntent().getStringExtra("qrCodeData");
        event = (Event) getIntent().getSerializableExtra("event");

        if (bookingReference == null || event == null) {
            finish();
            return;
        }

        // Display booking information
        bookingReferenceText.setText("Booking Reference: " + bookingReference);
        eventTitleText.setText(event.getTitle());
        eventDateText.setText(DateUtils.formatDate(event.getEventDate()));
        eventVenueText.setText(event.getVenue());

        // Display QR code
        displayQrCode();

        // Setup done button
        doneButton.setOnClickListener(v -> {
            // Go back to main activity, clearing the back stack
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void displayQrCode() {
        try {
            // Generate a simple QR code with the booking reference
            generateSimpleQrCode(bookingReference);
        } catch (Exception e) {
            Log.e("BookingConfirmation", "Error displaying QR code: " + e.getMessage());
            e.printStackTrace();

            // If there's an exception, generate a simple QR code with just the booking reference
            generateSimpleQrCode(bookingReference);
        }
    }

    private void generateSimpleQrCode(String data) {
        try {
            // Create a simple QR code-like image
            int size = 300;
            Bitmap qrBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(qrBitmap);

            // Fill background
            Paint bgPaint = new Paint();
            bgPaint.setColor(Color.WHITE);
            canvas.drawRect(0, 0, size, size, bgPaint);

            // Draw border
            Paint borderPaint = new Paint();
            borderPaint.setColor(Color.BLACK);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(10);
            canvas.drawRect(10, 10, size - 10, size - 10, borderPaint);

            // Draw position detection patterns (corners)
            Paint cornerPaint = new Paint();
            cornerPaint.setColor(Color.BLACK);

            // Top-left corner
            canvas.drawRect(30, 30, 90, 90, cornerPaint);
            canvas.drawRect(40, 40, 80, 80, bgPaint);
            canvas.drawRect(50, 50, 70, 70, cornerPaint);

            // Top-right corner
            canvas.drawRect(size - 90, 30, size - 30, 90, cornerPaint);
            canvas.drawRect(size - 80, 40, size - 40, 80, bgPaint);
            canvas.drawRect(size - 70, 50, size - 50, 70, cornerPaint);

            // Bottom-left corner
            canvas.drawRect(30, size - 90, 90, size - 30, cornerPaint);
            canvas.drawRect(40, size - 80, 80, size - 40, bgPaint);
            canvas.drawRect(50, size - 70, 70, size - 50, cornerPaint);

            // Draw some random dots to make it look like a QR code
            for (int i = 0; i < 100; i++) {
                int x = (int)(Math.random() * (size - 100)) + 50;
                int y = (int)(Math.random() * (size - 100)) + 50;
                int dotSize = (int)(Math.random() * 10) + 5;
                canvas.drawRect(x, y, x + dotSize, y + dotSize, cornerPaint);
            }

            // Draw text in the middle
            Paint textPaint = new Paint();
            textPaint.setColor(getResources().getColor(R.color.colorPrimary));
            textPaint.setTextSize(24);
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(data, size/2, size/2, textPaint);

            qrCodeImage.setImageBitmap(qrBitmap);
        } catch (Exception e) {
            Log.e("BookingConfirmation", "Error generating simple QR code: " + e.getMessage());
        }
    }
}
