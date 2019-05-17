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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookworm.R;
import com.example.thebookworm.BackEnd;
import com.example.thebookworm.Models.Book;
import com.example.thebookworm.Models.Buyer;
import com.example.thebookworm.Models.Product;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewCart extends Fragment implements View.OnClickListener {


    // BUYER SPECIFIC USECASE
    private final String tag = "ViewCart";
    private BackEnd backEnd;

    private Button checkOut;
    private TextView emptyCartAlert;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_cart, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        backEnd = new BackEnd(getActivity(), tag);
        getCartItems();
    }

    private void getCartItems() {

        final TextView emptyCartsAlert = getView().findViewById(R.id.emptyCartAlert);

        final Button placeOrder = getView().findViewById(R.id.placeOrder);


        // TODO fix empty cart bug
        resetUI(emptyCartsAlert, placeOrder, View.GONE, View.VISIBLE);

        final Buyer currentBuyer = (Buyer) backEnd.getFromPersistentStorage("currentUser");
        final GroupAdapter<ViewHolder> adapter = new GroupAdapter<>();
        final RecyclerView recyclerView = getView().findViewById(R.id.shopping_cart);
        final List<CartItemRow> cartItemList = new ArrayList<>();

//        checkIfEmpty(placeOrder,emptyCartsAlert,recyclerView);


        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("/users/buyers/" + currentBuyer.getUserID() + "/cart");

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    List<Product> cartProducts = new ArrayList<>();

                    for (DataSnapshot currItem : dataSnapshot.getChildren()) {
                        Product current = currItem.getValue(Book.class);
                        current.setImageURL(currItem.child("/imageURL").getValue().toString());
                        current.setPID(currItem.child("/pid").getValue().toString());
                        cartProducts.add(current);
                        Log.d("VE", "onDataChange: " + current.getImageURL());
                    }

                    Collections.reverse(cartProducts);

                    for (Product cartItem : cartProducts)
                        cartItemList.add(new CartItemRow(cartItem, backEnd, adapter));


                    for (CartItemRow cartItem : cartItemList) {
                        adapter.add(cartItem);
                    }


                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    recyclerView.setAdapter(adapter);


                } else {
                    backEnd.notifyByToast("No items in cart!");
                    currentBuyer.getCart().clear();
                    resetUI(emptyCartsAlert, placeOrder, View.VISIBLE, View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cartRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("CartListener", "Child Event Added");
                checkIfEmpty(placeOrder, emptyCartsAlert, recyclerView);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d("CartListener", "Child Event Removed!");
                checkIfEmpty(placeOrder, emptyCartsAlert, recyclerView);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void resetUI(TextView emptyCartsAlert, Button placeOrder, int visible, int gone) {
        placeOrder.setVisibility(visible);
        emptyCartsAlert.setVisibility(gone);
    }

    private void checkIfEmpty(Button placeOrder, TextView emptyCartsAlert, RecyclerView recyclerView) {
        // TODO buggy code
//        if (recyclerView.getChildCount() == 0)
//            resetUI(emptyCartsAlert, placeOrder, View.GONE, View.VISIBLE);
//        else
//            resetUI(emptyCartsAlert, placeOrder, View.VISIBLE, View.GONE);

    }


    private void updateUI(Buyer currentBuyer) {
        TextView bill = getView().findViewById(R.id.bill);
        TextView numItems = getView().findViewById(R.id.numItems);

        bill.setText(String.format("%.2f", currentBuyer.calculateBill()));
        numItems.setText(String.valueOf(currentBuyer.cartSize()));
    }


    @Override
    public void onClick(View v) {
        backEnd.notifyByToast("Clicked: " + v.getId());
    }
}

class CartItemRow extends Item<ViewHolder> {

    Product currentProduct;
    BackEnd backEnd;
    GroupAdapter<ViewHolder> adapter;


    // TODO finish this later

    public CartItemRow(Product currentProduct, BackEnd backEnd, GroupAdapter<ViewHolder> adapter) {
        this.currentProduct = currentProduct;
        this.backEnd = backEnd;
        this.adapter = adapter;
    }


    @Override
    public int getLayout() {
        return R.layout.product_catalog_row;
    }

    @Override
    public void bind(@NonNull ViewHolder viewHolder, int position) {
        TextView productName = viewHolder.itemView.findViewById(R.id.productName);
        TextView productPrice = viewHolder.itemView.findViewById(R.id.sellerPrice);
        ImageView productImage = viewHolder.itemView.findViewById(R.id.productImage);
        TextView productStock = viewHolder.itemView.findViewById(R.id.stock);
        TextView productSeller = viewHolder.itemView.findViewById(R.id.seller);
        TextView productId = viewHolder.itemView.findViewById(R.id.productID);
        Button removeFromCart = viewHolder.itemView.findViewById(R.id.removeFromCart);


        final CartItemRow currRow = this;

        Picasso.get().load(currentProduct.getImageURL()).into(productImage);
        productName.setText(currentProduct.getName());
        productPrice.setText(String.format("$%.2f", currentProduct.getPrice()));
        productStock.setText(String.valueOf(currentProduct.getAvailableStock()));
        productSeller.setText(currentProduct.getSoldBy());
        productId.setText(currentProduct.getPID());
        removeFromCart.setVisibility(View.VISIBLE);

        // TODO pulled this out app:layout_behavior=""

        removeFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("removeFromCart", "onClick: " + currentProduct.getPID());
                backEnd.removeItemFromCart(currentProduct.getPID());
                adapter.remove(currRow);
                notifyChanged();

            }
        });
    }
}