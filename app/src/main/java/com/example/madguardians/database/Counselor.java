package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "counselor",
        indices = {
            @Index(value = {"email"}, unique = true),
            @Index(value = {"contactNo"}, unique = true)
        }
)
public class Counselor {
    @PrimaryKey
    private String counselorId;

    @NonNull
    private String name;
    @NonNull
    private String office;
    @NonNull
    private String email;
    @NonNull
    private String password;
    @NonNull
    @ColumnInfo(defaultValue = "url link of the default profilepic")
    private String profilePic;

}
