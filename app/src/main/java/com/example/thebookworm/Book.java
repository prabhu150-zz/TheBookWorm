package com.example.thebookworm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Book extends Product {


    String title, author, genre, publisher;
    int pages;
    Date published;
    String imageURL = "https://firebasestorage.googleapis.com/v0/b/bookworm-cb649.appspot.com/o/profile-pics%2Fdefault.png?alt=media&token=58ed84ff-1040-428d-bfb3-0e1c224693ba";

    public Book(String name, String description, String imageURL, double price, String PID, int stocks) {
        super(name, description, imageURL, price, PID, stocks);
        this.title = name;
        this.imageURL = imageURL;
    }

    // TODO fix image url in superclass
    public Book() {
        super();
    }

    public void setDetails(String author, String genre, String publisher, int pages, String datePublished) throws ParseException {

        this.author = author;
        this.genre = genre;
        this.publisher = publisher;
        this.published = new SimpleDateFormat("MM/dd/yy").parse(datePublished);
        this.pages = pages;
    }

}
