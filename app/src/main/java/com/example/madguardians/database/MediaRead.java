package com.example.madguardians.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "mediaRead",
        primaryKeys = {"mediaId", "postId", "userId"},
        foreignKeys = {
            @ForeignKey(
                    entity = MediaSet.class,
                    parentColumns = "mediaId",
                    childColumns = "mediaId",
                    onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Post.class,
                    parentColumns= "postId",
                    childColumns = "postId",
                    onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = User.class,
                    parentColumns = "userId",
                    childColumns = "userId",
                    onDelete = ForeignKey.CASCADE
            )
        }
)
public class MediaRead {
    private String userId;
    private String postId;
    private String mediaId;
}
