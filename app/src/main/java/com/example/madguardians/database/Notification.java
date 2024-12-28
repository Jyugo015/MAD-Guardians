package com.example.madguardians.database;

import com.google.firebase.Timestamp;
public class Notification {
    private String notificationId;
    private String userId;
    private String message;
    private Timestamp deliveredTime;
    private Timestamp readTime;

    public Notification() {}

    public Notification(String notificationId, String userId, String message, Timestamp deliveredTime, Timestamp readTime) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.message = message;
        this.deliveredTime = deliveredTime;
        this.readTime = readTime;
    }

    // Getter and Setter
    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getDeliveredTime() {
        return deliveredTime;
    }

    public void setDeliveredTime(Timestamp deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    public Timestamp getReadTime() {
        return readTime;
    }

    public void setReadTime(Timestamp readTime) {
        this.readTime = readTime;
    }
}
