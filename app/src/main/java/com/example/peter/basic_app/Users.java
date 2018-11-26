package com.example.peter.basic_app;

public class Users {

    String name;
    String membership;
    String startdate;
    String key;


    public Users(String name, String membership, String startdate, String key){
        this.name = name;
        this.membership = membership;
        this.startdate = startdate;
        this.key = key;

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


}
