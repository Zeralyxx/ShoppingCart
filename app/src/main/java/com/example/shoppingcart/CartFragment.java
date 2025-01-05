package com.example.shoppingcart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private CartManager cartManager;
    private ImageButton removeButton;
    private CheckBox selectAllCheckbox;
    private TextView totalPriceTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Initialize RecyclerView
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get CartManager instance
        cartManager = CartManager.getInstance();

        // Initialize cart adapter
        List<Product> cartItems = cartManager.getCartItems();
        cartAdapter = new CartAdapter(cartItems);
        cartRecyclerView.setAdapter(cartAdapter);

        // Initialize remove button
        removeButton = view.findViewById(R.id.removeButton);
        removeButton.setOnClickListener(v -> removeCheckedItems());

        // Initialize select all checkbox and total price text view
        selectAllCheckbox = view.findViewById(R.id.selectAllCheckbox);
        totalPriceTextView = view.findViewById(R.id.totalPriceTextView);

        // Set up select all checkbox listener
        selectAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cartAdapter.selectAllItems();
            } else {
                cartAdapter.deselectAllItems();
            }
            updateTotalPrice(cartAdapter.getCheckedItems());
        });


        // Set up item checked change listener
        cartAdapter.setOnItemCheckedChangeListener(checkedItems -> updateTotalPrice(checkedItems));

        updateTotalPrice(cartAdapter.getCheckedItems());

        return view;
    }

    private void removeCheckedItems() {
        List<Product> checkedItems = cartAdapter.getCheckedItems();
        if (checkedItems != null && !checkedItems.isEmpty()) {
            cartManager.getCartItems().removeAll(checkedItems);
            cartAdapter.clearCheckedItems();
            cartAdapter.notifyDataSetChanged();
            updateTotalPrice(cartAdapter.getCheckedItems());
            selectAllCheckbox.setChecked(false); // Reset select all checkbox
        }
    }


    private void updateTotalPrice(List<Product> checkedItems) {
        double totalPrice = 0;
        if (checkedItems != null) {
            for (Product product : checkedItems) {
                totalPrice += product.getPrice() * product.getQuantity();
            }
        }
        totalPriceTextView.setText("Total: ₱" + String.format("%.2f", totalPrice));
    }



    @Override
    public void onResume() {
        super.onResume();
        if (cartAdapter != null) {
            cartAdapter.notifyDataSetChanged();
            updateTotalPrice(cartAdapter.getCheckedItems());
        }
    }
}