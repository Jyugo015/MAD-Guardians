package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "mediaSet")
public class MediaSet {
    @PrimaryKey
    private String mediaId;
    @NonNull
    private String mediaSetId;

    @NonNull
    private String url;

    // Constructor
    public MediaSet(@NonNull String mediaId, @NonNull String mediaSetId, @NonNull String url) {
        this.mediaId = mediaId;
        this.mediaSetId = mediaSetId;
        this.url = url;
    }

    // Getters and Setters
    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaSetId() {
        return mediaSetId;
    }

    public void setMediaSetId(String mediaSetId) {
        this.mediaSetId = mediaSetId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
