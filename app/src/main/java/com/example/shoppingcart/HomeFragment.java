package com.example.shoppingcart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> filteredList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Initialize product list
        productList = new ArrayList<>();
        productList.add(new Product("Pastil", 15.0, R.drawable.pastil, 1, 1));
        productList.add(new Product("Rice", 10.0, R.drawable.rice, 2, 1));
        productList.add(new Product("Hotdog", 10.0, R.drawable.hotdog, 3, 1));
        productList.add(new Product("Coke", 15.0, R.drawable.coke, 4, 1));
        productList.add(new Product("Sprite", 15.0, R.drawable.sprite, 5, 1));
        productList.add(new Product("Food Container", 3.0, R.drawable.styro, 6, 1));
        productList.add(new Product("Plastic Spoon", 1.0, R.drawable.spoon, 7, 1));
        productList.add(new Product("Plastic Fork", 1.0, R.drawable.fork, 8, 1));
        productList.add(new Product("Fried Chicken", 10.0, R.drawable.chicken, 9, 1));
        productList.add(new Product("Kwek-Kwek", 3.0, R.drawable.kwek, 10, 1));


        // Create a copy of the product list for filtering
        filteredList = new ArrayList<>(productList);

        // Set adapter
        productAdapter = new ProductAdapter(filteredList, getContext());
        recyclerView.setAdapter(productAdapter);



        // Search bar functionality
        EditText editSearch = view.findViewById(R.id.editSearch);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    //Para gumana search bar
    private void filterProducts(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(productList);
        } else {
            for (Product product : productList) {
                if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(product);
                }
            }
        }
        productAdapter.updateProductList(filteredList);
    }
}