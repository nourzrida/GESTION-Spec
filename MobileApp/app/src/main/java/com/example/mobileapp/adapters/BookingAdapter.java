package com.example.mobileapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.models.Booking;
import com.example.mobileapp.utils.DateUtils;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private Context context;
    private List<Booking> bookingList;

    public BookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.referenceText.setText("Ref: " + booking.getBookingReference());
        holder.eventTitleText.setText(booking.getEventTitle());
        holder.eventDateText.setText(DateUtils.formatDate(booking.getEventDate()));
        holder.eventVenueText.setText(booking.getEventVenue());
        holder.seatsText.setText("Seats: " + String.join(", ", booking.getSeats()));
        holder.priceText.setText(String.format("%.2f TND", booking.getTotalPrice()));
        holder.bookingDateText.setText("Booked on: " + DateUtils.formatDate(booking.getBookingDate()));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView referenceText, eventTitleText, eventDateText, eventVenueText, seatsText, priceText, bookingDateText;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            referenceText = itemView.findViewById(R.id.referenceText);
            eventTitleText = itemView.findViewById(R.id.eventTitleText);
            eventDateText = itemView.findViewById(R.id.eventDateText);
            eventVenueText = itemView.findViewById(R.id.eventVenueText);
            seatsText = itemView.findViewById(R.id.seatsText);
            priceText = itemView.findViewById(R.id.priceText);
            bookingDateText = itemView.findViewById(R.id.bookingDateText);
        }
    }
}
