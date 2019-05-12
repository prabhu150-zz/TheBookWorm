package com.example.thebookworm.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookworm.R;
import com.example.thebookworm.BackEnd;

public class BuyerDashBoard extends Fragment {


    private BackEnd singleton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vendor_dashboard, null);
    }

    private void notifyByToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        singleton = new BackEnd(getContext(), "BuyerDashBoard");
        handleBuyerDashBoard();
    }

    private void handleBuyerDashBoard() {

    }


}
