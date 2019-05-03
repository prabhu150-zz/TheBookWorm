package com.example.thebookworm;

public class Buyer {
    /*
    Have kept buyer as a concrete class since there is only one type of customer.
     */


    String userID, name, email, nickname;
    String profilePic = "https://firebasestorage.googleapis.com/v0/b/bookworm-cb649.appspot.com/o/profile-pics%2Fdefault.png?alt=media&token=58ed84ff-1040-428d-bfb3-0e1c224693ba";

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
