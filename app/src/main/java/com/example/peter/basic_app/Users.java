package com.example.peter.basic_app;

import android.text.Editable;

public class Users {

    String name;
    String membership;
    String startdate;
    String key;
    String leftmoney;


    public Users(String name, String membership, String startdate, String key, String leftmoney){
        this.name = name;
        this.membership = membership;
        this.startdate = startdate;
        this.key = key;
        this.leftmoney = leftmoney;

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

    public String getLeftmoney(){
        return leftmoney;
    }

    public void setLeftmoney(String leftmoney) {
        this.leftmoney = leftmoney;
    }
}
