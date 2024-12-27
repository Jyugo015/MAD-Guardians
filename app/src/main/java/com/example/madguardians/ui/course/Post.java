package com.example.madguardians.ui.course;

import java.util.ArrayList;

public class Post {
    private String postId;
    private String userId;
    private String title;
    private String description;
    private String imageSetId;
    private String videoSetId;
    private String fileSetId;
    private String quizId;
    private String domainId;
    private String folderId;
    private String date;
    private static ArrayList<Post> posts = new ArrayList<>();
    public Post(String postId, String userId, String title, String description, String imageSetId, String videoSetId, String fileSetId, String quizId, String domainId, String folderId, String date) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.imageSetId = imageSetId;
        this.videoSetId = videoSetId;
        this.fileSetId = fileSetId;
        this.quizId = quizId;
        this.domainId = domainId;
        this.folderId = folderId;
        this.date = date;
    }

    public static Post getPost(String postId) {
        for (Post post: posts)  {
            if (post.getPostId().equals(postId)) {
                return post;
            }
        }
        return null;
    }
    public static ArrayList<Post> getPosts() {
        return posts;
    }

    public static void intializePosts() {
        posts.add(new Post("P001","James",  "Java1",  "This is description 1",  "M001",  "M003",  "M002",  "quizId1",  "domainId1",  "folderId1",  "date1"));
        posts.add(new Post("P002","JJ",  "Java2",  "This is description 2",  null,  null,  "M002",  "quizId1",  "domainId1",  "folderId1",  "date2"));
        posts.add(new Post("P003","Michiel",  "Java3",  "This is description 3",  null,  "M003",  null,  "quizId1",  "domainId1",  "folderId1",  "date3"));
        posts.add(new Post("P004","Michiel",  "Java4",  "This is description 4",  "M001",  "M003",  "M002",  "quizId1",  "domainId1",  "folderId1",  "date3"));
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getImageSetId() {
        return imageSetId;
    }

    public void setImageSetId(String imageSetId) {
        this.imageSetId = imageSetId;
    }

    public String getVideoSetId() {
        return videoSetId;
    }

    public void setVideoSetId(String videoSetId) {
        this.videoSetId = videoSetId;
    }

    public String getFileSetId() {
        return fileSetId;
    }

    public void setFileSetId(String fileSetId) {
        this.fileSetId = fileSetId;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
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

