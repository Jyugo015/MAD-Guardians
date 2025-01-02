//package com.example.madguardians.ui.staff;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.room.ColumnInfo;
//import androidx.room.Entity;
//import androidx.room.ForeignKey;
//import androidx.room.PrimaryKey;
//
//public class Helpdesk {
//    @NonNull
//    private String helpdeskId;
//    @NonNull
//    private String issueId;
//    @NonNull
//    private String userId;
//    @Nullable
//    private String postId;
//    @Nullable
//    private String courseId;
//    @Nullable
//    private String commentId;
//    @Nullable
//    private String quizId;
//    @Nullable
//    private String staffId;
//    @Nullable
//    private String reason;
//    @NonNull
//    @ColumnInfo(defaultValue = "pending")
//    private String helpdeskStatus;
//
//    public Helpdesk() {
//    }
//
//    // Constructor
//    public Helpdesk(@NonNull String helpdeskId, @NonNull String issueId, @NonNull String userId,
//                    @Nullable String postId, @Nullable String courseId, @Nullable String commentId,
//                    @Nullable String quizId, @NonNull String staffId, @Nullable String reason,
//                    @NonNull String helpdeskStatus) {
//        this.helpdeskId = helpdeskId;
//        this.issueId = issueId;
//        this.userId = userId;
//        this.postId = postId;
//        this.courseId = courseId;
//        this.commentId = commentId;
//        this.quizId = quizId;
//        this.staffId = staffId;
//        this.reason = reason;
//        this.helpdeskStatus = helpdeskStatus;
//    }
//
//    // Getters and Setters
//    public String getHelpdeskId() {
//        return helpdeskId;
//    }
//
//    public void setHelpdeskId(String helpdeskId) {
//        this.helpdeskId = helpdeskId;
//    }
//
//    public String getIssueId() {
//        return issueId;
//    }
//
//    public void setIssueId(String issueId) {
//        this.issueId = issueId;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public String getPostId() {
//        return postId;
//    }
//
//    public void setPostId(String postId) {
//        this.postId = postId;
//    }
//
//    public String getCourseId() {
//        return courseId;
//    }
//
//    public void setCourseId(String courseId) {
//        this.courseId = courseId;
//    }
//
//    public String getCommentId() {
//        return commentId;
//    }
//
//    public void setCommentId(String commentId) {
//        this.commentId = commentId;
//    }
//
//    public String getQuizId() {
//        return quizId;
//    }
//
//    public void setQuizId(String quizId) {
//        this.quizId = quizId;
//    }
//
//    public String getStaffId() {
//        return staffId;
//    }
//
//    public void setStaffId(String staffId) {
//        this.staffId = staffId;
//    }
//
//    public String getReason() {
//        return reason;
//    }
//
//    public void setReason(String reason) {
//        this.reason = reason;
//    }
//
//    public String getHelpdeskStatus() {
//        return helpdeskStatus;
//    }
//
//    public void setHelpdeskStatus(String helpdeskStatus) {
//        this.helpdeskStatus = helpdeskStatus;
//    }
//}

package com.example.madguardians.ui.staff;

import com.google.firebase.Timestamp;

import java.sql.Time;

public class Helpdesk {
    private String helpdeskId;
    private String issueId;
    private String userId;
    private String postId;
    private String courseId;
    private String commentId;
    private String quizId;
    private String staffId;
    private String reason;
    private String helpdeskStatus;
    private Timestamp timestamp;

    public Helpdesk() {
    }

    // Constructor
    public Helpdesk( String helpdeskId,  String issueId,  String userId,
                     String postId,  String courseId,  String commentId,
                     String quizId,  String staffId,  String reason,
                     String helpdeskStatus, Timestamp timestamp) {
        this.helpdeskId = helpdeskId;
        this.issueId = issueId;
        this.userId = userId;
        this.postId = postId;
        this.courseId = courseId;
        this.commentId = commentId;
        this.quizId = quizId;
        this.staffId = staffId;
        this.reason = reason;
        this.helpdeskStatus = helpdeskStatus;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getHelpdeskId() {
        return helpdeskId;
    }

    public void setHelpdeskId(String helpdeskId) {
        this.helpdeskId = helpdeskId;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getHelpdeskStatus() {
        return helpdeskStatus;
    }

    public void setHelpdeskStatus(String helpdeskStatus) {
        this.helpdeskStatus = helpdeskStatus;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
