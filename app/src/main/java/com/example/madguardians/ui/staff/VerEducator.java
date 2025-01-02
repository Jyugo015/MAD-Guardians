package com.example.madguardians.ui.staff;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.google.firebase.Timestamp;

public class VerEducator {
    @NonNull
    private String verEducatorId;
    @NonNull
    private String userId;
    @Nullable
    private String imageSetId;
    @Nullable
    private String fileSetId;
    @NonNull
    private String domainId;
    @NonNull
    private String staffId;
    @NonNull
    @ColumnInfo(defaultValue = "pending")
    private String verifiedStatus;

    @NonNull
    public Timestamp timestamp;
    public VerEducator() {
    }

    // Constructor

    public VerEducator(@NonNull String verEducatorId,@NonNull String userId, @Nullable String imageSetId,
                       @Nullable String fileSetId, @NonNull String domainId,
                       @Nullable String staffId, @NonNull String verifiedStatus,
                       @NonNull Timestamp timestamp) {
        this.verEducatorId = verEducatorId;
        this.userId = userId;
        this.imageSetId = imageSetId;
        this.fileSetId = fileSetId;
        this.domainId = domainId;
        this.staffId = staffId;
        this.verifiedStatus = verifiedStatus;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getVerEducatorId() {
        return verEducatorId;
    }
    public void setVerEducatorId(String verEducatorId) {
        this.verEducatorId = verEducatorId;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Nullable
    public String getImageSetId() {
        return imageSetId;
    }

    public void setImageSetId(@Nullable String imageSetId) {
        this.imageSetId = imageSetId;
    }

    @Nullable
    public String getFileSetId() {
        return fileSetId;
    }

    public void setFileSetId(@Nullable String fileSetId) {
        this.fileSetId = fileSetId;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
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
