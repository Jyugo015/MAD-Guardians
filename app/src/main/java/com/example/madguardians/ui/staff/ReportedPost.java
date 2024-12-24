package com.example.madguardians.ui.staff;

public class ReportedPost{
    private String image;
    private String postTitle;
    private String authorName;
    private String reason;
    private String status;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }
    public String getAuthorName() {
        return authorName;
    }
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ReportedPost(String image, String postTitle, String authorName, String reason, String status){
        this.image=image;
        this.postTitle=postTitle;
        this.authorName=authorName;
        this.reason=reason;
        this.status=status;
    }
}
