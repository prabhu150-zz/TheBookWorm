package com.example.thebookworm.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.bookworm.R;
import com.example.thebookworm.BackEnd;
import com.example.thebookworm.Models.Buyer;
import com.example.thebookworm.Models.Order;
import com.example.thebookworm.Models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CheckOut extends Fragment {

    String total;
    Double grandTotal;
    AwesomeValidation awesomeValidation;
    Button placeYourOrder;
    EditText fullName, addressLine1, addressLine2, city, stateEditText, zipEditText, email, phone;
    EditText personNameEditTextBilling, address01EditTextBilling, address02EditTextBilling, cityEditTextBilling, stateEditTextBilling, zipEditTextBilling, phoneEditTextBilling;

    private BackEnd backEnd;
    private Order pendingOrder;
    private Buyer currentBuyer;
    private ArrayList<String> sellerIds;
    private List<Product> cart;

    private TextView numItems, shippingCosts, estimatedTax, orderTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.payment_options, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findIDs();
    }

    @Override
    public void onStart() {
        super.onStart();
        backEnd = new BackEnd(getActivity(), "CheckOutAct#logger");

        preProcessing();

        placeYourOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backEnd.notifyByToast("Order Placed!");

                userCombinedOrder();
                sellerSpecificOrder();
            }
        });


    }

    private void sellerSpecificOrder() {
        DatabaseReference sellerRef = FirebaseDatabase.getInstance().getReference("users/sellers/");

        for (String currSeller : sellerIds) {
            String orderID = sellerRef.push().getKey();
            List<Product> sellerCart = getAllProductsByThisSeller(cart, currSeller);
            Order sellersOrder = new Order(orderID, currentBuyer, sellerCart, currSeller);
            sellerRef.child(currSeller).child("/orders/" + sellersOrder.getOrderID()).setValue(sellersOrder);
            sellerRef.child(currSeller).child("/customers/" + sellersOrder.getCustomer().getUserID()).setValue(sellersOrder.getCustomer());
        }

    }


    private List<Product> getAllProductsByThisSeller(List<Product> cart, String sellerID) {
        List<Product> sellersGoods = new ArrayList<>();

        for (Product curr : cart) {
            if (curr.getSellerID().equals(sellerID))
                sellersGoods.add(curr);
        }

        return sellersGoods;
    }


    private void userCombinedOrder() {

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("users/buyers/" + currentBuyer.getUserID());

        String orderID = ordersRef.push().getKey();

        backEnd.logit("Cart size: " + cart.size());
        pendingOrder.setOrderID(orderID);
        pendingOrder.setItems(cart);
        backEnd.logit("Seller list size: " + sellerIds.size());
        pendingOrder.setSellerList(sellerIds);

        backEnd.logit("Adding user specific order! " + pendingOrder.getGrandTotal());
        ordersRef.child("/orders/" + orderID).setValue(pendingOrder);

        ordersRef.child("/orders/" + orderID + "/items/").setValue(cart);
        ordersRef.child("/orders/" + orderID + "/sellers/").setValue(sellerIds);

    }

    private void preProcessing() {

        currentBuyer = (Buyer) backEnd.getFromPersistentStorage("currentUser");

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("users/buyers/" + currentBuyer.getUserID());

        String orderID = ordersRef.push().getKey();


        cart = new ArrayList<>(currentBuyer.getCart());
        sellerIds = new ArrayList<>();

        for (Product currentProduct : cart) {
            if (!sellerIds.contains(currentProduct.getPID()))
                sellerIds.add(currentProduct.getSellerID());
        }

        pendingOrder = new Order(orderID, currentBuyer, cart, sellerIds);
        resetUI();


/*
TODO:

Reupload to have soldBy as ID and not name
Sort by seller ids
Make a fresh order for each seller
    save each fresh order under seller table
Make a combined order for everyone and save in user table
 */
    }

    private void resetUI() {
        numItems.setText(String.valueOf(pendingOrder.getNumItems()));
        shippingCosts.setText(String.format("%.2f", pendingOrder.getShippingCosts()));
        estimatedTax.setText(String.format("%.2f", pendingOrder.getEstimatedTax()));
        orderTotal.setText(String.format("%.2f", pendingOrder.getGrandTotal()));

        autofill();
    }

    private void autofill() {

        FirebaseDatabase.getInstance().getReference("/users/buyers/").child(currentBuyer.getUserID()).child("/shipping/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    fullName.setText(getValueIfExists(dataSnapshot.child("fullName").getValue()));
                    addressLine1.setText(getValueIfExists(dataSnapshot.child("addressLine1").getValue()));
                    addressLine2.setText(getValueIfExists(dataSnapshot.child("addressLine2").getValue()));
                    city.setText(getValueIfExists(dataSnapshot.child("city").getValue()));
                    email.setText(getValueIfExists(dataSnapshot.child("email").getValue()));
                    phone.setText(getValueIfExists(dataSnapshot.child("phone").getValue()));
                    stateEditText.setText(getValueIfExists(dataSnapshot.child("state").getValue()));
                    zipEditText.setText(getValueIfExists(dataSnapshot.child("zip").getValue()));

                } else {
                    backEnd.logit("Couldn't find any info on user!");
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private String getValueIfExists(Object object) {
        if (object == null)
            return "";
        return String.valueOf(object);
    }


    private void findIDs() {
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        fullName = getView().findViewById(R.id.fullName);
        addressLine1 = getView().findViewById(R.id.address1);
        addressLine2 = getView().findViewById(R.id.address2);
        city = getView().findViewById(R.id.city);
        email = getView().findViewById(R.id.email);
        phone = getView().findViewById(R.id.phone);
        stateEditText = getView().findViewById(R.id.state);
        zipEditText = getView().findViewById(R.id.zipCode);
        placeYourOrder = getView().findViewById(R.id.place_Order_Button);
        numItems = getView().findViewById(R.id.itemsCountOrder);
        shippingCosts = getView().findViewById(R.id.shippingCosts);
        estimatedTax = getView().findViewById(R.id.totalTax);
        orderTotal = getView().findViewById(R.id.orderTotal);
    }


}
