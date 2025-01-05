package com.example.shoppingcart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Product> cartItems;
    private List<Product> checkedItems = new ArrayList<>();
    private OnItemCheckedChangeListener onItemCheckedChangeListener;
    private boolean isSelectAllChanging = false;

    public CartAdapter(List<Product> cartItems) {
        this.cartItems = cartItems;
    }

    public interface OnItemCheckedChangeListener {
        void onItemCheckedChanged(List<Product> checkedItems);
    }

    public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener listener) {
        this.onItemCheckedChangeListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product cartItem = cartItems.get(position);
        holder.productName.setText(cartItem.getName());
        double totalPrice = cartItem.getPrice() * cartItem.getQuantity();
        holder.productPrice.setText("₱" + String.format("%.2f", totalPrice));
        holder.productImage.setImageResource(cartItem.getImageResId());
        holder.productQuantity.setText("Qty: " + cartItem.getQuantity());

        // Set checkbox state
        holder.itemCheckbox.setChecked(checkedItems.contains(cartItem));

        // Handle checkbox clicks
        holder.itemCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Add only if not already present
                if (!checkedItems.contains(cartItem)) {
                    checkedItems.add(cartItem);
                }
            } else {
                checkedItems.remove(cartItem);
            }
            if (onItemCheckedChangeListener != null && !isSelectAllChanging) {
                onItemCheckedChangeListener.onItemCheckedChanged(checkedItems);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public List<Product> getCheckedItems() {
        return checkedItems;
    }

    public void clearCheckedItems() {
        checkedItems.clear();
        notifyDataSetChanged();
        if (onItemCheckedChangeListener != null && !isSelectAllChanging) {
            onItemCheckedChangeListener.onItemCheckedChanged(checkedItems);
        }
    }

    public void selectAllItems() {
        isSelectAllChanging = true;

        // Clear the list to avoid duplicates
        checkedItems.clear();

        // Add all unique items
        checkedItems.addAll(cartItems);

        notifyDataSetChanged();

        if (onItemCheckedChangeListener != null) {
            onItemCheckedChangeListener.onItemCheckedChanged(checkedItems);
        }

        isSelectAllChanging = false;
    }



    public void deselectAllItems() {
        isSelectAllChanging = true;
        checkedItems.clear();
        notifyDataSetChanged();
        if (onItemCheckedChangeListener != null) {
            onItemCheckedChangeListener.onItemCheckedChanged(checkedItems);
        }
        isSelectAllChanging = false;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productPrice;
        ImageView productImage;
        TextView productQuantity;
        CheckBox itemCheckbox;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productImage = itemView.findViewById(R.id.productImage);
            productQuantity = itemView.findViewById(R.id.productQuantity);
            itemCheckbox = itemView.findViewById(R.id.itemCheckbox);
        }
    }

}