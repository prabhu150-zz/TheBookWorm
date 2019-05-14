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
import com.example.thebookworm.BackEnd;
import com.example.thebookworm.Models.Buyer;
import com.example.thebookworm.Models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;

public class ProductDescription extends Fragment {


    BackEnd singleton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.product_description, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();
        Paper.init(getActivity());
        singleton = new BackEnd(getActivity(), "product_description");
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


        FirebaseDatabase.getInstance().getReference("market/products/" + productType).child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Product currentProduct = singleton.fetchProduct(dataSnapshot, arguments);

                    Log.d("checkUrl", "onDataChange: " + currentProduct.getImageURL());
                    updateBuyersUI(currentProduct);

                } else {


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void updateBuyersUI(final Product currentProduct) {
//        ImageView productImage = getView().findViewById(R.id.imageView);
//        TextView price = getView().findViewById(R.id.productPrice);
//        TextView stocks = getView().findViewById(R.id.stocks);

        ImageView productImage = getView().findViewById(R.id.productImage);
        TextView price = getView().findViewById(R.id.sellerPrice);
        TextView productName = getView().findViewById(R.id.productName);
        TextView soldBy = getView().findViewById(R.id.seller);
        Button addToCart = getView().findViewById(R.id.cartslashmodify);
        Button buyNow = getView().findViewById(R.id.buyslashdelete);
        TextView stocks = getView().findViewById(R.id.stock);


        buyNow.setText("Buy Now!");
        addToCart.setText("Add To Cart!");


        Picasso.get().load(currentProduct.getImageURL()).into(productImage);
        productName.setText(currentProduct.getName());
        soldBy.setText(currentProduct.getSoldBy());
        price.setText(String.format("$%.2f", currentProduct.getPrice()));

        String stocks_str = "Stocks: " + currentProduct.getAvailableStock() + " items";
        stocks.setText(stocks_str);

        // TODO display product specific details in the table below

        final Buyer currentBuyer = (Buyer) (singleton.getFromPersistentStorage("currentUser"));

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.d("AddToCart", "onClick: ");
                boolean status = currentBuyer.addToCart(currentProduct);


                if (status) {
                    singleton.notifyByToast("Items in cart: " + currentBuyer.cartSize());
                    singleton.saveToPersistentStorage("currentUser", currentBuyer);

                    Log.d("lastUrl", "Latest Product: " + currentBuyer.getCart().get(currentBuyer.cartSize() - 1).getImageURL());
                    singleton.updateBuyeronBackEnd(currentBuyer, currentBuyer.cartSize());
                } else {
                    singleton.notifyByToast("Couldn't add to cart!");
                }

            }
        });


        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add to cart first
                // redirect to orders
            }
        });



    }
}
