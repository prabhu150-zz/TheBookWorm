package com.example.thebookworm.Models;

import java.util.List;

public class Order {

    private String orderID;
    private Buyer customer;
    private List<Product> items;
    private double bill;
    private double shippingCosts;
    private double estimatedTax;
    private double grandTotal;
    private List<String> sellerID;

    public Order(Buyer customer, List<Product> items, double bill, double shippingCosts, double estimatedTax, double grandTotal, List<String> sellerID) {
        this.customer = customer;
        this.items = items;
        this.bill = bill;
        this.shippingCosts = shippingCosts;
        this.estimatedTax = estimatedTax;
        this.grandTotal = grandTotal;
        this.sellerID = sellerID;
    }

    public Buyer getCustomer() {
        return customer;
    }

    public void setCustomer(Buyer customer) {
        this.customer = customer;
    }

    public double getBill() {
        return bill;
    }

    public void setBill(double bill) {
        this.bill = bill;
    }


}
