package com.example.thebookworm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.thebookworm.Activities.BaseActivity;
import com.example.thebookworm.Activities.LoginActivity;
import com.example.thebookworm.Models.Book;
import com.example.thebookworm.Models.Buyer;
import com.example.thebookworm.Models.Order;
import com.example.thebookworm.Models.Product;
import com.example.thebookworm.Models.Seller;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import io.paperdb.Paper;


public class BackEnd {

    private Context currentActivity;
    private String tag;

    public BackEnd(Context currentActivity, String tag) {
        this.currentActivity = currentActivity;
        this.tag = tag;
        Paper.init(currentActivity);
    }

    public void findCurrentUser() {
        checkSellersList();
    }

    public void logit(String message) {
        Log.d(tag, message);
    }

    public void notifyByToast(String message) {
        Toast.makeText(currentActivity, message, Toast.LENGTH_SHORT).show();
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
                        logit("Its a seller alright " + currentSeller.getName());
                        saveToPersistentStorage("currentUser", currentSeller);
                        String currentUserType = "seller";
                        saveToPersistentStorage("currentUserType", currentUserType);
                        getDashBoard(currentUserType);
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


    public void saveToPersistentStorage(String key, Object value) {
        Paper.book().write(key, value);
        logit("Storing in persistent " + key + ":" + value);
    }


    public String getChildStringVal(DataSnapshot currentChild, String path) {
        String result = currentChild.child(path).getValue().toString();
//        logit("path: " + path + "child exists: " + currentChild.child(path).exists() + " value: " + result);
        return result;
    }

    public Object getFromPersistentStorage(String key) {
        Object value = Paper.book().read(key);
        return value;
    }

    public void deletePersistentStorage(String key) {
        logit("Deleting key for " + key);
        Paper.book().delete(key);
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
                        logit("Its a buyer alright! Name " + currentBuyer.getName());
                        saveToPersistentStorage("currentUser", currentBuyer);
                        saveToPersistentStorage("currentUserType", "buyer");
                        getDashBoard("buyer");
                    }
                } else {
                    logit("Not a buyer either. No-one found");
                    notifyByToast("Email doesn't match any user! Please re-enter");
                    logout();
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
        redirect.putExtra("currentUserType", type);
        redirect.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        currentActivity.startActivity(redirect);
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        deletePersistentStorage("currentUser");
        deletePersistentStorage("currentUserType");
        redirect(LoginActivity.class);
    }


    private void redirect(Class nextActivity) {
        Intent redirect = new Intent(currentActivity, nextActivity);
        redirect.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        currentActivity.startActivity(redirect);
    }


    @NotNull
    public Product fetchProduct(DataSnapshot currentChild, Bundle args) {

        Product currentProduct;

        String productType = args.getString("productType");

        productType = productType.trim().toLowerCase().replaceAll("[^\\w\\s]", "");

        switch (productType) {
            case "book":

                currentProduct = new Book(getChildStringVal(currentChild, "/name/"), getChildStringVal(currentChild, "/description/"), getChildStringVal(currentChild, "/imageURL/"), Double.parseDouble(getChildStringVal(currentChild, "/price/")), getChildStringVal(currentChild, "/pid/"), Integer.parseInt(getChildStringVal(currentChild, "/availableStock/")), getChildStringVal(currentChild, "/sellerName"), getChildStringVal(currentChild, "/type"), getChildStringVal(currentChild, "/sellerID"));
//                String author, String genre, String publisher, int pages, String datePublished


                Log.d("checkURL", currentProduct.getImageURL());

                ((Book) currentProduct).setDetails(getChildStringVal(currentChild, "/author/"), getChildStringVal(currentChild, "/genre"), getChildStringVal(currentChild, "/publisher/"), Integer.parseInt(getChildStringVal(currentChild, "/pages/")), getChildStringVal(currentChild, "/datePublished"));
                return currentProduct;

            default:
                throw new IllegalArgumentException("This product " + productType + " is not yet supported!");
        }
    }


    public void updateBuyeronBackEnd(Buyer currentBuyer, int position) {
        FirebaseDatabase.getInstance().getReference("/users/buyers/" + currentBuyer.getUserID() + "/cart/" + currentBuyer.getLatestItem().getPID()).setValue(currentBuyer.getLatestItem());
    }


    public void removeItemFromCart(String currentProductPID, Buyer currentBuyer) {

        Log.d("removeFromCart", "Removing item: " + currentProductPID + " for user id: " + currentBuyer.getUserID());

        Log.d("removeFromCart", "UserId: " + currentBuyer.getUserID());

        FirebaseDatabase.getInstance().getReference("/users/buyers/" + currentBuyer.getUserID()).child("/cart/" + currentProductPID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                logit("Deleted item from cart!");
            }
        });

        currentBuyer.removeFromCart(currentProductPID);
        saveToPersistentStorage("currentUser", currentBuyer);
    }

    public void removeItemFromInventory(String currentProductPID, String productType) {
        Seller currentSeller = (Seller) getFromPersistentStorage("currentUser");

        DatabaseReference sellerInventoryRef = FirebaseDatabase.getInstance().getReference("/users/sellers/" + currentSeller.getUserID() + "/inventory/" + productType).child(currentProductPID);

        sellerInventoryRef.removeValue();

        removeItemFromMarket(currentProductPID, productType);

        currentSeller.removeFromInventory(currentProductPID);

        saveToPersistentStorage("currentUser", currentSeller);

        Log.d("removeFromCart", "UserId: " + currentSeller.getUserID());

    }

    private void removeItemFromMarket(String currentProductPID, String productType) {


        logit("Removing " + productType + " prod: " + currentProductPID);

        DatabaseReference marketRef = FirebaseDatabase.getInstance().getReference("/market/products/" + productType).child(currentProductPID);


        marketRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                logit("Item removed from database!");
            }
        }).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        logit("Couldnt do it. Reason: " + e.getMessage());
                    }
                }
        );


    }

    @NotNull
    public Product getSpecificProduct(DataSnapshot currentChild, String productType) {

        Product currentProduct;

        productType = productType.trim().toLowerCase().replaceAll("[^\\w\\s]", "");

        logit("About to get specific item!");


        switch (productType) {
            case "book":

                currentProduct = new Book(getChildStringVal(currentChild, "/name/"), getChildStringVal(currentChild, "/description/"), getChildStringVal(currentChild, "/imageURL/"), Double.parseDouble(getChildStringVal(currentChild, "/price/")), getChildStringVal(currentChild, "/pid/"), Integer.parseInt(getChildStringVal(currentChild, "/availableStock/")), getChildStringVal(currentChild, "/sellerName"), getChildStringVal(currentChild, "/type/"),
                        getChildStringVal(currentChild, "/sellerID/"));
//                String author, String genre, String publisher, int pages, String datePublished

                ((Book) currentProduct).setDetails(getChildStringVal(currentChild, "/author/"), getChildStringVal(currentChild, "/genre"), getChildStringVal(currentChild, "/publisher/"), Integer.parseInt(getChildStringVal(currentChild, "/pages/")), getChildStringVal(currentChild, "/datePublished"));

                return currentProduct;

            default:
                throw new IllegalArgumentException("This product " + productType + " is not yet supported!");
        }
    }


    public Order getSpecificOrder(DataSnapshot child) {
        return new Order(getChildStringVal(child, "/orderID"), Integer.parseInt(getChildStringVal(child, "/numItems")), Double.parseDouble(getChildStringVal(child, "/bill")), Double.parseDouble(getChildStringVal(child, "/shippingCosts")), Double.parseDouble(getChildStringVal(child, "/estimatedTax")), Double.parseDouble(getChildStringVal(child, "/grandTotal")));
    }
}