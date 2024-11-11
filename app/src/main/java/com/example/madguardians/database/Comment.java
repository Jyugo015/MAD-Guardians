package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "comment",
        foreignKeys = {
            @ForeignKey(
                    entity = User.class,
                    parentColumns = "userId",
                    childColumns = "userId",
                    onDelete = ForeignKey.SET_NULL
                    //shown as "user no longer exists" upon deletion
            ),
            @ForeignKey(
                    entity = Post.class,
                    parentColumns = "postId",
                    childColumns = "postId",
                    onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Comment.class,
                    parentColumns = "commentId",
                    childColumns = "rootComment",
                    onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = User.class,
                    parentColumns = "userId",
                    childColumns = "replyUserId",
                    onDelete = ForeignKey.SET_NULL
                    //shown as "user no longer exists" upon deletion
            )
        }
)
public class Comment {
    @PrimaryKey
    private String commentId;
    @Nullable
    private String userId;
    @NonNull
    private String postId;
    @NonNull
    private String comment;
    @Nullable
    private String rootComment;
    @Nullable
    private String replyUserId;
    private boolean isRead;
    @NonNull
    private String timestamp;
}
