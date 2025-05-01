package com.example.mobileapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mobileapp.R;
import com.example.mobileapp.models.Event;
import com.example.mobileapp.utils.DateUtils;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private Context context;
    private List<Event> eventList;
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public EventAdapter(Context context, List<Event> eventList, OnEventClickListener listener) {
        this.context = context;
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.titleText.setText(event.getTitle());
        holder.dateText.setText(DateUtils.formatDate(event.getEventDate()));
        holder.venueText.setText(event.getVenue());
        holder.priceText.setText(String.format("%.2f TND", event.getPrice()));
        holder.categoryText.setText(event.getCategory());

        // Load image with Glide
        Glide.with(context)
                .load(event.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.eventImage);

        // Show sold out badge if event is sold out
        if (event.isSoldOut()) {
            holder.soldOutBadge.setVisibility(View.VISIBLE);
        } else {
            holder.soldOutBadge.setVisibility(View.GONE);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEventClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage, soldOutBadge;
        TextView titleText, dateText, venueText, priceText, categoryText;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            soldOutBadge = itemView.findViewById(R.id.soldOutBadge);
            titleText = itemView.findViewById(R.id.titleText);
            dateText = itemView.findViewById(R.id.dateText);
            venueText = itemView.findViewById(R.id.venueText);
            priceText = itemView.findViewById(R.id.priceText);
            categoryText = itemView.findViewById(R.id.categoryText);
        }
    }
}
