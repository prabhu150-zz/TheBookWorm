package com.example.thebookworm.Models;

public class Book extends Product {

    private String title, author, genre, publisher, datePublished;
    private int pages;
    //    Date published; too complex not needed
    private String imageURL = "https://firebasestorage.googleapis.com/v0/b/bookworm-cb649.appspot.com/o/profile-pics%2Fdefault.png?alt=media&token=58ed84ff-1040-428d-bfb3-0e1c224693ba";

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public int getPages() {
        return pages;
    }

    @Override
    public String getImageURL() {
        return imageURL;
    }


    public Book(String name, String description, String imageURL, double price, String PID, int stocks, String sellerName, String type, String sellerID) {
        super(name, description, imageURL, price, PID, stocks, sellerName, type, sellerID);
        this.title = name;
        this.imageURL = imageURL;
    }

    // TODO fix image url in superclass
    public Book() {
        super();
    }

    public void setDetails(String author, String genre, String publisher, int pages, String datePublished) {

        {
            this.author = author;
            this.genre = genre;
            this.publisher = publisher;
//            this.published = new SimpleDateFormat("MM/dd/yy").parse(datePublished);
            this.datePublished = datePublished;
            this.pages = pages;
        }

    }

}
