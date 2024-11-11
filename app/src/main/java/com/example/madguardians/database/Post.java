package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "post",
        foreignKeys = {
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "userId",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE
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
                        childColumns = "videoSetId",
                        onDelete = ForeignKey.SET_NULL
                ),
                @ForeignKey(
                        entity = MediaSet.class,
                        parentColumns = "mediaSetId",
                        childColumns = "fileSetId",
                        onDelete = ForeignKey.SET_NULL
                ),
                @ForeignKey(
                        entity = QuizQuestion.class,
                        parentColumns = "quizId",
                        childColumns = "quizId",
                        onDelete = ForeignKey.SET_NULL
                ),
                @ForeignKey(
                        entity = Domain.class,
                        parentColumns = "domainId",
                        childColumns = "domainId",
                        onDelete = ForeignKey.RESTRICT
                ),
                @ForeignKey(
                        entity = Folder.class,
                        parentColumns = "folderId",
                        childColumns = "folderId",
                        onDelete = ForeignKey.SET_NULL
                )
        }
)
public class Post {
    @PrimaryKey
    private String postId;

    @NonNull
    private String userId;

    @NonNull
    private String title;
    @NonNull
    private String description;
    @Nullable
    private String imageSetId;
    @Nullable
    private String videoSetId;
    @Nullable
    private String fileSetId;

    @Nullable
    private String quizId;

    @NonNull
    private String domainId;
    @Nullable
    private String folderId;

    @NonNull
    private String date;
}
