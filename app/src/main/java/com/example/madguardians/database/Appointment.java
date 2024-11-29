package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "appointment",
        primaryKeys = {"counselorAvailability", "userId"},
        foreignKeys = {
            @ForeignKey(
                    entity= CounselorAvailability.class,
                    parentColumns = "counselorAvailabilityId",
                    childColumns = "counselorAvailability",
                    onDelete = ForeignKey.RESTRICT
            ),
            @ForeignKey(
                    entity = User.class,
                    parentColumns = "userId",
                    childColumns = "userId",
                    onDelete= ForeignKey.CASCADE
            )
        },
        indices = {@Index(value ={"counselorAvailabilityId"}, unique = true)}
)
public class Appointment {
    private String counselorAvailabilityId;
    private String userId;

    @ColumnInfo(defaultValue = "true")
    private boolean isOnline;

    // Constructor
    public Appointment(@NonNull String counselorAvailabilityId, @NonNull String userId, boolean isOnline) {
        this.counselorAvailabilityId = counselorAvailabilityId;
        this.userId = userId;
        this.isOnline = isOnline;
    }

    // Getter for counselorAvailabilityId
    @NonNull
    public String getCounselorAvailabilityId() {
        return counselorAvailabilityId;
    }

    // Setter for counselorAvailabilityId
    public void setCounselorAvailabilityId(@NonNull String counselorAvailabilityId) {
        this.counselorAvailabilityId = counselorAvailabilityId;
    }

    // Getter for userId
    @NonNull
    public String getUserId() {
        return userId;
    }

    // Setter for userId
    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    // Getter for isOnline
    public boolean isOnline() {
        return isOnline;
    }

    // Setter for isOnline
    public void setOnline(boolean online) {
        isOnline = online;
    }
}
