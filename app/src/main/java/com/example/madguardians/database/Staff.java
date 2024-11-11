package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "staff",
        indices = {
            @Index(value = {"name"}, unique = true),
            @Index(value = {"email"}, unique = true)
        }
)
public class Staff {
    @PrimaryKey
    private String staffId;

    @NonNull
    private String name;

    @NonNull
    private String email;

    @NonNull
    private String password;
}