package com.example.thebookworm;

public class Buyer {
    /*
    Have kept buyer as a concrete class since there is only one type of customer.
     */


    String userID, name, email, nickname;
    String profilePic = "https://firebasestorage.googleapis.com/v0/b/instapost-bb5c4.appspot.com/o/profile-pics%2Favatar-single-360.png?alt=media&token=137ad0f3-aca7-4096-aa95-eeed0a630890";

//    List<Orders> orders;
//    List<Product> cartItems;

//    List<String> reviews;
//    List<Integer> ratings;


    public Buyer(String userID, String name, String email, String nickname) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.nickname = nickname;
    }

    public Buyer() {
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
