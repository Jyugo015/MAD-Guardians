package com.example.madguardians.ui.consult.model_lo;

public class CounselorModel {
    String name;
    String skill;
    String experience;
    int image;
    private String userId;
    boolean online;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public CounselorModel(String name, String skill, int image, String experience, String userId, boolean online) {
        this.name = name;
        this.skill = skill;
        this.experience = experience;
        this.image = image;
        this.userId = userId;
        this.online = online;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public CounselorModel() {
    }


}
