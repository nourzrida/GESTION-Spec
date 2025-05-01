package com.example.mobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.adapters.ScheduleAdapter;
import com.example.mobileapp.api.ApiClient;
import com.example.mobileapp.api.ApiService;
import com.example.mobileapp.models.Event;
import com.example.mobileapp.models.EventSchedule;
import com.example.mobileapp.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventScheduleActivity extends AppCompatActivity implements ScheduleAdapter.OnScheduleClickListener {
    private static final String TAG = "EventScheduleActivity";

    private Event event;
    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private List<EventSchedule> scheduleList;
    private ProgressBar progressBar;
    private TextView noSchedulesText;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_schedule);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get event from intent
        event = (Event) getIntent().getSerializableExtra("event");
        if (event == null) {
            Toast.makeText(this, "Error: Event information not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getSupportActionBar().setTitle(event.getTitle() + " - Schedules");

        // Initialize views
        recyclerView = findViewById(R.id.schedulesRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        noSchedulesText = findViewById(R.id.noSchedulesText);

        // Setup RecyclerView
        scheduleList = new ArrayList<>();
        adapter = new ScheduleAdapter(this, scheduleList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Load schedules
        loadEventSchedules();
    }

    private void loadEventSchedules() {
        progressBar.setVisibility(View.VISIBLE);
        noSchedulesText.setVisibility(View.GONE);

        apiService.getEventSchedules(event.getId()).enqueue(new Callback<List<EventSchedule>>() {
            @Override
            public void onResponse(Call<List<EventSchedule>> call, Response<List<EventSchedule>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    scheduleList.clear();
                    scheduleList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (scheduleList.isEmpty()) {
                        noSchedulesText.setVisibility(View.VISIBLE);
                        // Create mock schedules for demonstration
                        createMockSchedules();
                    }
                } else {
                    LogUtils.error(TAG);
                    Toast.makeText(EventScheduleActivity.this, "Failed to load schedules", Toast.LENGTH_SHORT).show();
                    // Create mock schedules for demonstration
                    createMockSchedules();
                }
            }

            @Override
            public void onFailure(Call<List<EventSchedule>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                LogUtils.error(TAG);
                Toast.makeText(EventScheduleActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Create mock schedules for demonstration
                createMockSchedules();
            }
        });
    }

    private void createMockSchedules() {
        scheduleList.clear();

        // Create mock schedules for different venues
        String[] venues = {
                "Amphithéâtre de Carthage",
                "Théâtre Municipal de Tunis",
                "Centre Culturel de Hammamet",
                "Palais Ennejma Ezzahra"
        };

        String[] dates = {
                "2023-07-15",
                "2023-07-22",
                "2023-07-29",
                "2023-08-05"
        };

        String[] startTimes = {
                "19:00",
                "20:00",
                "18:30",
                "21:00"
        };

        String[] endTimes = {
                "22:00",
                "23:00",
                "21:30",
                "23:30"
        };

        int[] availableSeats = {
                750, 200, 350, 0
        };

        int[] totalSeats = {
                1000, 500, 400, 800
        };

        boolean[] soldOut = {
                false, false, false, true
        };

        for (int i = 0; i < venues.length; i++) {
            EventSchedule schedule = new EventSchedule();
            schedule.setId(i + 1);
            schedule.setEventId(event.getId());
            schedule.setVenueId(i + 1);
            schedule.setVenue(venues[i]);
            schedule.setDate(dates[i]);
            schedule.setStartTime(startTimes[i]);
            schedule.setEndTime(endTimes[i]);
            schedule.setAvailableSeats(availableSeats[i]);
            schedule.setTotalSeats(totalSeats[i]);
            schedule.setSoldOut(soldOut[i]);

            scheduleList.add(schedule);
        }

        adapter.notifyDataSetChanged();

        if (scheduleList.isEmpty()) {
            noSchedulesText.setVisibility(View.VISIBLE);
        } else {
            noSchedulesText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onScheduleClick(EventSchedule schedule) {
        if (schedule.isSoldOut()) {
            Toast.makeText(this, "This schedule is sold out", Toast.LENGTH_SHORT).show();
            return;
        }

        // Navigate to ticket selection
        Intent intent = new Intent(this, TicketSelectionActivity.class);
        intent.putExtra("event", event);
        intent.putExtra("schedule", schedule);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
