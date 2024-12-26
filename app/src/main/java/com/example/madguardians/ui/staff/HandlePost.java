package com.example.madguardians.ui.staff;

import java.util.ArrayList;
import java.util.List;

public class HandlePost {
    private String image;
    private String postTitle;
    private String authorName;
    private String date;
    private String status;

    public static List<HandlePost> filterPostsByStatus(List<HandlePost> posts, String status) {
        List<HandlePost> filteredList = new ArrayList<>();
        for (HandlePost post : posts) {
            if (post.getStatus().equalsIgnoreCase(status)) {
                filteredList.add(post);
            }
        }
        return filteredList;
    }

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
//
//    @Override
//    public String getTitle() {
//        return "";
//    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }
//
//    @Override
//    public String getReason() {
//        return "";
//    }

    public void setStatus(String status) {
        this.status = status;
    }

    public HandlePost(String image, String postTitle, String authorName, String date, String status){
        this.image=image;
        this.postTitle=postTitle;
        this.authorName=authorName;
        this.date=date;
        this.status=status;
    }
}

//import android.os.Parcel;
//import android.os.Parcelable;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Post implements Parcelable {
//    private String image;
//    private String postTitle;
//    private String authorName;
//    private String date;
//    private String status;
//
//    // Default constructor for frameworks like Firebase
//    public Post() {}
//
//    public Post(String image, String postTitle, String authorName, String date, String status) {
//        this.image = image;
//        this.postTitle = postTitle;
//        this.authorName = authorName;
//        this.date = date;
//        this.status = status;
//    }
//
//    // Parcelable implementation
//    protected Post(Parcel in) {
//        image = in.readString();
//        postTitle = in.readString();
//        authorName = in.readString();
//        date = in.readString();
//        status = in.readString();
//    }
//
//    public static final Creator<Post> CREATOR = new Creator<Post>() {
//        @Override
//        public Post createFromParcel(Parcel in) {
//            return new Post(in);
//        }
//
//        @Override
//        public Post[] newArray(int size) {
//            return new Post[size];
//        }
//    };
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(image);
//        dest.writeString(postTitle);
//        dest.writeString(authorName);
//        dest.writeString(date);
//        dest.writeString(status);
//    }
//
//    // Getters and Setters
//    public String getImage() {
//        return image;
//    }
//
//    public void setImage(String image) {
//        this.image = image;
//    }
//
//    public String getPostTitle() {
//        return postTitle;
//    }
//
//    public void setPostTitle(String postTitle) {
//        this.postTitle = postTitle;
//    }
//
//    public String getAuthorName() {
//        return authorName;
//    }
//
//    public void setAuthorName(String authorName) {
//        this.authorName = authorName;
//    }
//
//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    // Static utility method for filtering posts
//    public static List<Post> filterPostsByStatus(List<Post> posts, String status) {
//        List<Post> filteredList = new ArrayList<>();
//        for (Post post : posts) {
//            if (post.getStatus().equalsIgnoreCase(status)) {
//                filteredList.add(post);
//            }
//        }
//        return filteredList;
//    }
//}
