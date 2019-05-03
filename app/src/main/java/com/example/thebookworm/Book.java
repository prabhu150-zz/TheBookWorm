package com.example.thebookworm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Book extends Product {


    String title, author, genre, publisher;
    int pages;
    Date published;

    public Book(String name, String description, String imageURL, double price, int PID) {
        super(name, description, imageURL, price, PID);
    }

    public void setDetails(String title, String author, String genre, String publisher, int pages, String datePublished) throws ParseException {

        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publisher = publisher;
        this.published = new SimpleDateFormat("d-MMM-yy").parse(datePublished);
        this.pages = pages;
    }

}
