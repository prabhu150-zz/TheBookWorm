package com.example.thebookworm.Models;

import java.util.ArrayList;
import java.util.List;

public class Buyer {
    /*
    Have kept buyer as a concrete class since there is only one type of customer.
     */
    private String userID, name, email, nickname, profilePic = "https://firebasestorage.googleapis.com/v0/b/bookworm-cb649.appspot.com/o/profile-pics%2Fdefault .png?alt=media&token=58ed84ff-1040-428d-bfb3-0e1c224693ba";

    List<Order> orders;
    List<Product> cart;
    private String fullName, addressLine1, addressLine2, city, state, zipCode, phoneNumber;
    private String nameOnCard;

    public void setShippingDetails(String fullName, String addressLine1, String addressLine2, String city, String state, String zipCode, String phoneNumber) {
        this.fullName = fullName;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public double getBill() {
        double bill = 0.0;
        for (Product curr : cart) {
            bill += curr.getPrice();
        }

        return bill;
    }


    public void setName(String name) {
        this.name = name;
    }

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
        orders = new ArrayList<>();
    }

    public Buyer() {

        cart = new ArrayList<>();
        orders = new ArrayList<>();
    }

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

    public void removeFromCart(String pid) {

        int index = 0;

        for (Product curr : cart) {
            if (curr.getPID().equals(pid))
                break;

            index++;
        }
        if (!cart.isEmpty())
            cart.remove(index);
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
        if (cartSize() > 0)
            return cart.get(cart.size() - 1);

        return null;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }




    public List<Order> getOrders() {
        return orders;
    }

    public boolean checkInCart(String pid) {

        for (Product curr : cart)
            if (curr.getPID().equals(pid))
                return true;
        return false;
    }

    public void setCart(List<Product> cartProducts) {
        this.cart = new ArrayList<>(cartProducts);
    }

    public void addOrder(Order order) {
        orders.add(order);
    }
}
