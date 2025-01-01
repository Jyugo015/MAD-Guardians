package com.example.madguardians.ui.course;

import android.net.Uri;

public class CourseViewModel {
    private static String title = "";
    private static String description = "";
    private static Uri coverImageUri = null;
    private static String domainId =  null;
    private static String folderId =  null;
    private static CourseViewModel course = new CourseViewModel();

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        course.domainId = domainId;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        course.folderId = folderId;
    }

    public Uri getCoverImageUri() {
        return coverImageUri;
    }

    public void setCoverImageUri(Uri coverImageUri) {
        course.coverImageUri = coverImageUri;
    }

    private CourseViewModel(){
    }

    public static CourseViewModel getCourse() {
        return course;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        course.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        course.title = title;
    }

    public static void clear(CourseViewModel course) {
        course.title = null;
        course.description = null;
        course.folderId = "";
        course.coverImageUri = null;
        course.domainId = "";
    }
}
