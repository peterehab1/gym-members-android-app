package com.example.peter.basic_app;

import android.text.Editable;

public class Users {

    String name;
    String membership;
    String startdate;
    String key;
    String notes;
    String image;


    public Users(String name, String membership, String startdate, String key, String notes, String image){
        this.name = name;
        this.membership = membership;
        this.startdate = startdate;
        this.key = key;
        this.notes = notes;
        this.image = image;

    }

    public Users(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMembership() {
        return membership;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNotes(){
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImage(){
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
