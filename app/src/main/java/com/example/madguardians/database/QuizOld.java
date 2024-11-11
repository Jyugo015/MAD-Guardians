package com.example.madguardians.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "quizOld",
        primaryKeys = {"quizId", "postId"},
        foreignKeys = {
                @ForeignKey(
                        entity = QuizQuestion.class,
                        parentColumns = "quizId",
                        childColumns = "quizId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Post.class,
                        parentColumns = "postId",
                        childColumns = "postId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class QuizOld {
    private String quizId;
    private String postId;
}