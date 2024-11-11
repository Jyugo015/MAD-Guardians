package com.example.madguardians.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface VerPostDao {

    @Insert
    void insertVerPost(VerPost verPost);

    @Query("SELECT * FROM verPost WHERE verPostId = :verPostId")
    VerPost getVerPostById(String verPostId);
}
