package com.example.madguardians.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface DomainDao {

    @Insert
    void insertDomain(Domain domain);

    @Query("SELECT * FROM domain WHERE domainId = :domainId")
    Domain getDomainById(String domainId);
}
