package com.vmh.manhhung.parkinglotsone;

import java.io.Serializable;

public class Customer extends UserId implements Serializable{
    private String fullname;
    private String email;
    private String phoneNumber;
    private String uri;
    private String birthDay;
    private String passWord;

    public Customer(){
        //ham khoi tao dac biet de doc du lieu xuong
    }

    public Customer(String fullname, String email, String phoneNumber, String uri, String birthDay) {
        this.fullname = fullname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.uri = uri;
        this.birthDay = birthDay;
    }

    public Customer(String fullname, String email, String phoneNumber, String uri, String birthDay, String passWord) {
        this.fullname = fullname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.uri = uri;
        this.birthDay = birthDay;
        this.passWord = passWord;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
