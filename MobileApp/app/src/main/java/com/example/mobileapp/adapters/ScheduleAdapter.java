package com.example.mobileapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.models.EventSchedule;
import com.example.mobileapp.utils.DateUtils;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
    private Context context;
    private List<EventSchedule> scheduleList;
    private OnScheduleClickListener listener;

    public interface OnScheduleClickListener {
        void onScheduleClick(EventSchedule schedule);
    }

    public ScheduleAdapter(Context context, List<EventSchedule> scheduleList, OnScheduleClickListener listener) {
        this.context = context;
        this.scheduleList = scheduleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        EventSchedule schedule = scheduleList.get(position);

        holder.venueText.setText(schedule.getVenue());
        holder.dateText.setText(DateUtils.formatDate(schedule.getDate()));
        holder.timeText.setText(schedule.getFormattedTime());

        // Set availability info
        int availabilityPercentage = schedule.getAvailabilityPercentage();
        holder.availabilityProgress.setProgress(availabilityPercentage);
        holder.availabilityText.setText(schedule.getAvailableSeats() + " / " + schedule.getTotalSeats() + " seats available");

        // Show sold out badge if event is sold out
        if (schedule.isSoldOut()) {
            holder.soldOutText.setVisibility(View.VISIBLE);
            holder.availabilityProgress.setVisibility(View.GONE);
        } else {
            holder.soldOutText.setVisibility(View.GONE);
            holder.availabilityProgress.setVisibility(View.VISIBLE);
        }

        // Set click listener
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onScheduleClick(schedule);
            }
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView venueText, dateText, timeText, availabilityText, soldOutText;
        ProgressBar availabilityProgress;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.scheduleCardView);
            venueText = itemView.findViewById(R.id.venueText);
            dateText = itemView.findViewById(R.id.dateText);
            timeText = itemView.findViewById(R.id.timeText);
            availabilityText = itemView.findViewById(R.id.availabilityText);
            soldOutText = itemView.findViewById(R.id.soldOutText);
            availabilityProgress = itemView.findViewById(R.id.availabilityProgress);
        }
    }
}
