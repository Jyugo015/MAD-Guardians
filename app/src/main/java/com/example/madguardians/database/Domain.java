package com.example.madguardians.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "domain",
        indices = {
                @Index(value = {"domainName"}, unique = true)
        }
        )
public class Domain {

    @PrimaryKey
    private String domainId;

    @NonNull
    private String domainName;

    // Getters and Setters
    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }
}
