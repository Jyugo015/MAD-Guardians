package com.example.madguardians.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "questionOption",
        primaryKeys = {"questionId", "choiceId"},
        foreignKeys = {
                @ForeignKey(
                        entity = QuizQuestion.class,
                        parentColumns = "questionId",
                        childColumns = "questionId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class QuestionOption {
    private String questionId;
    private String choice;
    @ColumnInfo(defaultValue = "false")
    private boolean isCorrect;

}
