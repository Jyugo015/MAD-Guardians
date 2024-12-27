package com.example.madguardians.ui.consult.model_lo;



import com.google.firebase.Timestamp;

public class TimeSlotModel {
    Timestamp timestamp;
    String setterName;
    String time;
    String getterName;
    String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(boolean bookStatus) {
        this.bookStatus = bookStatus;
    }

    boolean bookStatus;

    public TimeSlotModel() {
    }

    public TimeSlotModel(Timestamp timestamp, String setterName, String time, String getterName, String date, boolean bookStatus) {
        this.timestamp = timestamp;
        this.setterName = setterName;
        this.time = time;
        this.getterName = getterName;
        this.date = date;
        this.bookStatus = bookStatus;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getSetterName() {
        return setterName;
    }

    public void setSetterName(String setterName) {
        this.setterName = setterName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getGetterName() {
        return getterName;
    }

    public void setGetterName(String getterName) {
        this.getterName = getterName;
    }
}
