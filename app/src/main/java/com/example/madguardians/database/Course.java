package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "course",
        foreignKeys = {
                @ForeignKey(
                        entity = Post.class,
                        parentColumns = "postId",
                        childColumns = "post1",
                        onDelete = ForeignKey.SET_NULL
                        //shown as "empty post" upon deletion
                ),
                @ForeignKey(
                        entity = Post.class,
                        parentColumns = "postId",
                        childColumns = "post2",
                        onDelete = ForeignKey.SET_NULL
                        //shown as "empty post" upon deletion
                ),
                @ForeignKey(
                        entity = Post.class,
                        parentColumns = "postId",
                        childColumns = "post3",
                        onDelete = ForeignKey.SET_NULL
                        //shown as "empty post" upon deletion
                ),
                @ForeignKey(
                        entity = Folder.class,
                        parentColumns = "folderId",
                        childColumns = "folderId",
                        onDelete = ForeignKey.SET_NULL
                )
        }
)
public class Course {
    @PrimaryKey
    private String courseId;
    @NonNull
    private String title;
    @NonNull
    private String description;
    @NonNull
    private String coverImage;
    @Nullable
    private String post1;
    @Nullable
    private String post2;
    @Nullable
    private String post3;
    @Nullable
    private String folderId;
    @NonNull
    private String date;

}
