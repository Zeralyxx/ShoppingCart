package com.example.shoppingcart;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    //Para sa items list to lahat
    private List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        if (view == null) {
            Log.e("ProductAdapter", "Inflated view is null");
        }
        return new ProductViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Bind data
        holder.productName.setText(product.getName());
        holder.productPrice.setText("₱" + product.getPrice());
        holder.productImage.setImageResource(product.getImageResId());
        holder.productQuantity.setText(String.valueOf(product.getQuantity()));

        // Handle "+" button click
        holder.itemView.findViewById(R.id.btnPlus).setOnClickListener(v -> {
            int currentQuantity = product.getQuantity();
            product.setQuantity(currentQuantity + 1);
            holder.productQuantity.setText(String.valueOf(product.getQuantity()));
        });

        // Handle "-" button click
        holder.itemView.findViewById(R.id.btnMinus).setOnClickListener(v -> {
            int currentQuantity = product.getQuantity();
            if (currentQuantity > 1) {
                product.setQuantity(currentQuantity - 1);
                holder.productQuantity.setText(String.valueOf(product.getQuantity()));
            }
        });

        // Handle "Add to Cart" button click
        holder.addToCartButton.setOnClickListener(v -> {
            Log.d("ProductAdapter", "Add to Cart clicked for " + product.getName());
            // Add logic to handle adding to cart if needed
        });
    }



    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productPrice;
        ImageView productImage;
        TextView productQuantity;
        Button addToCartButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            // Bind views
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productImage = itemView.findViewById(R.id.productImage);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);

            // Debugging logs
            Log.d("ProductViewHolder", "productName: " + (productName != null));
            Log.d("ProductViewHolder", "productPrice: " + (productPrice != null));
            Log.d("ProductViewHolder", "productImage: " + (productImage != null));
            Log.d("ProductViewHolder", "productQuantity: " + (productQuantity != null));
            Log.d("ProductViewHolder", "addToCartButton: " + (addToCartButton != null));
        }
    }

    //Para sa search bar function
    public void updateProductList(List<Product> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }


}
