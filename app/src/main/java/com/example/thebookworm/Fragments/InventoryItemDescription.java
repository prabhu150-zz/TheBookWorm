package com.example.thebookworm.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookworm.R;
import com.example.thebookworm.Activities.BaseActivity;
import com.example.thebookworm.BackEnd;
import com.example.thebookworm.Models.Product;
import com.example.thebookworm.Models.Seller;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class InventoryItemDescription extends Fragment {

    private BackEnd backEnd;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.product_description, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backEnd = new BackEnd(getActivity(), "SellerSpecificItem");
        checkArguments();
    }

    private void checkArguments() {

        String pid = getArguments().getString("pid");
//        String productType= getArguments().getString("productType");

        if (pid == null)
            throw new IllegalArgumentException("Arguments not recieved!");

        retrieveProduct(getArguments());
    }

    private void retrieveProduct(final Bundle arguments) {

        String pid = getArguments().getString("pid");
        String productType = getArguments().getString("productType");


        FirebaseDatabase.getInstance().getReference("users/sellers/inventory" + productType).child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Product currentProduct = backEnd.fetchProduct(dataSnapshot, arguments);

                    Log.d("checkUrl", "onDataChange: " + currentProduct.getImageURL());
                    updateSellerUI(currentProduct);

                } else {

                    Fragment inventory = new ShowInventory();
                    Bundle args = new Bundle();
                    args.putString("currentUserType", "seller");
                    args.putString("request", getString(R.string.seller_get_all_products_request));

                    ((BaseActivity) getActivity()).redirectToFragment(inventory, args);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void updateSellerUI(final Product currentProduct) {
//        ImageView productImage = getView().findViewById(R.id.imageView);
//        TextView price = getView().findViewById(R.id.productPrice);
//        TextView stocks = getView().findViewById(R.id.stocks);

        ImageView productImage = getView().findViewById(R.id.productImage);
        TextView price = getView().findViewById(R.id.sellerPrice);
        TextView productName = getView().findViewById(R.id.productName);
        TextView soldBy = getView().findViewById(R.id.seller);
        Button modify = getView().findViewById(R.id.modifyInventory);
        Button delete = getView().findViewById(R.id.deleteInventory);
        TextView stocks = getView().findViewById(R.id.stock);

        // product specific details in textbox


        Picasso.get().load(currentProduct.getImageURL()).into(productImage);
        productName.setText(currentProduct.getName());
        soldBy.setText(currentProduct.getSoldBy());
        price.setText(String.format("$%.2f", currentProduct.getPrice()));

        String stocks_str = "Stocks: " + currentProduct.getAvailableStock() + " items";
        stocks.setText(stocks_str);

        // TODO display product specific details in the table below

        final Seller currentSeller = (Seller) (backEnd.getFromPersistentStorage("currentUser"));


        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // TBA


            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TBA

            }
        });


    }
}
