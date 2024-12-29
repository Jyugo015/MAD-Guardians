package com.example.madguardians.ui.course;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class PostViewModel{
    public static final ArrayList<PostViewModel> selectedMedias = new ArrayList<>();
    private final List<Uri> imagesUri = new ArrayList<>();
    private final List<Uri> videosUri = new ArrayList<>();
    private final List<Uri> pdfsUri = new ArrayList<>();
    private String title = "";
    private String description = "";
    private int level = 0;

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

    public void clear() {
        this.imagesUri.clear();
        this.videosUri.clear();
        this.pdfsUri.clear();
        this.description = "";
        this.title = "";
        this.level = 0;
    }

    public static void clearAll() {
        selectedMedias.clear();
    }
}
