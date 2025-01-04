package com.example.madguardians.ui.staff;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VerEducator {
    @NonNull
    private String verEducatorId;
    @NonNull
    private String userId;
//    @Nullable
//    private String pdfUrl;
    @Nullable
    private String mediaId;

    // Change domainId to a List
    @NonNull
    private List<String> domainId;

    @NonNull
    private String staffId;

    @NonNull
    @ColumnInfo(defaultValue = "pending")
    private String verifiedStatus;

    @NonNull
    public Timestamp timestamp;

    public VerEducator() {
        this.domainId = new ArrayList<>();
    }

    // Updated constructor
    public VerEducator(@NonNull String verEducatorId, @NonNull String userId,
                       @Nullable String mediaId, @NonNull List<String> domainId,
                       @Nullable String staffId, @NonNull String verifiedStatus,
                       @NonNull Timestamp timestamp) {
        this.verEducatorId = verEducatorId;
        this.userId = userId;
        this.mediaId = mediaId;
        this.domainId = domainId;
        this.staffId = staffId;
        this.verifiedStatus = verifiedStatus;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    @NonNull
    public String getVerEducatorId() {
        return verEducatorId;
    }

    public void setVerEducatorId(@NonNull String verEducatorId) {
        this.verEducatorId = verEducatorId;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(@NonNull String mediaId) {
        this.mediaId = mediaId;
    }

    @NonNull
    public List<String> getDomainId() {
        return domainId;
    }

    public void setDomainId(@NonNull List<String> domainId) {
        this.domainId = domainId;
    }

    @NonNull
    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(@NonNull String staffId) {
        this.staffId = staffId;
    }

    @NonNull
    public String getVerifiedStatus() {
        return verifiedStatus;
    }

    public void setVerifiedStatus(@NonNull String verifiedStatus) {
        this.verifiedStatus = verifiedStatus;
    }

    @NonNull
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NonNull Timestamp timestamp) {
        this.timestamp = timestamp;
    }

}
