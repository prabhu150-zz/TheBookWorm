package com.example.thebookworm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.thebookworm.Activities.BaseActivity;
import com.example.thebookworm.Activities.LoginActivity;
import com.example.thebookworm.Models.Buyer;
import com.example.thebookworm.Models.Seller;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class BackEnd {

    Context currentActivity;
    String tag;

    public BackEnd(Context currentActivity, String tag) {
        this.currentActivity = currentActivity;
        this.tag = tag;
    }

    public void findCurrentUser() {
        checkSellersList();
    }

    public void logit(String message) {
        Log.d(tag, message);
    }

    public void notifyByToast(String message) {
        Toast.makeText(currentActivity, message, Toast.LENGTH_LONG).show();
    }

    private void checkSellersList() {

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Query query = FirebaseDatabase.getInstance().getReference("/users/sellers/").orderByChild("email").equalTo(email);

        logit("Retrieving users from seller's database");

        ValueEventListener eventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot currentTask : dataSnapshot.getChildren()) {
                        Seller currentSeller = new Seller(currentTask.child("userID").getValue().toString(), currentTask.child("name").getValue().toString(), currentTask.child("email").getValue().toString());
                        currentSeller.setProfilePic(currentTask.child("profilePic").getValue().toString());
                        logit("Seller profile pic set as: " + currentSeller);
                        saveToPersistentStorage("currentUser", currentSeller);
                        getDashBoard("Seller");
                    }
                } else {
                    logit("Its probably a buyer. No-one from sellers found!");
                    checkBuyersList();
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        query.addListenerForSingleValueEvent(eventListener);
    }

    public Seller getSeller(DataSnapshot currentTask, String path) {
        Seller currentSeller = currentTask.child(path).getValue(Seller.class);
        logit("Seller for curr product " + currentSeller.getName());
        return currentSeller;
    }


    public void saveToPersistentStorage(String key, Object value) {
        Paper.book().write(key, value);
    }


    public String getChildStringVal(DataSnapshot currentChild, String path) {

        String result = currentChild.child(path).getValue().toString();
        logit("path: " + path + "child exists: " + currentChild.child(path).exists() + " value: " + result);
        return result;
    }

    public Object getFromPersistentStorage(String key) {
        return Paper.book().read(key);
    }

    private void checkBuyersList() {
        final String emailText = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Query query = FirebaseDatabase.getInstance().getReference("/users/buyers/").orderByChild("email").equalTo(emailText);

        logit("Retrieving buyer from realtime database");

        ValueEventListener eventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot currentTask : dataSnapshot.getChildren()) {
//                        Buyer currentBuyer = currentTask.getValue(Buyer.class);

                        Buyer currentBuyer = new Buyer(currentTask.child("userID").getValue().toString(), currentTask.child("name").getValue().toString(), currentTask.child("email").getValue().toString(), currentTask.child("nickname").getValue().toString());

                        currentBuyer.setProfilePic(currentTask.child("profilePic").getValue().toString());
                        saveToPersistentStorage("currentUser", currentBuyer);
                        getDashBoard("Buyer");
                    }
                } else {
                    logit("Not a buyer either. No-one found");
                    notifyByToast("Email doesn't match any user! Please re-enter");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                logit("Error :" + databaseError.getMessage());
            }
        };

        query.addListenerForSingleValueEvent(eventListener);
    }

    private void getDashBoard(String type) {
        Intent redirect = new Intent(currentActivity, BaseActivity.class);
        redirect.putExtra("userType", type);
        redirect.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        currentActivity.startActivity(redirect);
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        redirect(LoginActivity.class);
    }

    private void redirect(Class nextActivity) {
        Intent redirect = new Intent(currentActivity, nextActivity);
        redirect.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        currentActivity.startActivity(redirect);

    }

    private void loadInventoryFromBackEnd(final Seller currentSeller) {

        Query selectAllProducts = FirebaseDatabase.getInstance().getReference("/users/sellers/inventory/");

        final DatabaseReference marketRef = FirebaseDatabase.getInstance().getReference("market/products/");

        logit("Loading all products from seller's current inventory to seller model");

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    List<String> productIds = new ArrayList<>();

                    for (DataSnapshot currentChild : dataSnapshot.getChildren())
                        productIds.add(currentChild.getValue().toString());


                    for (String productId : productIds) {
//                        DatabaseReference productRef = marketRef.child(productId);
//                        Product product = new Product(productRef.child("name").toString(), productRef.child("description"), productRef.child("imageURL"))


//                        currentSeller.inventory.add();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


    }

}