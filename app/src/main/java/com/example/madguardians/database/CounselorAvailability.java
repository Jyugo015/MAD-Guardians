package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName ="counselorAvailability",
        foreignKeys = {
            @ForeignKey(
                    entity= Counselor.class,
                    parentColumns = "counselorId",
                    childColumns = "counselorId",
                    onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity =Timeslot.class,
                    parentColumns = "timeslotId",
                    childColumns = "timeslotId",
                    onDelete = ForeignKey.RESTRICT
            )
        }
)
public class CounselorAvailability {
    @PrimaryKey
    private String counselorAvailabilityId;

    @NonNull
    private String counselorId;
    @NonNull
    private String timeslotId;

    @NonNull
    private String date;
    @ColumnInfo(defaultValue = "false")
    private boolean isBooked;
}
