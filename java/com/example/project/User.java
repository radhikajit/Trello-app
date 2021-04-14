package com.example.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class User {
    String username;
    String email;
    String password;
    String profession;
    String phone;
    public User(){
    }
    public User(String username, String email,String password,String profession,String phone)
    {
        this.username=username;
        this.email=email;
        this.password=password;
        this.profession=profession;
        this.phone=phone;
    }
    public String getusername(){
        return username;
    }
    public void setUsername(String name){
        username=name;
    }
    public void setEmail(String email){this.email=email;}
    public void setPassword(String password){this.password=password;}
    public void setProfession(String profession){this.profession=profession;}
    public String getemail(){
        return email;
    }
    public String getpassword(){
        return password;
    }
    public String getprofession(){
        return profession;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}
