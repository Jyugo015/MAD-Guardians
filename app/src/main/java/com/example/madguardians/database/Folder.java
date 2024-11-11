package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "folder",
        foreignKeys ={
            @ForeignKey(
                    entity = User.class,
                    parentColumns = "userId",
                    childColumns = "userId",
                    onDelete = ForeignKey.CASCADE
            ),
            @ForeignKey(
                    entity = Folder.class,
                    parentColumns = "folderId",
                    childColumns = "rootFolder",
                    onDelete = ForeignKey.SET_NULL
            )
        }
)
public class Folder {
    @PrimaryKey
    private String folderId;
    @NonNull
    private String userId;
    @NonNull
    private String name;
    @Nullable
    private String rootFolder;
}
