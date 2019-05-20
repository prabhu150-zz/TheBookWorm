package com.example.thebookworm.Models;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private String orderID;
    private Buyer customer;
    private int numItems;

    private double bill, shippingCosts, estimatedTax, grandTotal;
    private String currentSeller;

    public Order(String orderID, int numItems, double bill, double shippingCosts, double estimatedTax, double grandTotal) {
        this.orderID = orderID;
        this.numItems = numItems;
        this.bill = bill;
        this.shippingCosts = shippingCosts;
        this.estimatedTax = estimatedTax;
        this.grandTotal = grandTotal;
    }

    private List<Product> items;
    private List<String> sellerID;

    public Order() {
        // for firebase
    }


    public Order(String orderID, Buyer customer, List<Product> items, List<String> sellerID) {
        this.orderID = orderID;
        this.customer = customer;
        this.items = items;
        this.sellerID = sellerID;
        bill = 0.0;
        shippingCosts = 0.0875;
        estimatedTax = 0.125;
        grandTotal = 0.0;
        numItems = items.size();
        calculateOrder();
    }


    public Order(String orderID, Buyer customer, List<Product> items, String sellerID) {
        this.orderID = orderID;
        this.customer = customer;
        this.items = items;
        this.currentSeller = sellerID;
        bill = 0.0;
        shippingCosts = 0.0875;
        estimatedTax = 0.125;
        grandTotal = 0.0;
        numItems = items.size();
        calculateOrder();
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public void setItems(List<Product> items) {
        this.items = new ArrayList<>(items);
    }

    public void setSellerList(List<String> sellerID) {
        this.sellerID = new ArrayList<>(sellerID);
    }

    public double getShippingCosts() {
        return shippingCosts;
    }

    public double getEstimatedTax() {
        return estimatedTax;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public int getNumItems() {
        return numItems;
    }

    public double getBill() {
        return bill;
    }

    private void calculateOrder() {

        for (Product item : items)
            bill += item.getPrice();

        grandTotal = bill * (1 + shippingCosts + estimatedTax);

        shippingCosts *= grandTotal;
        estimatedTax *= grandTotal;

    }


    public String getOrderID() {
        return orderID;
    }

    public Buyer getCustomer() {
        return customer;
    }
}
