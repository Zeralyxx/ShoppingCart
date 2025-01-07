package com.example.shoppingcart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.widget.Button;

public class CartFragment extends Fragment {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private CartManager cartManager;
    private ImageButton removeButton;
    private CheckBox selectAllCheckbox;
    private TextView totalPriceTextView;
    private TextView checkoutTotalPriceTextView;
    private Button checkoutButton;

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
        cartAdapter.setOnItemCheckedChangeListener(checkedItems -> {
            updateTotalPrice(checkedItems);
            updateCheckoutButtonState(checkedItems);
        });

        updateTotalPrice(cartAdapter.getCheckedItems());

        // Initialize checkout button and set click listener
        checkoutButton = view.findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(v -> navigateToCheckout());

        // Initial state of the checkout button
        updateCheckoutButtonState(cartAdapter.getCheckedItems());

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

    private void navigateToCheckout() {
        List<Product> checkedItems = cartAdapter.getCheckedItems();
        if (checkedItems != null && !checkedItems.isEmpty()) {
            Intent intent = new Intent(getActivity(), CheckoutActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Please select items to checkout", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateCheckoutButtonState(List<Product> checkedItems) {
        if (checkedItems == null || checkedItems.isEmpty()) {
            checkoutButton.setEnabled(false);
            checkoutButton.setAlpha(0.5f); // Optional: make it look disabled
        } else {
            checkoutButton.setEnabled(true);
            checkoutButton.setAlpha(1.0f); // Optional: make it look enabled
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (cartAdapter != null) {
            cartAdapter.notifyDataSetChanged();
            updateTotalPrice(cartAdapter.getCheckedItems());
            updateCheckoutButtonState(cartAdapter.getCheckedItems());
        }
    }
}