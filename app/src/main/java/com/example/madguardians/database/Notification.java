package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "notification",
        foreignKeys ={
            @ForeignKey(
                    entity = User.class,
                    parentColumns = "userId",
                    childColumns = "userId",
                    onDelete = ForeignKey.CASCADE
            )
        }
)
public class Notification {
    @PrimaryKey
    private String notificationId;
    @NonNull
    private String userId;
    @NonNull
    private String message;
    @Nullable
    private String deliveredTime;
    @Nullable
    private String readTime;
}
