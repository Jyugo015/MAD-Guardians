package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import java.io.Serializable;

public class Comments implements Serializable{
    @NonNull
    private String commentId;
    @Nullable
    private String userId;
    @NonNull
    private String postId;
    @NonNull
    private String comment;
    @Nullable
    private String rootComment;
    @Nullable
    private String replyUserId;
    @NonNull
    @ColumnInfo(defaultValue = "0")
    private boolean isRead;
    @NonNull
    private String authorId;
    @NonNull
    private Timestamp timestamp;

    // Constructor
    public Comments(@NonNull String commentId, @Nullable String userId, @NonNull String postId,
                   @NonNull String comment, @Nullable String rootComment, @Nullable String replyUserId,
                   boolean isRead, String authorId, @NonNull Timestamp timestamp) {
        this.commentId = commentId;
        this.userId = userId;
        this.postId = postId;
        this.comment = comment;
        this.rootComment = rootComment;
        this.replyUserId = replyUserId;
        this.isRead = isRead;
        this.authorId = authorId;
        this.timestamp = timestamp;
    }
    public Comments() {
    }

    // Getter and Setter for commentId
    @NonNull
    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(@NonNull String commentId) {
        this.commentId = commentId;
    }

    // Getter and Setter for userId
    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@Nullable String userId) {
        this.userId = userId;
    }

    // Getter and Setter for postId
    @NonNull
    public String getPostId() {
        return postId;
    }

    public void setPostId(@NonNull String postId) {
        this.postId = postId;
    }

    // Getter and Setter for comment text
    @NonNull
    public String getComment() {
        return comment;
    }

    public void setComment(@NonNull String comment) {
        this.comment = comment;
    }

    // Getter and Setter for rootComment
    @Nullable
    public String getRootComment() {
        return rootComment;
    }

    public void setRootComment(@Nullable String rootComment) {
        this.rootComment = rootComment;
    }

    // Getter and Setter for replyUserId
    @Nullable
    public String getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(@Nullable String replyUserId) {
        this.replyUserId = replyUserId;
    }

    // Getter and Setter for isRead
    public boolean isRead() {
        return isRead;
    }


    public void setRead(boolean read) {
        isRead = read;
    }

    @NonNull
    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(@NonNull String authorId) {
        this.authorId = authorId;
    }

    // Getter and Setter for timestamp
    @NonNull
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NonNull Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
