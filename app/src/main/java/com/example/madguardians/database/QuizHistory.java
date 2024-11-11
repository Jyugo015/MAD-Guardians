package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName ="quiz",
        foreignKeys = {
                @ForeignKey(
                        entity = QuizQuestion.class,
                        parentColumns = "quizId",
                        childColumns = "quizId",
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
public class QuizHistory {
    @PrimaryKey
    private String quizId;

    @NonNull
    private String userId;

    private int score;
    @NonNull
    private String timestamp;

}
