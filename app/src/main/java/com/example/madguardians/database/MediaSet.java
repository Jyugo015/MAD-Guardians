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
}
