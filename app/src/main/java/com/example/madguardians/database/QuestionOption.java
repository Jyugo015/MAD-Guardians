package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "questionOption",
        primaryKeys = {"questionId", "choice"},
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

    // Constructor
    public QuestionOption(@NonNull String questionId, @NonNull String choice, boolean isCorrect) {
        this.questionId = questionId;
        this.choice = choice;
        this.isCorrect = isCorrect;
    }

    // Getters and Setters
    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
