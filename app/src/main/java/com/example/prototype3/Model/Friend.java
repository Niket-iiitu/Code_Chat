package com.example.prototype3.Model;

public class Friend {
    private String friendCredential;
    private String permissions;

    public Friend(){
        //Note: Empty constructor is required.
    }

    public Friend(String friendCredential, String permissions){
        this.friendCredential=friendCredential;
        this.permissions=permissions;
    }

    public String getFriendCredential() {
        return friendCredential;
    }

    public String getPermissions() {
        return permissions;
    }
}

/*
permission=>
Null-> Only text
Call->Calling
Group->In Group Chat
All->Calling and in Group Chat
 */
