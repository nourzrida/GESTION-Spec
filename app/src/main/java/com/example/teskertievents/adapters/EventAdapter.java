package com.example.teskertievents.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.teskertievents.R;
import com.example.teskertievents.models.Event;
import com.example.teskertievents.utils.DateUtils;

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
        
        // Load image with Glide
        Glide.with(context)
                .load(event.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.eventImage);
        
        // Show featured badge if event is featured
        if (event.isFeatured()) {
            holder.featuredBadge.setVisibility(View.VISIBLE);
        } else {
            holder.featuredBadge.setVisibility(View.GONE);
        }
        
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
        ImageView eventImage;
        TextView titleText, dateText, venueText, priceText;
        View featuredBadge, soldOutBadge;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            titleText = itemView.findViewById(R.id.titleText);
            dateText = itemView.findViewById(R.id.dateText);
            venueText = itemView.findViewById(R.id.venueText);
            priceText = itemView.findViewById(R.id.priceText);
            featuredBadge = itemView.findViewById(R.id.featuredBadge);
            soldOutBadge = itemView.findViewById(R.id.soldOutBadge);
        }
    }
}
