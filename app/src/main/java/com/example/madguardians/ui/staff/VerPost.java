package com.example.madguardians.ui.staff;

import com.example.madguardians.utilities.UploadCallback;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class VerPost {
    private String verPostId;
    private String postId;
    private String staffId;
    private String verifiedStatus;
    private Timestamp timestamp;

    // Constructor
    public VerPost() {}

    public VerPost(String verPostId, String postId, String staffId, String verifiedStatus, Timestamp timestamp) {
        this.verPostId = verPostId;
        this.postId = postId;
        this.staffId = staffId;
        this.verifiedStatus = verifiedStatus;
        this.timestamp = timestamp;
    }

    // Getter and Setter methods
    public String getVerPostId() {
        return verPostId;
    }

    public void setVerPostId(String verPostId) {
        this.verPostId = verPostId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getVerifiedStatus() {
        return verifiedStatus;
    }

    public void setVerifiedStatus(String verifiedStatus) {
        this.verifiedStatus = verifiedStatus;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}