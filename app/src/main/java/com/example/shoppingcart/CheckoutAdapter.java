package com.example.shoppingcart;

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

    private List<Product> productList;

    public CheckoutAdapter(List<Product> productList) {
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
        Product product = productList.get(position);
        holder.productNameTextView.setText(product.getName());
        holder.productPriceTextView.setText("₱" + String.format("%.2f", product.getPrice()));
        holder.productQuantityTextView.setText("x" + product.getQuantity());

        // Load product image using Glide with resource ID
        Glide.with(holder.itemView.getContext())
                .load(product.getImageResId()) // Load image using resource ID
                .placeholder(R.drawable.ic_launcher_background) // Placeholder image while loading
                .error(R.drawable.ic_launcher_background) // Error image if loading fails
                .into(holder.productImageView);
    }

    @Override
    public int getItemCount() {
        return productList.size();
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