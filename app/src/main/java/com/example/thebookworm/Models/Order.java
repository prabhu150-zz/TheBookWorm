package com.example.thebookworm.Models;

import java.util.List;

public class Order {

    private Buyer customer;

    private List<Product> items;

    private double bill;

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
