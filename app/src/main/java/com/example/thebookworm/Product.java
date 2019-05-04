package com.example.thebookworm;

public abstract class Product {

    String imageURL = "https://firebasestorage.googleapis.com/v0/b/bookworm-cb649.appspot.com/o/profile-pics%2Fdefault.png?alt=media&token=58ed84ff-1040-428d-bfb3-0e1c224693ba";
    private double price;
    private int availableStock;
    private String name, description, PID;

    public Product() {

    }

    public String getPID() {
        return PID;
    }

    public Product(String name, String description, String imageURL, double price, String PID, int availableStock) {
        this.name = name;
        this.description = description;
//        this.imageURL = imageURL;
        this.price = price;
        this.PID = PID;
        this.availableStock = availableStock;
    }

}
