package com.example.shoppingcart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ToShipAdapter extends RecyclerView.Adapter<ToShipAdapter.ToShipViewHolder> {

    private static final String TAG = "ToShipAdapter";
    private List<Product> orderList;
    private FirebaseFirestore db;
    private Context context;
    private SharedPreferences sharedPreferences;

    public ToShipAdapter(List<Product> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
        db = FirebaseFirestore.getInstance();
        sharedPreferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ToShipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_to_ship, parent, false);
        return new ToShipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToShipViewHolder holder, int position) {
        Product product = orderList.get(position);
        holder.productNameTextView.setText(product.getName());
        holder.productPriceTextView.setText("₱" + String.format("%.2f", product.getPrice()));
        holder.productQuantityTextView.setText("x" + product.getQuantity());

        // Load product image using Glide with resource ID
        Glide.with(holder.itemView.getContext())
                .load(product.getImageResId())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.productImageView);

        // Set initial state of the cancel button and preparing text
        holder.cancelButton.setEnabled(true);
        holder.cancelButton.setTextColor(Color.BLACK);
        holder.cancelButton.setText("Cancel");
        holder.preparingTextView.setText("Seller is preparing your package");

        // Check if the item is already cancelled
        db.collection("cancelled")
                .whereEqualTo("documentId", product.getDocumentId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Item is already cancelled, disable the button and update the text
                        holder.cancelButton.setEnabled(false);
                        holder.cancelButton.setTextColor(Color.GRAY);
                        holder.cancelButton.setText("Cancelled");
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String cancellationDate = document.getString("cancellationDate");
                            holder.preparingTextView.setText("Cancelled on: " + cancellationDate);
                        }
                    } else {
                        // Item is not cancelled, set up the cancel button listener
                        holder.cancelButton.setOnClickListener(v -> showCancelConfirmationDialog(holder, product, position));
                    }
                });
    }

    private void showCancelConfirmationDialog(ToShipViewHolder holder, Product product, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setTitle("Cancel Order");
        builder.setMessage("Are you sure you want to cancel this order?");
        builder.setPositiveButton("Yes", (dialog, which) -> cancelOrder(holder, product, position));
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void cancelOrder(ToShipViewHolder holder, Product product, int position) {
        // Get the current date and time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String cancellationDate = sdf.format(new Date());

        // Disable the cancel button immediately
        holder.cancelButton.setEnabled(false);
        holder.cancelButton.setTextColor(Color.GRAY);
        holder.cancelButton.setText("Cancelled");
        holder.preparingTextView.setText("Cancelled on: " + cancellationDate);

        // Get the username from SharedPreferences
        String username = sharedPreferences.getString("userId", "");

        // Create a map for the cancelled order
        Map<String, Object> cancelledOrder = new HashMap<>();
        cancelledOrder.put("username", username);
        cancelledOrder.put("name", product.getName());
        cancelledOrder.put("imageNum", product.getImageNum());
        cancelledOrder.put("productName", product.getName());
        cancelledOrder.put("price", product.getPrice());
        cancelledOrder.put("quantity", product.getQuantity());
        cancelledOrder.put("cancellationDate", cancellationDate);
        cancelledOrder.put("documentId", product.getDocumentId());

        // Add the cancelled order to the cancelled collection
        db.collection("cancelled")
                .add(cancelledOrder)
                .addOnSuccessListener(documentReference -> {
                    // Delete the document from the toShip collection
                    db.collection("toShip")
                            .whereEqualTo("documentId", product.getDocumentId())
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        document.getReference().delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d(TAG, "cancelOrder: Order cancelled and moved to cancelled collection");
                                                    Toast.makeText(holder.itemView.getContext(), "Order cancelled", Toast.LENGTH_SHORT).show();
                                                    orderList.remove(position);
                                                    notifyDataSetChanged();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e(TAG, "cancelOrder: Error deleting order from toShip collection.", e);
                                                    Toast.makeText(holder.itemView.getContext(), "Error deleting order from toShip collection.", Toast.LENGTH_SHORT).show();
                                                    holder.cancelButton.setEnabled(true);
                                                    holder.cancelButton.setTextColor(Color.BLACK);
                                                    holder.cancelButton.setText("Cancel");
                                                    holder.preparingTextView.setText("Seller is preparing your package");
                                                });
                                    }
                                } else {
                                    Log.e(TAG, "cancelOrder: Document not found in toShip collection.");
                                    Toast.makeText(holder.itemView.getContext(), "Document not found in toShip collection.", Toast.LENGTH_SHORT).show();
                                    holder.cancelButton.setEnabled(true);
                                    holder.cancelButton.setTextColor(Color.BLACK);
                                    holder.cancelButton.setText("Cancel");
                                    holder.preparingTextView.setText("Seller is preparing your package");
                                }
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "cancelOrder: Error getting document from toShip collection.", e);
                                Toast.makeText(holder.itemView.getContext(), "Error getting document from toShip collection.", Toast.LENGTH_SHORT).show();
                                holder.cancelButton.setEnabled(true);
                                holder.cancelButton.setTextColor(Color.BLACK);
                                holder.cancelButton.setText("Cancel");
                                holder.preparingTextView.setText("Seller is preparing your package");
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "cancelOrder: Error adding order to cancelled collection.", e);
                    Toast.makeText(holder.itemView.getContext(), "Error adding order to cancelled collection.", Toast.LENGTH_SHORT).show();
                    holder.cancelButton.setEnabled(true);
                    holder.cancelButton.setTextColor(Color.BLACK);
                    holder.cancelButton.setText("Cancel");
                    holder.preparingTextView.setText("Seller is preparing your package");
                });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ToShipViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView productQuantityTextView;
        TextView preparingTextView;
        Button cancelButton;

        public ToShipViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            productQuantityTextView = itemView.findViewById(R.id.productQuantityTextView);
            preparingTextView = itemView.findViewById(R.id.preparingTextView);
            cancelButton= itemView.findViewById(R.id.cancelButton);
        }
    }
}