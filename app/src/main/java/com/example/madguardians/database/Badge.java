package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "badge",
        indices = {
        @Index(value= {"badgeName"}, unique = true),
        @Index(value= {"badgeImage"}, unique = true)
        }
        )
public class Badge {
    @PrimaryKey
    private String badgeId;
    @NonNull
    private String badgeName;
    @NonNull
    private String badgeImage;
}
