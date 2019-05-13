package com.example.thebookworm.Models;

public abstract class Product {

    String imageURL = "https://firebasestorage.googleapis.com/v0/b/bookworm-cb649.appspot.com/o/profile-pics%2Fdefault.png?alt=media&token=58ed84ff-1040-428d-bfb3-0e1c224693ba";
    private double price;
    private int availableStock;
    private String name, description, PID;

    private String soldBy;

    public Product() {
// for firebase
    }

    public Product(String name, String description, String imageURL, double price, String PID, int availableStock, String soldBy) {
        this.name = name;
        this.description = description;
        this.imageURL = imageURL;
        this.price = price;
        this.PID = PID;
        this.availableStock = availableStock;
        this.soldBy = soldBy;
    }

    public String getPID() {
        return PID;
    }

    public String getImageURL() {
        return imageURL;
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

    public String getSoldBy() {
        return soldBy;
    }

}
