package com.example.shoppingcart;

import android.content.Context;
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

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;
    private CartManager cartManager;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
        this.cartManager = CartManager.getInstance();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText("₱" + String.format("%.2f", product.getPrice()));
        holder.productImage.setImageResource(product.getImageResId());
        holder.productQuantity.setText(String.valueOf(product.getQuantity()));

        holder.addButton.setOnClickListener(v -> {
            int quantity = product.getQuantity() + 1;
            product.setQuantity(quantity);
            holder.productQuantity.setText(String.valueOf(quantity));
        });

        holder.removeButton.setOnClickListener(v -> {
            int quantity = product.getQuantity();
            if (quantity > 1) {
                quantity--;
                product.setQuantity(quantity);
                holder.productQuantity.setText(String.valueOf(quantity));
            }
        });

        holder.addToCartButton.setOnClickListener(v -> {
            Toast.makeText(context, "Added " + product.getName() + " to cart", Toast.LENGTH_SHORT).show();
            Log.d("ProductAdapter", "Add to Cart clicked for " + product.getName() + ", quantity: " + product.getQuantity());
            cartManager.addToCart(new Product(product.getName(), product.getPrice(), product.getImageResId(), product.getImageNum(), product.getQuantity())); // Add product to cart with correct imageNum and quantity
            product.setQuantity(1); // Reset quantity to 1
            holder.productQuantity.setText(String.valueOf(1)); // Update the displayed quantity
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProductList(List<Product> filteredList) {
        this.productList = filteredList;
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productPrice;
        ImageView productImage;
        TextView productQuantity;
        Button addButton;
        Button removeButton;
        Button addToCartButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productImage = itemView.findViewById(R.id.productImage);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            addButton = itemView.findViewById(R.id.btnPlus);
            removeButton = itemView.findViewById(R.id.btnMinus);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }
}