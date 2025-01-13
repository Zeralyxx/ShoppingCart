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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ToReceiveAdapter extends RecyclerView.Adapter<ToReceiveAdapter.ToReceiveViewHolder> {

    private static final String TAG = "ToReceiveAdapter";
    private FirebaseFirestore db;
    private Context context;
    private SharedPreferences sharedPreferences;
    private List<DocumentSnapshot> documentSnapshots;

    public ToReceiveAdapter(Context context, List<DocumentSnapshot> documentSnapshots) {
        this.context = context;
        this.documentSnapshots = documentSnapshots;
        db = FirebaseFirestore.getInstance();
        sharedPreferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ToReceiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_to_receive, parent, false);
        return new ToReceiveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToReceiveViewHolder holder, int position) {
        if (documentSnapshots == null || documentSnapshots.isEmpty() || position >= documentSnapshots.size()) {
            Log.e(TAG, "Invalid documentSnapshots or position: " + position);
            return;
        }

        DocumentSnapshot document = documentSnapshots.get(position);
        if (document == null) {
            Log.e(TAG, "DocumentSnapshot is null at position: " + position);
            return;
        }

        // Extract data from DocumentSnapshot
        String productName = document.getString("productName");
        Double price = document.getDouble("price");
        Long quantity = document.getLong("quantity");
        Long imageNum = document.getLong("imageNum");

        if (productName == null || price == null || quantity == null || imageNum == null) {
            Log.e(TAG, "One or more fields are null in DocumentSnapshot at position: " + position);
            return;
        }

        int imageResourceId = getImageResource(imageNum.intValue());

        Product product = new Product(productName, price, imageResourceId, imageNum.intValue(), quantity.intValue());
        product.setDocumentId(document.getId());

        holder.productNameTextView.setText(product.getName());
        holder.productPriceTextView.setText("₱" + String.format("%.2f", product.getPrice()));
        holder.productQuantityTextView.setText("x" + product.getQuantity());

        // Load product image using Glide with resource ID
        Glide.with(holder.itemView.getContext())
                .load(product.getImageResId())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.productImageView);

        // Set initial state of the received button and preparing text
        holder.receivedButton.setEnabled(true);
        holder.receivedButton.setTextColor(Color.BLACK);
        holder.receivedButton.setText("Received");
        holder.preparingTextView.setText("Product is on delivery");

        // Check if the item is already received
        db.collection("received")
                .whereEqualTo("documentId", document.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Item is already received, disable the button and update the text
                        holder.receivedButton.setEnabled(false);
                        holder.receivedButton.setTextColor(Color.GRAY);
                        holder.receivedButton.setText("Received");
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String receivedDate = doc.getString("receivedDate");
                            holder.preparingTextView.setText("Received on: " + receivedDate);
                        }
                    } else {
                        // Item is not received, set up the received button listener
                        holder.receivedButton.setOnClickListener(v -> showReceivedConfirmationDialog(holder, product, document.getId(), position));
                    }
                });
    }

    private int getImageResource(int imageNum) {
        if (imageNum == 1) {
            return R.drawable.pastil;
        } else if (imageNum == 2) {
            return R.drawable.rice;
        } else if (imageNum == 3) {
            return R.drawable.hotdog;
        } else if (imageNum == 4) {
            return R.drawable.coke;
        } else if (imageNum == 5) {
            return R.drawable.sprite1;
        } else if (imageNum == 6) {
            return R.drawable.styro;
        } else if (imageNum == 7) {
            return R.drawable.spoon1;
        } else if (imageNum == 8) {
            return R.drawable.fork;
        } else if (imageNum == 9) {
            return R.drawable.chicken;
        } else if (imageNum == 10) {
            return R.drawable.kwek;
        }
        return R.drawable.ic_launcher_foreground; // Default image
    }

    private void showReceivedConfirmationDialog(ToReceiveViewHolder holder, Product product, String documentId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setTitle("Confirm Received");
        builder.setMessage("Have you received this order?");
        builder.setPositiveButton("Yes", (dialog, which) -> confirmReceived(holder, product, documentId, position));
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void confirmReceived(ToReceiveViewHolder holder, Product product, String documentId, int position) {
        // Get the current date and time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String receivedDate = sdf.format(new Date());

        // Disable the received button immediately
        holder.receivedButton.setEnabled(false);
        holder.receivedButton.setTextColor(Color.GRAY);
        holder.receivedButton.setText("Received");
        holder.preparingTextView.setText("Received on: " + receivedDate);

        // Get the username from SharedPreferences
        String username = sharedPreferences.getString("userId", "");

        // Create a map for the received order
        Map<String, Object> receivedOrder = new HashMap<>();
        receivedOrder.put("username", username);
        receivedOrder.put("name", product.getName());
        receivedOrder.put("imageNum", product.getImageNum());
        receivedOrder.put("productName", product.getName());
        receivedOrder.put("price", product.getPrice());
        receivedOrder.put("quantity", product.getQuantity());
        receivedOrder.put("receivedDate", receivedDate);
        receivedOrder.put("documentId", documentId);

        // Log the documentId
        Log.d(TAG, "confirmReceived: Document ID to delete: " + documentId);

        // Add the received order to the received collection
        db.collection("received")
                .add(receivedOrder)
                .addOnSuccessListener(documentReference -> {
                    // Delete the document from the toReceive collection
                    db.collection("toReceive")
                            .document(documentId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "confirmReceived: Order received and moved to received collection");
                                Toast.makeText(holder.itemView.getContext(), "Order received", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "confirmReceived: Error deleting order from toReceive collection.", e);
                                Toast.makeText(holder.itemView.getContext(), "Error deleting order from toReceive collection.", Toast.LENGTH_SHORT).show();
                                holder.receivedButton.setEnabled(true);
                                holder.receivedButton.setTextColor(Color.BLACK);
                                holder.receivedButton.setText("Received");
                                holder.preparingTextView.setText("Product is on delivery");
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "confirmReceived: Error adding order to received collection.", e);
                    Toast.makeText(holder.itemView.getContext(), "Error adding order to received collection.", Toast.LENGTH_SHORT).show();
                    holder.receivedButton.setEnabled(true);
                    holder.receivedButton.setTextColor(Color.BLACK);
                    holder.receivedButton.setText("Received");
                    holder.preparingTextView.setText("Product is on delivery");
                });
    }

    @Override
    public int getItemCount() {
        return documentSnapshots != null ? documentSnapshots.size() : 0;
    }

    public static class ToReceiveViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView productQuantityTextView;
        TextView preparingTextView;
        Button receivedButton;

        public ToReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            productQuantityTextView = itemView.findViewById(R.id.productQuantityTextView);
            preparingTextView = itemView.findViewById(R.id.preparingTextView);
            receivedButton = itemView.findViewById(R.id.receivedButton);
        }
    }
}