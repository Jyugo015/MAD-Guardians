package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "helpdesk",
        foreignKeys = {
            @ForeignKey(
                    entity = Issue.class,
                    parentColumns = "issueId",
                    childColumns = "issueid",
                    onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = User.class,
                    parentColumns = "userId",
                    childColumns = "userId",
                    onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Post.class,
                    parentColumns = "postId",
                    childColumns = "postId",
                    onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Course.class,
                    parentColumns = "courseId",
                    childColumns = "courseId",
                    onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Staff.class,
                    parentColumns = "staffId",
                    childColumns = "staffId",
                    onDelete = ForeignKey.RESTRICT
            )
        }
)
public class Helpdesk {
    @PrimaryKey
    private String helpdeskId;
    @NonNull
    private String issueId;
    @NonNull
    private String userId;
    @Nullable
    private String postId;
    @Nullable
    private String courseId;
    @NonNull
    private String staffId;
    @NonNull
    @ColumnInfo(defaultValue = "pending")
    private String helpdeskStatus;
}
