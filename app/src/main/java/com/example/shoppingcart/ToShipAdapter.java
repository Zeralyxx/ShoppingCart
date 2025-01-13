package com.example.shoppingcart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ToShipAdapter extends RecyclerView.Adapter<ToShipAdapter.ToShipViewHolder> {

    private static final String TAG = "ToShipAdapter";
    private FirebaseFirestore db;
    private Context context;
    private SharedPreferences sharedPreferences;
    private List<DocumentSnapshot> documentSnapshots;
    private Set<String> transferredItems;
    private Map<String, Boolean> cancelStatus = new HashMap<>();

    public ToShipAdapter(Context context, List<DocumentSnapshot> documentSnapshots) {
        this.context = context;
        this.documentSnapshots = documentSnapshots;
        db = FirebaseFirestore.getInstance();
        sharedPreferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        transferredItems = getTransferredItemsFromPrefs();
    }

    @NonNull
    @Override
    public ToShipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_to_ship, parent, false);
        return new ToShipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToShipViewHolder holder, int position) {
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

        // Set initial state of the cancel button and preparing text
        holder.cancelButton.setEnabled(true);
        holder.cancelButton.setTextColor(Color.BLACK);
        holder.cancelButton.setText("Cancel");
        holder.preparingTextView.setText("Seller is preparing your package");

        // Set up the cancel button listener
        holder.cancelButton.setOnClickListener(v -> showCancelConfirmationDialog(holder, product, document.getId()));

        // Check if the item has already been transferred or cancelled
        if (!transferredItems.contains(document.getId()) && !cancelStatus.containsKey(document.getId())) {
            // Check if the item has already been transferred to "toReceive"
            db.collection("toReceive")
                    .whereEqualTo("documentId", document.getId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().isEmpty()) {
                            // Transfer the item after 10 seconds
                            new Handler().postDelayed(() -> {
                                if (!transferredItems.contains(document.getId()) && !cancelStatus.containsKey(document.getId())) {
                                    transferToReceive(holder, product, document.getId());
                                    transferredItems.add(document.getId());
                                    saveTransferredItemsToPrefs();
                                }
                            }, 10000);
                        } else {
                            transferredItems.add(document.getId());
                            saveTransferredItemsToPrefs();
                        }
                    });
        }
    }

    private int getImageResource(int imageNum) {
        if (imageNum == 1) {
            return R.drawable.pastil1;
        } else if (imageNum == 2) {
            return R.drawable.rice1;
        } else if (imageNum == 3) {
            return R.drawable.hotdog1;
        } else if (imageNum == 4) {
            return R.drawable.coke1;
        } else if (imageNum == 5) {
            return R.drawable.sprite1;
        } else if (imageNum == 6) {
            return R.drawable.styro1;
        } else if (imageNum == 7) {
            return R.drawable.spoon1;
        } else if (imageNum == 8) {
            return R.drawable.fork1;
        } else if (imageNum == 9) {
            return R.drawable.chicken1;
        } else if (imageNum == 10) {
            return R.drawable.kwek1;
        }
        return R.drawable.ic_launcher_foreground;
    }

    private void showCancelConfirmationDialog(ToShipViewHolder holder, Product product, String documentId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setTitle("Confirm Cancellation");
        builder.setMessage("Are you sure you want to cancel this order?");
        builder.setPositiveButton("Yes", (dialog, which) -> cancelOrder(holder, product, documentId));
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void cancelOrder(ToShipViewHolder holder, Product product, String documentId) {
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
        cancelledOrder.put("documentId", documentId);

        // Add the cancelled order to the cancelled collection
        db.collection("cancelled")
                .add(cancelledOrder)
                .addOnSuccessListener(documentReference -> {
                    // Delete the document from the toShip collection
                    db.collection("toShip")
                            .document(documentId).delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "cancelOrder: Order cancelled and moved to cancelled collection");
                                Toast.makeText(holder.itemView.getContext(), "Order cancelled", Toast.LENGTH_SHORT).show();
                                cancelStatus.put(documentId, true); // Mark as cancelled
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "cancelOrder: Error deleting order from toShip collection.", e);
                                Toast.makeText(holder.itemView.getContext(), "Error deleting order from toShip collection.", Toast.LENGTH_SHORT).show();
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

    private void transferToReceive(ToShipViewHolder holder, Product product, String documentId) {
        // Get the username from SharedPreferences
        String username = sharedPreferences.getString("userId", "");

        // Create a map for the toReceive order
        Map<String, Object> toReceiveOrder = new HashMap<>();
        toReceiveOrder.put("username", username);
        toReceiveOrder.put("name", product.getName());
        toReceiveOrder.put("imageNum", product.getImageNum());
        toReceiveOrder.put("productName", product.getName());
        toReceiveOrder.put("price", product.getPrice());
        toReceiveOrder.put("quantity", product.getQuantity());
        toReceiveOrder.put("documentId", documentId);

        // Add the order to the toReceive collection
        db.collection("toReceive")
                .add(toReceiveOrder)
                .addOnSuccessListener(documentReference -> {
                    // Delete the document from the toShip collection
                    db.collection("toShip")
                            .document(documentId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "transferToReceive: Order moved to toReceive collection");
                                Toast.makeText(holder.itemView.getContext(), "Order is on the way", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "transferToReceive: Error deleting order from toShip collection.", e);
                                Toast.makeText(holder.itemView.getContext(), "Error moving order to toReceive collection.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "transferToReceive: Error adding order to toReceive collection.", e);
                    Toast.makeText(holder.itemView.getContext(), "Error moving order to toReceive collection.", Toast.LENGTH_SHORT).show();
                });
    }

    private Set<String> getTransferredItemsFromPrefs() {
        String serializedSet = sharedPreferences.getString("transferred_items", "");
        if (serializedSet.isEmpty()) {
            return new HashSet<>();
        }
        String[] items = serializedSet.split(",");
        Set<String> set = new HashSet<>();
        for (String item : items) {
            set.add(item);
        }
        return set;
    }

    private void saveTransferredItemsToPrefs() {
        StringBuilder sb = new StringBuilder();
        for (String item : transferredItems) {
            sb.append(item).append(",");
        }
        String serializedSet = sb.toString();
        if (serializedSet.endsWith(",")) {
            serializedSet = serializedSet.substring(0, serializedSet.length() - 1);
        }
        sharedPreferences.edit().putString("transferred_items", serializedSet).apply();
    }

    @Override
    public int getItemCount() {
        return documentSnapshots != null ? documentSnapshots.size() : 0;
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
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
}