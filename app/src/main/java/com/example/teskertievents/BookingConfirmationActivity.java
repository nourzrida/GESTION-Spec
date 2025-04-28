package com.example.teskertievents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.teskertievents.models.Event;
import com.example.teskertievents.utils.DateUtils;

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

        if (bookingReference == null || qrCodeData == null || event == null) {
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
            // QR code data is a base64 encoded string
            String base64Image = qrCodeData.split(",")[1];
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            qrCodeImage.setImageBitmap(decodedBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
