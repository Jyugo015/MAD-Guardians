package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName= "timeslot")
public class Timeslot {
    @PrimaryKey
    private String timeslotId;

    private int startTime;
    private int endTime;
}
