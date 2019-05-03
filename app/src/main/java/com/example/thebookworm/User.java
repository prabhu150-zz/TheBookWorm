package com.example.thebookworm;

public class User {

    String userID, name, email, nickname, type;
    String profilePic = "https://firebasestorage.googleapis.com/v0/b/instapost-bb5c4.appspot.com/o/profile-pics%2Favatar-single-360.png?alt=media&token=137ad0f3-aca7-4096-aa95-eeed0a630890";

    public User(String userID, String name, String email, String nickname, String type) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.type = type;
    }

//    List<Orders> orders;
//    List<Books> cartItems;

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
