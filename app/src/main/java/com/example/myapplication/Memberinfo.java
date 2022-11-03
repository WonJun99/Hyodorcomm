package com.example.myapplication;

import android.widget.EditText;

public class Memberinfo {
    private String name;
    private String PhoneNumber;
    private String Date;
    private String Address;

    public Memberinfo(String name, String PhoneNumber, String Date, String Address){
        this.name = name;
        this.PhoneNumber = PhoneNumber;
        this.Date = Date;
        this.Address = Address;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getPhoneNumber() {
        return this.PhoneNumber;
    }
    public void setPhoneNumber(String PhoneNumber){
        this.PhoneNumber = PhoneNumber;
    }

    public String getDate() {
        return this.Date;
    }
    public void setDate(String Date){
        this.Date = Date;
    }

    public String getAddress() {
        return this.Address;
    }
    public void setAddress(String Address){
        this.Address = Address;
    }
}
