package com.unicyb.shaurmago.models;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.net.URI;

//UNUSED
public class UserModel {
    private String id;
    private String name;
    private String email;
    private URI photoURI;

    @Exclude
    private Bitmap img;

    public UserModel(String uid, String displayName, String email, Uri photoUrl) {
    }


    public UserModel(String id, String name, String email, URI photoURI) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.photoURI = photoURI;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }
}
