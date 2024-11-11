package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName ="issue")
public class Issue {
    @PrimaryKey
    private String issueId;
    @NonNull
    private String type;
}
