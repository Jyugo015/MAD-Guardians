package com.example.madguardians.ui.course;

import java.util.ArrayList;

public class Media {
    private String mediaId;
    private String mediaSetId;
    private String url;
    private static ArrayList<Media> medias = new ArrayList<>();

    public Media(String mediaId, String mediaSetId, String url) {
        this.mediaId = mediaId;
        this.mediaSetId = mediaSetId;
        this.url = url;
    }

    public String getMediaId() {
        return mediaId;
    }

    public String getMediaSetId() {
        return mediaSetId;
    }

    public String getUrl() {
        return url;
    }

    public static void initialiseMedia() {
        medias.add(new Media("IMG001", "M001", "https://res.cloudinary.com/dmgpozfee/image/upload/v1730788530/cld-sample-5.jpg"));
        medias.add(new Media("IMG002", "M001", "https://res.cloudinary.com/dmgpozfee/image/upload/v1732898099/vfp2hoinnc2udodmftyv.jpg"));
        medias.add(new Media("IMG001", "M001", "https://res.cloudinary.com/dmgpozfee/image/upload/v1730788530/cld-sample-5.jpg"));
        medias.add(new Media("PDF001", "M002", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1732898566/yzre4gxv3weijurjt0rn"));
        medias.add(new Media("PDF002", "M002", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1732898566/yzre4gxv3weijurjt0rn"));
        medias.add(new Media("PDF003", "M002", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1732898566/yzre4gxv3weijurjt0rn"));
        medias.add(new Media("VID001", "M003", "https://res.cloudinary.com/dmgpozfee/video/upload/v1734399444/ku2zmz8wrd67bbcup1o4.mp4"));
        medias.add(new Media("VID002", "M003", "https://res.cloudinary.com/dmgpozfee/video/upload/v1734399444/ku2zmz8wrd67bbcup1o4.mp4"));
        medias.add(new Media("VID003", "M003", "https://res.cloudinary.com/dmgpozfee/video/upload/v1734399444/ku2zmz8wrd67bbcup1o4.mp4"));
    }

    public static ArrayList<Media> getMedias(String mediaSetId) {
        ArrayList<Media> mediaArrayList = new ArrayList<>();
        for (Media media:medias) {
            if (media.getMediaSetId().equals(mediaSetId)) {
                mediaArrayList.add(media);
            }
        }
        return mediaArrayList;
    }

    public static Media getMedia(String mediaId) {
        for (Media media:medias) {
            if (media.getMediaId().equals(mediaId)) {
                return media;
            }
        }
        return null;
    }
}
