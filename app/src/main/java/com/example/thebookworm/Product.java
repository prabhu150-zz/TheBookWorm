package com.example.thebookworm;

public abstract class Product {

    private String name, description, imageURL;
    private double price;
    private int availableStock;
    private int PID;


    public Product(String name, String description, String imageURL, double price, int PID, int availableStock) {
        this.name = name;
        this.description = description;
        this.imageURL = imageURL;
        this.price = price;
        this.PID = PID;
        this.availableStock = availableStock;
    }

}
