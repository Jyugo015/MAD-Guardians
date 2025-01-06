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
    @Nullable
    private String replyText;
    @NonNull
    private boolean authorRead;

    @NonNull
    @ColumnInfo(defaultValue = "0")
    private boolean repliedUserRead;
    @NonNull
    private String authorId;
    @NonNull
    private Timestamp timestamp;

    // Constructor
    public Comments(@NonNull String commentId, @Nullable String userId, @NonNull String postId,
                    @NonNull String comment, @Nullable String rootComment, @Nullable String replyUserId,
                    @Nullable String replyText, boolean authorRead, boolean repliedUserRead,
                    String authorId, @NonNull Timestamp timestamp) {
        this.commentId = commentId;
        this.userId = userId;
        this.postId = postId;
        this.comment = comment;
        this.rootComment = rootComment;
        this.replyUserId = replyUserId;
        this.replyText = replyText;
        this.authorRead = authorRead;
        this.repliedUserRead = repliedUserRead;
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

    @Nullable
    public String getReplyText() {
        return replyText;
    }

    public void setReplyText(@Nullable String replyText) {
        this.replyText = replyText;
    }

    public boolean isAuthorRead() {
        return authorRead;
    }

    public void setAuthorRead(boolean authorRead) {
        this.authorRead = authorRead;
    }

    public boolean isRepliedUserRead() {
        return repliedUserRead;
    }

    public void setRepliedUserRead(boolean repliedUserRead) {
        this.repliedUserRead = repliedUserRead;
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
