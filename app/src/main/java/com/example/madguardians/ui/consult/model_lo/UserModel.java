package com.example.madguardians.ui.consult.model_lo;

import com.google.firebase.Timestamp;

public class UserModel {
    private String phone;
    private String name;
    private Timestamp createdTimestamp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String userId;
    private String role;
    private String status;
    private Timestamp lastActive;

    public UserModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }



    public String getStatus() {
        return status;
    }

    public UserModel(String name, String phone, Timestamp createdTimestamp, String userId, String role, String status, Timestamp lastActive) {
        this.name = name;
        this.phone = phone;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
        this.role = role;
        this.status = status;
        this.lastActive = lastActive;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getLastActive() {
        return lastActive;
    }

    public void setLastActive(Timestamp lastActive) {
        this.lastActive = lastActive;
    }

    public String getPhone() {
        return phone;
    }


    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }


}
