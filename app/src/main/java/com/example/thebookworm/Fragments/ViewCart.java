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
import com.example.thebookworm.Activities.BaseActivity;
import com.example.thebookworm.BackEnd;
import com.example.thebookworm.Models.Book;
import com.example.thebookworm.Models.Buyer;
import com.example.thebookworm.Models.Product;
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

public class ViewCart extends Fragment {


    // BUYER SPECIFIC USECASE

    private final String tag = "ViewCart";
    private RecyclerView recyclerView;
    private BackEnd backEnd;
    private Button placeOrder;
    private TextView emptyCartAlert;
    private Buyer currentBuyer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_cart, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backEnd = new BackEnd(getActivity(), tag);
        currentBuyer = (Buyer) backEnd.getFromPersistentStorage("currentUser");
    }

    private void findIds() {
        placeOrder = getView().findViewById(R.id.placeOrder);
        emptyCartAlert = getView().findViewById(R.id.emptyCartAlert);
    }

    @Override
    public void onStart() {
        super.onStart();
        findIds();


        recyclerView = getView().findViewById(R.id.shopping_cart);
        currentBuyer = (Buyer) backEnd.getFromPersistentStorage("currentUser");
        placeOrder = getView().findViewById(R.id.placeOrder);


        getCartItems();
        resetUI();

        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                resetUI();
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                resetUI();
            }
        });
    }

    private void resetUI() {


        currentBuyer = (Buyer) backEnd.getFromPersistentStorage("currentUser");
        if (currentBuyer.cartSize() > 0) {
            backEnd.logit("Items in cart: " + currentBuyer.cartSize());
            placeOrder.setVisibility(View.VISIBLE);
            emptyCartAlert.setVisibility(View.INVISIBLE);
        } else {
            placeOrder.setVisibility(View.GONE);
            emptyCartAlert.setVisibility(View.VISIBLE);
        }
    }

    private void getCartItems() {

        final TextView emptyCartsAlert = getView().findViewById(R.id.emptyCartAlert);

        final Buyer currentBuyer = (Buyer) backEnd.getFromPersistentStorage("currentUser");
        final GroupAdapter<ViewHolder> adapter = new GroupAdapter<>();
        final List<CartItemRow> cartItemList = new ArrayList<>();


        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentBuyer.cartSize() == 0)
                    backEnd.notifyByToast("No items to checkout!");
                else {
                    Fragment paymentInfo = new CheckOut();
                    Bundle args = new Bundle();
                    args.putString("currentUserType", "buyer");
                    args.putString("request", getResources().getString(R.string.proceed_to_checkout));
                    backEnd.saveToPersistentStorage("currentUser", currentBuyer);
                    ((BaseActivity) getActivity()).redirectToFragment(paymentInfo, args);
                }

            }
        });


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
                        currentBuyer.addToCart(current);
                        Log.d("VE", "onDataChange: " + current.getImageURL());
                    }

                    Collections.reverse(cartProducts);

                    currentBuyer.setCart(cartProducts);

                    for (Product cartItem : cartProducts)
                        cartItemList.add(new CartItemRow(cartItem, backEnd, adapter));


                    for (CartItemRow cartItem : cartItemList) {
                        adapter.add(cartItem);
                    }


                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    recyclerView.setAdapter(adapter);

                    backEnd.saveToPersistentStorage("currentUser", currentBuyer);

                    resetUI();

                } else {
                    backEnd.notifyByToast("No items in cart!");
                    currentBuyer.getCart().clear();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

class CartItemRow extends Item<ViewHolder> {

    Product currentProduct;
    BackEnd backEnd;
    GroupAdapter<ViewHolder> adapter;

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

        backEnd.logit(currentProduct.getImageURL());
        Picasso.get().load(currentProduct.getImageURL()).into(productImage);

        productName.setText(currentProduct.getName());
        productPrice.setText(String.format("$%.2f", currentProduct.getPrice()));
        productStock.setText(String.valueOf(currentProduct.getAvailableStock()));
        productSeller.setText(currentProduct.getSellerName());
        productId.setText(currentProduct.getPID());
        removeFromCart.setVisibility(View.VISIBLE);


        removeFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("removeFromCart", "onClick: " + currentProduct.getPID());
                Buyer buyer = (Buyer) backEnd.getFromPersistentStorage("currentUser");
                backEnd.removeItemFromCart(currentProduct.getPID(), buyer);
                adapter.remove(currRow);
                notifyChanged();

            }
        });
    }
}