package com.example.madguardians.ui.course;

import java.util.ArrayList;
import java.util.List;

public class CourseElement {
    private String courseId;
    private String title;
    private String author;
    private String description;
    private String coverImage;
    private String post1;
    private String post2;
    private String post3;
    private String folderId;
    private String date;
    private static ArrayList<CourseElement> courseList = new ArrayList<>();

    public static CourseElement getCourse(String courseId) {
        for (CourseElement course : CourseElement.courseList) {
            if (course.getCourseId().equals(courseId)) {
                return course;
            }
        }
        return null;
    }

    public static ArrayList<CourseElement> getCourses(String domainId) {
        ArrayList<CourseElement> courses = new ArrayList<>();
        for (CourseElement course : CourseElement.courseList) {
            if (course.getFolderId().equals(domainId)) {
                courses.add(course);
            }
        }
        return courses;
    }

    public static List<CourseElement> getCourseList() {
        return courseList;
    }

    public static void initializeCourseList() {
        courseList.add(new CourseElement("C001", "Python", "Daniel Liang", "This is Java", "https://res.cloudinary.com/dmgpozfee/image/upload/v1732898099/vfp2hoinnc2udodmftyv.jpg","P001", "P002","P003", "D001", "11/12/2024"));
        courseList.add(new CourseElement("C002", "Java", "Daniel Chong", "This is Python", "https://res.cloudinary.com/dmgpozfee/image/upload/v1732898099/vfp2hoinnc2udodmftyv.jpg","P004", "P002","P003", "D001", "12/12/2024"));
        courseList.add(new CourseElement("C003", "C++", "Michille Liang", "This is C++", "https://res.cloudinary.com/dmgpozfee/image/upload/v1732898099/vfp2hoinnc2udodmftyv.jpg","P001", "P002","P003", "D002", "13/12/2024"));
        courseList.add(new CourseElement("C004", "JavaScript", "Jane Smith", "This is JavaScript", "https://res.cloudinary.com/dmgpozfee/image/upload/v1732898099/vfp2hoinnc2udodmftyv.jpg","P001", "P002","P003", "D003", "14/12/2024"));
        courseList.add(new CourseElement("C005", "PHP", "Benz Harris", "This is PHP", "https://res.cloudinary.com/dmgpozfee/image/upload/v1732898099/vfp2hoinnc2udodmftyv.jpg","P001", "P002","P003", "D003", "15/12/2024"));
    }
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public CourseElement(String courseId, String title, String author, String description, String coverImage, String post1, String post2, String post3, String folderId, String date) {
        this.courseId = courseId;
        this.title = title;
        this.author = author;
        this.description = description;
        this.coverImage = coverImage;
        this.post1 = post1;
        this.post2 = post2;
        this.post3 = post3;
        this.folderId = folderId;
        this.date = date;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getPost1() {
        return post1;
    }

    public void setPost1(String post1) {
        this.post1 = post1;
    }

    public String getPost2() {
        return post2;
    }

    public void setPost2(String post2) {
        this.post2 = post2;
    }

    public String getPost3() {
        return post3;
    }

    public void setPost3(String post3) {
        this.post3 = post3;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

