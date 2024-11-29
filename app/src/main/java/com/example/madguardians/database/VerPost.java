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

    // Constructor
    public VerPost(@NonNull String verPostId, @NonNull String postId, @NonNull String staffId, @NonNull String verifiedStatus) {
        this.verPostId = verPostId;
        this.postId = postId;
        this.staffId = staffId;
        this.verifiedStatus = verifiedStatus;
    }

    // Getter and Setter for verPostId
    public String getVerPostId() {
        return verPostId;
    }

    public void setVerPostId(String verPostId) {
        this.verPostId = verPostId;
    }

    // Getter and Setter for postId
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    // Getter and Setter for staffId
    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    // Getter and Setter for verifiedStatus
    public String getVerifiedStatus() {
        return verifiedStatus;
    }

    public void setVerifiedStatus(String verifiedStatus) {
        this.verifiedStatus = verifiedStatus;
    }
}
