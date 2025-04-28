package com.example.teskertievents.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teskertievents.R;
import com.example.teskertievents.models.TicketType;

import java.util.List;

public class TicketTypeAdapter extends RecyclerView.Adapter<TicketTypeAdapter.TicketTypeViewHolder> {
    private Context context;
    private List<TicketType> ticketTypes;
    private OnQuantityChangedListener listener;

    public interface OnQuantityChangedListener {
        void onQuantityChanged();
    }

    public TicketTypeAdapter(Context context, List<TicketType> ticketTypes, OnQuantityChangedListener listener) {
        this.context = context;
        this.ticketTypes = ticketTypes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TicketTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ticket_type, parent, false);
        return new TicketTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketTypeViewHolder holder, int position) {
        TicketType ticketType = ticketTypes.get(position);
        
        holder.nameText.setText(ticketType.getName());
        holder.descriptionText.setText(ticketType.getDescription());
        holder.priceText.setText(String.format("%.2f TND", ticketType.getPrice()));
        holder.quantityText.setText(String.valueOf(ticketType.getSelectedQuantity()));
        
        // Setup increment button
        holder.incrementButton.setOnClickListener(v -> {
            ticketType.incrementQuantity();
            holder.quantityText.setText(String.valueOf(ticketType.getSelectedQuantity()));
            if (listener != null) {
                listener.onQuantityChanged();
            }
        });
        
        // Setup decrement button
        holder.decrementButton.setOnClickListener(v -> {
            ticketType.decrementQuantity();
            holder.quantityText.setText(String.valueOf(ticketType.getSelectedQuantity()));
            if (listener != null) {
                listener.onQuantityChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ticketTypes.size();
    }

    public static class TicketTypeViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, descriptionText, priceText, quantityText;
        ImageButton incrementButton, decrementButton;

        public TicketTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            priceText = itemView.findViewById(R.id.priceText);
            quantityText = itemView.findViewById(R.id.quantityText);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
        }
    }
}
