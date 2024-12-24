package com.example.madguardians.ui.staff;

public class HandleEducator {
    private String educatorName;
    private String image;
    private String date;
    private String status;


    public HandleEducator(String educatorName, String image, String date, String status) {
        this.educatorName = educatorName;
        this.image = image;
        this.date = date;
        this.status = status;
    }
    public String getEducatorName() {
        return educatorName;
    }

    public void setEducatorName(String educatorName) {
        this.educatorName = educatorName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
