package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "verPost",
        foreignKeys = {
                @ForeignKey(
                        entity = Post.class,
                        parentColumns = "postId",
                        childColumns = "postId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Staff.class,
                        parentColumns = "staffId",
                        childColumns = "staffId",
                        onDelete = ForeignKey.RESTRICT
                        //prevent the staff to be deleted if they are in charge
                        //of verification of a post
                )
        },
        indices = {
                @Index(value = {"postId"}, unique = true)
        }
)
public class VerPost {

    @PrimaryKey
    private String verPostId;
    @NonNull
    private String postId;
    @NonNull
    private String staffId;
    @NonNull
    @ColumnInfo(defaultValue = "pending")
    private String verifiedStatus;

    // Getters and Setters
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
}
