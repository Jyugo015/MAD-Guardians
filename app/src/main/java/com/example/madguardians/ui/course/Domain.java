package com.example.madguardians.ui.course;
import java.util.ArrayList;

public class Domain {
    private String domainId;

    private String domainName;
    private static ArrayList<Domain> domains= new ArrayList<>();

    public Domain(String domainId, String domainName) {
        this.domainId = domainId;
        this.domainName = domainName;
    }

    public static ArrayList<Domain> getDomains() {
        return domains;
    }

    public static void initialiseDomains() {
        domains = new ArrayList<>();
        domains.add(new Domain("D001", "Language"));
        domains.add(new Domain("D002", "Programming"));
        domains.add(new Domain("D003", "Music"));
    }

    public static Domain getDomainById(String domainId){
        for (Domain domain : domains) {
            if (domain.getDomainId().equals(domainId))
                return domain;
        }
        return null;
    }

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
