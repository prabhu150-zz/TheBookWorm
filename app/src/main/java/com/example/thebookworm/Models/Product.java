package com.example.thebookworm.Models;

public abstract class Product {

    //TODO remove conflicting/duplicate values from inherited values and make a pt to store an item
    // as its parent and not its children



    String imageURL = "https://firebasestorage.googleapis.com/v0/b/bookworm-cb649.appspot.com/o/profile-pics%2Fdefault.png?alt=media&token=58ed84ff-1040-428d-bfb3-0e1c224693ba";
    private double price;
    private int availableStock;
    private String name, description, PID, type;

    private String sellerName;
    private String sellerID;

    public Product() {
// for firebase
    }

    public Product(String name, String description, String imageURL, double price, String PID, int availableStock, String sellerName, String type, String sellerID) {
        this.name = name;
        this.description = description;
        this.imageURL = imageURL;
        this.price = price;
        this.PID = PID;
        this.availableStock = availableStock;
        this.sellerName = sellerName;
        this.type = type;
        this.sellerID = sellerID;
    }

    public String getPID() {
        return PID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public double getPrice() {
        return price;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getType() {
        return type;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public String getSellerID() {
        return sellerID;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }
}
