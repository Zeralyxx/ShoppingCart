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

public class CancelledAdapter extends RecyclerView.Adapter<CancelledAdapter.CancelledViewHolder> {

    private List<CancelledProduct> cancelledOrderList;

    public CancelledAdapter(List<CancelledProduct> cancelledOrderList) {
        this.cancelledOrderList = cancelledOrderList;
    }

    @NonNull
    @Override
    public CancelledViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cancelled, parent, false);
        return new CancelledViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CancelledViewHolder holder, int position) {
        CancelledProduct cancelledProduct = cancelledOrderList.get(position);
        Product product = cancelledProduct.getProduct();
        holder.productNameTextView.setText(product.getName());
        holder.productPriceTextView.setText("₱" + String.format("%.2f", product.getPrice()));
        holder.productQuantityTextView.setText("x" + product.getQuantity());
        holder.cancellationDateTextView.setText("Cancelled on: " + cancelledProduct.getCancellationDate());

        // Get the image resource ID based on imageNum
        int imageResId = getImageResource(product.getImageNum());

        // Load product image using Glide with resource ID
        Glide.with(holder.itemView.getContext())
                .load(imageResId)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.productImageView);
    }

    @Override
    public int getItemCount() {
        return cancelledOrderList.size();
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
        return R.drawable.ic_launcher_foreground; // Default image
    }

    public static class CancelledViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView productQuantityTextView;
        TextView cancellationDateTextView;

        public CancelledViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            productQuantityTextView = itemView.findViewById(R.id.productQuantityTextView);
            cancellationDateTextView = itemView.findViewById(R.id.cancellationDateTextView);
        }
    }
}