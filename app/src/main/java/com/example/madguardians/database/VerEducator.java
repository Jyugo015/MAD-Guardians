package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "verEducator",
        primaryKeys = {"userId", "domainId"},
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "userId",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Domain.class,
                        parentColumns = "domainId",
                        childColumns = "domainId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Staff.class,
                        parentColumns = "staffId",
                        childColumns = "staffId",
                        onDelete = ForeignKey.RESTRICT
                ),
                @ForeignKey(
                        entity = MediaSet.class,
                        parentColumns = "mediaSetId",
                        childColumns = "imageSetId",
                        onDelete = ForeignKey.SET_NULL
                ),
                @ForeignKey(
                        entity = MediaSet.class,
                        parentColumns = "mediaSetId",
                        childColumns = "fileSetId",
                        onDelete = ForeignKey.SET_NULL
                )
        },
        indices = {
            @Index(value = {"imageSetId"}, unique = true),
            @Index(value = {"fileSetId"}, unique = true)
        }
        )
public class VerEducator {
    private String userId;
    @Nullable
    private String imageSetId;
    @Nullable
    private String fileSetId;
    private String domainId;
    @NonNull
    private String staffId;
    @NonNull
    @ColumnInfo(defaultValue = "pending")
    private String verifiedStatus;
}
