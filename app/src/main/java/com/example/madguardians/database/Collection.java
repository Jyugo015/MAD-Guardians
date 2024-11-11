package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "collection",
        foreignKeys = {
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
                        entity = Folder.class,
                        parentColumns = "folderId",
                        childColumns = "folderId",
                        onDelete = ForeignKey.SET_NULL
                )
        }
)
public class Collection {
    @PrimaryKey
    private String collectionId;
    @NonNull
    private String userId;
    @Nullable
    private String postId;
    @Nullable
    private String courseId;
    @Nullable
    private String folderId;
}
