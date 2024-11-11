package com.example.madguardians.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM user WHERE userId = :userId")
    User getUserById(String userId);

    @Query("SELECT * FROM user")
    List<User> getAllUsers();

    @Update
    void updateUser(User user);

    @Query("DELETE FROM user WHERE userId = :userId")
    void deleteUser(String userId);
}