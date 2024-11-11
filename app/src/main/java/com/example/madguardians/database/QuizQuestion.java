package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "quizQuestion")
public class QuizQuestion {
    @PrimaryKey
    private String questionId;

    @NonNull
    private String quizId;
    @NonNull
    private String question;

    private int questionNo;

}
