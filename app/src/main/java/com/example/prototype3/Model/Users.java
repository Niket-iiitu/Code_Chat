package com.example.prototype3.Model;

public class Users { //Saving ID
    private String id;
    private String username;
    private String imageURL;
    private String phoneNumber;
    private String status;

    //----------------------------------------------------------------------------------------------Constructor

    public Users(){
    }

    public Users(String id,String username,String imageURL,String phoneNumber, String status){
        this.id=id;
        this.imageURL=imageURL;
        this.phoneNumber=phoneNumber;
        this.username=username;
        this.status=status;
    }

    //----------------------------------------------------------------------------------------------Getters ans Setters

    public String getId() {
        return id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }
}
