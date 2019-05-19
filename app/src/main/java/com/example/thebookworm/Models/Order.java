package com.example.thebookworm.Models;

import java.util.ArrayList;
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

    public Order(Buyer customer, List<Product> items, List<String> sellerID) {
        this.customer = customer;
        this.items = items;
        this.sellerID = sellerID;
    }

    public Order(Buyer currentBuyer, ArrayList<Product> items, double bill, double shippingCosts, double estimatedTax, double grandTotal, List<String> sellerIDs) {

    }

    public double getShippingCosts() {
        return shippingCosts;
    }

    public void setShippingCosts(double shippingCosts) {
        this.shippingCosts = shippingCosts;
    }

    public double getEstimatedTax() {
        return estimatedTax;
    }

    public void setEstimatedTax(double estimatedTax) {
        this.estimatedTax = estimatedTax;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public void setDetails() {

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


    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }
}
