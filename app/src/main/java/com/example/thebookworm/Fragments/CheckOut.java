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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backEnd = new BackEnd(getActivity(), "CheckOutAct#logger");
        findIDs();

    }


    @Override
    public void onStart() {
        super.onStart();
        preProcessing();


        placeYourOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userCombinedOrder();
                sellerSpecificOrder();


            }
        });


    }

    private void sellerSpecificOrder() {

    }

    private void userCombinedOrder() {

        pendingOrder = currentBuyer.placeOrder(sellerIds);

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("users/buyers/" + currentBuyer.getUserID()).child("/orders/");

        String orderID = ordersRef.getKey();

        pendingOrder.setOrderID(orderID);

        ordersRef.child(orderID).setValue(pendingOrder);

    }

    private void preProcessing() {

        currentBuyer = (Buyer) backEnd.getFromPersistentStorage("currentUser");
        cart = new ArrayList<>(currentBuyer.getCart());
        sellerIds = new ArrayList<>();

        for (Product currentProduct : cart) {
            if (!sellerIds.contains(currentProduct.getPID()))
                sellerIds.add(currentProduct.getSoldBy());
        }

        numItems.setText(cart.size());
        shippingCosts.setText(String.format("%.2f", pendingOrder.getShippingCosts()));
        estimatedTax.setText(String.format("%.2f", pendingOrder.getEstimatedTax()));
        orderTotal.setText(String.format("%.2f", pendingOrder.getGrandTotal()));


        autofill();


/*
TODO:

Reupload to have soldBy as ID and not name

Sort by seller ids

Make a fresh order for each seller
    save each fresh order under seller table

Make a combined order for everyone and save in user table



 */
    }

    private void autofill() {


        FirebaseDatabase.getInstance().getReference("/users/buyers/").child(currentBuyer.getUserID()).child("/shipping/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {


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


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // validateInput()


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
        personNameEditTextBilling = getView().findViewById(R.id.fullName_billing_EditText);
        address01EditTextBilling = getView().findViewById(R.id.adress01_billing_EditText);
        address02EditTextBilling = getView().findViewById(R.id.address02_billing_EditText);
        cityEditTextBilling = getView().findViewById(R.id.city_billing_EditText);
        phoneEditTextBilling = getView().findViewById(R.id.phone_billing_EditText);
        stateEditTextBilling = getView().findViewById(R.id.state_billing_EditText);
        zipEditTextBilling = getView().findViewById(R.id.zip_billing_EditText);


        placeYourOrder = getView().findViewById(R.id.place_Order_Button);
    }


}
