package com.example.shoppingcart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ToShipAdapter extends RecyclerView.Adapter<ToShipAdapter.OrderViewHolder> {

    private List<Product> orderList;

    public ToShipAdapter(List<Product> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_to_ship, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Product product = orderList.get(position);
        holder.orderImageView.setImageResource(product.getImageResId()); // Changed here
        holder.orderNameTextView.setText(product.getName());
        holder.orderPriceTextView.setText("₱" + String.format("%.2f", product.getPrice()));
        holder.orderQuantityTextView.setText("Quantity: " + product.getQuantity());

        // Set click listener for the cancel button
        holder.cancelButton.setOnClickListener(v -> {
            // Handle cancel button click here
            Toast.makeText(holder.itemView.getContext(), "Order Cancelled", Toast.LENGTH_SHORT).show();
            // You can add logic to remove the item from the list or update the UI
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView orderImageView;
        TextView orderNameTextView;
        TextView orderPriceTextView;
        TextView orderQuantityTextView;
        TextView preparingTextView;
        Button cancelButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderImageView = itemView.findViewById(R.id.productImageView);
            orderNameTextView = itemView.findViewById(R.id.productNameTextView);
            orderPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            orderQuantityTextView = itemView.findViewById(R.id.productQuantityTextView);
            preparingTextView = itemView.findViewById(R.id.preparingTextView);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
}