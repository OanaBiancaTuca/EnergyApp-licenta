package com.example.energyapp.classes;

public class User {
   private  String uid;
   private String email;
   private String nume;
   private long timestamp;
   private String costKWh;
    public User(){

    }
    public User(String uid, String email, String nume, long timestamp) {
        this.uid = uid;
        this.email = email;
        this.nume = nume;
        this.timestamp = timestamp;
        this.costKWh="";
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public String getCostKWh() {
        return costKWh;
    }

    public void setCostKWh(String costKWh) {
        this.costKWh = costKWh;
    }


}
