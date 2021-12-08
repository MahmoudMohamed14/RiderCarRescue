package com.example.riderremake.Model;

public class RiderInfo {
    private String name;
    private String email, phone ,id ,password,image;


    public RiderInfo() {

    }

    public RiderInfo(String name, String email, String phone, String id, String password, String image) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.id = id;
        this.password = password;
        this.image = image;

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
