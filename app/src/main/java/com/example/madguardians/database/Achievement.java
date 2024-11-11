package com.example.madguardians.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "achievement",
        primaryKeys = {"userId", "badgeId"},
        foreignKeys = {
            @ForeignKey(
                    entity = User.class,
                    parentColumns = "userId",
                    childColumns = "userId",
                    onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Badge.class,
                    parentColumns = "badgeId",
                    childColumns = "badgeId",
                    onDelete = ForeignKey.CASCADE
            )
        }
)
public class Achievement {
    private String userId;
    private String badgeId;
}
