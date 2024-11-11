package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "appointment",
        primaryKeys = {"counselorAvailability", "userId"},
        foreignKeys = {
            @ForeignKey(
                    entity= CounselorAvailability.class,
                    parentColumns = "counselorAvailabilityId",
                    childColumns = "counselorAvailability",
                    onDelete = ForeignKey.RESTRICT
            ),
            @ForeignKey(
                    entity = User.class,
                    parentColumns = "userId",
                    childColumns = "userId",
                    onDelete= ForeignKey.CASCADE
            )
        },
        indices = {@Index(value ={"counselorAvailabilityId"}, unique = true)}
)
public class Appointment {
    private String counselorAvailabilityId;
    private String userId;

    @ColumnInfo(defaultValue = "true")
    private boolean isOnline;
}
