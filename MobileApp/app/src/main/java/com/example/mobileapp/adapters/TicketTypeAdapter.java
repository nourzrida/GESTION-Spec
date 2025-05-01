package com.example.mobileapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;
import com.example.mobileapp.models.TicketType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketTypeAdapter extends RecyclerView.Adapter<TicketTypeAdapter.TicketTypeViewHolder> {
    private Context context;
    private List<TicketType> ticketTypeList;
    private Map<String, Integer> selectedTickets;
    private OnTicketSelectedListener listener;

    public interface OnTicketSelectedListener {
        void onTicketSelected(Map<String, Integer> selectedTickets, double totalPrice);
    }

    public TicketTypeAdapter(Context context, List<TicketType> ticketTypeList, OnTicketSelectedListener listener) {
        this.context = context;
        this.ticketTypeList = ticketTypeList;
        this.listener = listener;
        this.selectedTickets = new HashMap<>();
    }

    @NonNull
    @Override
    public TicketTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ticket_type, parent, false);
        return new TicketTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketTypeViewHolder holder, int position) {
        TicketType ticketType = ticketTypeList.get(position);

        holder.ticketName.setText(ticketType.getName());
        holder.ticketPrice.setText(String.format("%.2f TND", ticketType.getPrice()));
        holder.ticketDescription.setText(ticketType.getDescription());
        holder.ticketAvailability.setText(String.format("%d/%d available", ticketType.getAvailableSeats(), ticketType.getTotalSeats()));

        // Get current quantity for this ticket type
        int quantity = selectedTickets.getOrDefault(ticketType.getName(), 0);
        holder.ticketQuantity.setText(String.valueOf(quantity));

        // Disable decrease button if quantity is 0
        holder.decreaseButton.setEnabled(quantity > 0);

        // Disable increase button if no more tickets available
        holder.increaseButton.setEnabled(quantity < ticketType.getAvailableSeats());

        // Set click listeners for increase and decrease
        holder.increaseButton.setOnClickListener(v -> {
            int currentQuantity = selectedTickets.getOrDefault(ticketType.getName(), 0);
            if (currentQuantity < ticketType.getAvailableSeats()) {
                selectedTickets.put(ticketType.getName(), currentQuantity + 1);
                holder.ticketQuantity.setText(String.valueOf(currentQuantity + 1));
                holder.decreaseButton.setEnabled(true);
                if (currentQuantity + 1 == ticketType.getAvailableSeats()) {
                    holder.increaseButton.setEnabled(false);
                }
                updateTotalPrice();
            }
        });

        holder.decreaseButton.setOnClickListener(v -> {
            int currentQuantity = selectedTickets.getOrDefault(ticketType.getName(), 0);
            if (currentQuantity > 0) {
                selectedTickets.put(ticketType.getName(), currentQuantity - 1);
                holder.ticketQuantity.setText(String.valueOf(currentQuantity - 1));
                holder.increaseButton.setEnabled(true);
                if (currentQuantity - 1 == 0) {
                    holder.decreaseButton.setEnabled(false);
                }
                updateTotalPrice();
            }
        });
    }

    private void updateTotalPrice() {
        double totalPrice = 0;
        for (TicketType ticketType : ticketTypeList) {
            int quantity = selectedTickets.getOrDefault(ticketType.getName(), 0);
            totalPrice += quantity * ticketType.getPrice();
        }
        listener.onTicketSelected(selectedTickets, totalPrice);
    }

    @Override
    public int getItemCount() {
        return ticketTypeList.size();
    }

    public static class TicketTypeViewHolder extends RecyclerView.ViewHolder {
        TextView ticketName;
        TextView ticketPrice;
        TextView ticketDescription;
        TextView ticketAvailability;
        TextView ticketQuantity;
        ImageButton increaseButton;
        ImageButton decreaseButton;

        public TicketTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            ticketName = itemView.findViewById(R.id.ticket_name);
            ticketPrice = itemView.findViewById(R.id.ticket_price);
            ticketDescription = itemView.findViewById(R.id.ticket_description);
            ticketAvailability = itemView.findViewById(R.id.ticket_availability);
            ticketQuantity = itemView.findViewById(R.id.ticket_quantity);
            increaseButton = itemView.findViewById(R.id.increase_button);
            decreaseButton = itemView.findViewById(R.id.decrease_button);
        }
    }
}
