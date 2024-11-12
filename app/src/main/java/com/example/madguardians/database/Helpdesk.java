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
                    onDelete = ForeignKey.SET_NULL
            ),
            @ForeignKey(
                    entity = Course.class,
                    parentColumns = "courseId",
                    childColumns = "courseId",
                    onDelete = ForeignKey.SET_NULL
            ),
            @ForeignKey(
                    entity = Commnet.class,
                    parentColumns = "commentId",
                    childColumns = "commentId",
                    onDelete = ForeignKey.SET_NULL
            ),
            @ForeignKey(
                    entity = QuizQuestion.class,
                    parentColumns = "quizId",
                    childColumns = "quizId",
                    onDelete = ForeignKey.SET_NULL
            )
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
    @Nullable
    private String commentId;
    @Nullable
    private String quizId;
    @NonNull
    private String staffId;
    @Nullable
    private String reason;
    @NonNull
    @ColumnInfo(defaultValue = "pending")
    private String helpdeskStatus;
}
