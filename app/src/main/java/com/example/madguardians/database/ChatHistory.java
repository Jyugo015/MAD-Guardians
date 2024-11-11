package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "chatHistory",
        foreignKeys = {
            @ForeignKey(
                    entity = User.class,
                    parentColumns = "userId",
                    childColumns = "senderUserId",
                    onDelete = ForeignKey.SET_NULL
            ),
            @ForeignKey(
                    entity = User.class,
                    parentColumns = "userId",
                    childColumns = "recipientUserId",
                    onDelete = ForeignKey.SET_NULL
            ),
            @ForeignKey(
                    entity = MediaSet.class,
                    parentColumns = "mediaSetId",
                    childColumns = "mediaSetId",
                    onDelete = ForeignKey.SET_NULL
            ),
            @ForeignKey(
                    entity = ChatHistory.class,
                    parentColumns = "messageId",
                    childColumns = "replyMessage",
                    onDelete = ForeignKey.SET_NULL
            )
        }
)
public class ChatHistory {
    @PrimaryKey
    private String messageId;
    @Nullable
    private String senderUserId;
    @Nullable
    private String recipientUserId;
    @NonNull
    private String message;
    @Nullable
    private String mediaId;
    @Nullable
    private String replyMessage;

    @Nullable
    private String deliveredTime;
    @Nullable
    private String readTime;

}
