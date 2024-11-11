package com.example.madguardians.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface DomainInterestedDao {
    @Insert
    void insertDomainInterested(DomainInterested domainInterested);

    @Query("SELECT * FROM domainInterested WHERE userId = :userId")
    List<DomainInterested> getDomainsByUserId(String userId);

    @Query("DELETE FROM domainInterested WHERE userId = :userId AND domainId = :domainId")
    void deleteDomainInterested(String userId, String domainId);
}
