package com.example.thebookworm.Models;

import java.util.ArrayList;
import java.util.List;

public class Buyer {
    /*
    Have kept buyer as a concrete class since there is only one type of customer.
     */
    private String userID, name, email, nickname, profilePic = "https://firebasestorage.googleapis.com/v0/b/bookworm-cb649.appspot.com/o/profile-pics%2Fdefault .png?alt=media&token=58ed84ff-1040-428d-bfb3-0e1c224693ba";

    public String getName() {
        return name;
    }

    //    List<Orders> orders;
    List<Product> cart;

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public Buyer(String userID, String name, String email, String nickname) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        cart = new ArrayList<>();
    }

    public Buyer() {
        cart = new ArrayList<>();
    }
//    List<String> reviews;
//    List<Integer> ratings;

    public List<Product> getCart() {
        return cart;
    }

    public String getUserID() {
        return userID;
    }

    public boolean addToCart(Product currentProduct) {

        for (Product curr : cart)
            if (curr.getPID().equals(currentProduct.getPID()))
                return false;

        return cart.add(currentProduct);
    }

    public boolean removeFromCart(String pid) {
        for (Product curr : cart)
            if (curr.getPID().equals(pid)) {
                return cart.remove(curr);
            }
        return false;
    }


    public int cartSize() {
        return cart.size();
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public Double calculateBill() {

        double bill = 0f;

        for (Product curr : cart)
            bill += curr.getPrice();


        return bill;
    }

    public Product getLatestItem() {
        return cart.get(cart.size() - 1);
    }
}
