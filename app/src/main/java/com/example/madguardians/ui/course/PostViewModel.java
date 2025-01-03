package com.example.madguardians.ui.course;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class PostViewModel{
    public static final ArrayList<PostViewModel> selectedMedias = new ArrayList<>();
    private final List<Uri> imagesUri = new ArrayList<>();
    private final List<Uri> videosUri = new ArrayList<>();
    private final List<Uri> pdfsUri = new ArrayList<>();
    private String imageSetId = null;
    private String videoSetId = null;
    private String pdfSetId = null;

    private String quizSetId = null;
    private boolean isImageUploaded = false;
    private boolean isVideosUploaded = false;
    private boolean isPdfsUploaded = false;
    private boolean isQuizUploaded = false;
    private boolean isUploading = false;

    public boolean isUploading() {
        return isUploading;
    }

    public void setUploading(boolean uploading) {
        isUploading = uploading;
    }

    private String title = "";
    private String description = "";
    private int level = 0;
    private String postId = null;


    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }


    public String getQuizSetId() {
        return quizSetId;
    }

    public void setQuizSetId(String quizSetId) {
        this.quizSetId = quizSetId;
    }

    public PostViewModel(int level) {
        this.level = level;
        selectedMedias.add(this);
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Uri> getPdfsUri() {
        return pdfsUri;
    }

    public List<Uri> getVideosUri() {
        return videosUri;
    }

    public List<Uri> getImagesUri() {
        return imagesUri;
    }

    public static PostViewModel getViewModel(int level) {
        for (PostViewModel viewModel : selectedMedias) {
            if (viewModel.getLevel() == level) {
                return viewModel;
            }
        }
        return null;
    }

    public int getLevel() {
        return level;
    }

    public String getImageSetId() {
        return imageSetId;
    }

    public void setImageSetId(String imageSetId) {
        this.imageSetId = imageSetId;
    }

    public String getPdfSetId() {
        return pdfSetId;
    }

    public void setPdfSetId(String pdfSetId) {
        this.pdfSetId = pdfSetId;
    }

    public String getVideoSetId() {
        return videoSetId;
    }

    public void setVideoSetId(String videoSetId) {
        this.videoSetId = videoSetId;
    }

    public boolean isQuizUploaded() {
        return isQuizUploaded;
    }

    public void setQuizUploaded(boolean quizUploaded) {
        isQuizUploaded = quizUploaded;
    }

    public void clear() {
        this.imagesUri.clear();
        this.videosUri.clear();
        this.pdfsUri.clear();
        this.imageSetId = null;
        this.videoSetId = null;
        this.pdfSetId = null;
        this.isImageUploaded = false;
        this.isPdfsUploaded = false;
        this.isVideosUploaded = false;
        this.isQuizUploaded = true; // need to chang to false
        this.isUploading = false;
        this.description = "";
        this.title = "";
        this.level = 0;
        this.postId = null;
    }

    public static void clearAll() {
        for (PostViewModel p: selectedMedias) {
            p.clear();
        }
        selectedMedias.clear();
    }

    public boolean isImageUploaded() {
        return isImageUploaded;
    }

    public void setImageUploaded(boolean imageUploaded) {
        isImageUploaded = imageUploaded;
    }

    public boolean isVideosUploaded() {
        return isVideosUploaded;
    }

    public void setVideosUploaded(boolean videosUploaded) {
        isVideosUploaded = videosUploaded;
    }

    public boolean isPdfsUploaded() {
        return isPdfsUploaded;
    }

    public void setPdfsUploaded(boolean pdfsUploaded) {
        isPdfsUploaded = pdfsUploaded;
    }
}
