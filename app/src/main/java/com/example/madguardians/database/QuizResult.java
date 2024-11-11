package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "quizResult",
        foreignKeys = {
                @ForeignKey(
                        entity = QuizQuestion.class,
                        parentColumns = "questionId",
                        childColumns = "questionId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "userId",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = QuestionOption.class,
                        parentColumns = "choiceId",
                        childColumns = "userAns",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class QuizResult {
    @PrimaryKey
    private String quizResultId;

    @NonNull
    private String questionId;

    @NonNull
    private String userId;

    @NonNull
    private String userAns;

    @NonNull
    private String timestamp;
}
