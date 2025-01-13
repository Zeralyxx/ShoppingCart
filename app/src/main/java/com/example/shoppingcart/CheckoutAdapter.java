package com.example.shoppingcart;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder> {

    private static final String TAG = "CheckoutAdapter";
    private Context context;
    private List<Product> productList;

    public CheckoutAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_item_layout, parent, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        if (productList == null || productList.isEmpty() || position >= productList.size()) {
            Log.e(TAG, "Invalid productList or position: " + position);
            return;
        }

        Product product = productList.get(position);
        if (product == null) {
            Log.e(TAG, "Product is null at position: " + position);
            return;
        }

        holder.productNameTextView.setText(product.getName());
        holder.productPriceTextView.setText("₱" + String.format("%.2f", product.getPrice()));
        holder.productQuantityTextView.setText("x" + product.getQuantity());

        int imageResourceId = getImageResource(product.getImageNum());

        // Load product image using Glide with resource ID
        Glide.with(holder.itemView.getContext())
                .load(imageResourceId)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.productImageView);
    }

    private int getImageResource(int imageNum) {
        if (imageNum == 1) {
            return R.drawable.pastil2;
        } else if (imageNum == 2) {
            return R.drawable.rice2;
        } else if (imageNum == 3) {
            return R.drawable.hotdog2;
        } else if (imageNum == 4) {
            return R.drawable.coke2;
        } else if (imageNum == 5) {
            return R.drawable.sprite2;
        } else if (imageNum == 6) {
            return R.drawable.styro2;
        } else if (imageNum == 7) {
            return R.drawable.spoon2;
        } else if (imageNum == 8) {
            return R.drawable.fork2;
        } else if (imageNum == 9) {
            return R.drawable.chicken2;
        } else if (imageNum == 10) {
            return R.drawable.kwek2;
        }
        return R.drawable.ic_launcher_foreground; // Default image
    }


    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class CheckoutViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView productQuantityTextView;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            productQuantityTextView = itemView.findViewById(R.id.productQuantityTextView);
        }
    }
}