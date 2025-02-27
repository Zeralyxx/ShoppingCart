package com.example.shoppingcart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class AccountFragment extends Fragment {

    private static final String TAG = "AccountFragment";
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        updateUI();
    }

    private void updateUI() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        Log.d(TAG, "isLoggedIn: " + isLoggedIn);
        if (isLoggedIn) {
            replaceFragment(new AccountFragmentIn());
        } else {
            replaceFragment(new AccountFragmentOut());
        }
    }

    private void replaceFragment(Fragment fragment) {
        Log.d(TAG, "replaceFragment");
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.account_fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }
}