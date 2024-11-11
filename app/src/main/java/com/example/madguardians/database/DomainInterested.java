package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "domainInterested",
        primaryKeys = {"userId", "domainId"},
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "userId",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Domain.class,
                        parentColumns = "domainId",
                        childColumns = "domainId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class DomainInterested {
    private String userId;

    private String domainId;

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }
}
