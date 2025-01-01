package com.example.madguardians.ui.staff;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;

public class Post {
    @NonNull
    private String postId;
    @NonNull
    private String userId;
    @NonNull
    private String title;
    @NonNull
    private String description;
    @Nullable
    private String imageSetId;
    @Nullable
    private String videoSetId;
    @Nullable
    private String fileSetId;
    @Nullable
    private String quizId;
    @NonNull
    private String domainId;
    @Nullable
    private String folderId;
    @NonNull
    private String date;

    public Post() {
    }

    // Constructor
    public Post(String postId, @NonNull String userId, @NonNull String title, @NonNull String description,
                @Nullable String imageSetId, @Nullable String videoSetId, @Nullable String fileSetId,
                @Nullable String quizId, @NonNull String domainId, @Nullable String folderId, @NonNull String date) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.imageSetId = imageSetId;
        this.videoSetId = videoSetId;
        this.fileSetId = fileSetId;
        this.quizId = quizId;
        this.domainId = domainId;
        this.folderId = folderId;
        this.date = date;
    }

    // Getters
    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageSetId() {
        return imageSetId;
    }

    public String getVideoSetId() {
        return videoSetId;
    }

    public String getFileSetId() {
        return fileSetId;
    }

    public String getQuizId() {
        return quizId;
    }

    public String getDomainId() {
        return domainId;
    }

    public String getFolderId() {
        return folderId;
    }

    public String getDate() {
        return date;
    }

    // Setters
    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageSetId(String imageSetId) {
        this.imageSetId = imageSetId;
    }

    public void setVideoSetId(String videoSetId) {
        this.videoSetId = videoSetId;
    }

    public void setFileSetId(String fileSetId) {
        this.fileSetId = fileSetId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
