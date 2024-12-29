package com.example.madguardians.firebase;

import android.util.Log;

import com.example.madguardians.utilities.FirebaseController;
import com.example.madguardians.utilities.UploadCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Media {
    private static String TABLE_NAME = FirebaseController.MEDIA;
    private String mediaId;
    private String mediaSetId;
    private String url;
    private static Queue<HashMap<String, Object>> insertQueue = new LinkedList<>();

    private Media(String mediaId, String mediaSetId, String url) {
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
        ArrayList<HashMap<String, Object>> hashMapList = new ArrayList<>();
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00001", "https://res.cloudinary.com/dmgpozfee/image/upload/v1730788530/cld-sample-5.jpg"));
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00001", "https://res.cloudinary.com/dmgpozfee/image/upload/v1732898099/vfp2hoinnc2udodmftyv.jpg"));
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00001", "https://res.cloudinary.com/dmgpozfee/image/upload/v1730788530/cld-sample-5.jpg"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00002", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1732898566/yzre4gxv3weijurjt0rn"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00002", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1732898566/yzre4gxv3weijurjt0rn"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00002", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1732898566/yzre4gxv3weijurjt0rn"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00003", "https://res.cloudinary.com/dmgpozfee/video/upload/v1734399444/ku2zmz8wrd67bbcup1o4.mp4"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00003", "https://res.cloudinary.com/dmgpozfee/video/upload/v1734399444/ku2zmz8wrd67bbcup1o4.mp4"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00003", "https://res.cloudinary.com/dmgpozfee/video/upload/v1734399444/ku2zmz8wrd67bbcup1o4.mp4"));
        for (HashMap<String, Object> dataHashMap:hashMapList) {
            insertMedia(dataHashMap);
        }
    }

    public static void insertMedia(HashMap<String, Object> data) {
        insertQueue.add(data);
        // start for the first, after that the method will call by itself, making sure no repetitive calling
        if (insertQueue.size() == 1) {
            processQueue();
        }
    }

    private static void processQueue() {
        if (!insertQueue.isEmpty()) {
            HashMap<String, Object> dataHashMap = insertQueue.peek();
            String TABLE_NAME = (String) dataHashMap.get("tableName");
            FirebaseController.generateDocumentId(TABLE_NAME, new UploadCallback<String>() {
                @Override
                public void onSuccess(String id) {
                    FirebaseController.insertFirebase(TABLE_NAME, id, dataHashMap, new UploadCallback<HashMap<String, Object>>() {
                        @Override
                        public void onSuccess(HashMap<String, Object> result) {
                            Log.d("initializeDomainList", "onSuccess");
                            insertQueue.poll();
                            processQueue();
                        }
                        @Override
                        public void onFailure(Exception e) {
                            Log.e("initializeDomainList", "onFailure");
                            insertQueue.poll();
                            processQueue();
                        }
                    });
                }
                @Override
                public void onFailure(Exception e) {
                }
            });
        }
    }

    public static HashMap<String, Object> createMediaData(String tableName, String mediaSetId, String url) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("tableName", tableName);
        data.put("mediaSetId", mediaSetId);
        data.put("url", url);
        return data;
    }

    public static void getMedias(String mediaSetId, UploadCallback<List<Media>> callback) {
        Log.d("TAG", "mediaSetId: " + mediaSetId);
        FirebaseController.getMatchedCollection(TABLE_NAME, FirebaseController.getIdName(FirebaseController.MEDIASET), mediaSetId, new UploadCallback<List<HashMap<String, Object>>>(){
            @Override
            public void onSuccess(List<HashMap<String, Object>> mediaHashMapList) {
                ArrayList<Media> medias = new ArrayList<>();
                for (HashMap<String, Object> mediaHashMap:mediaHashMapList) {
                    medias.add(mapHashMapToMedia(mediaHashMap));
                }
                callback.onSuccess(medias);
            }
            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    private static Media mapHashMapToMedia(HashMap<String, Object> media) {
        return new Media((String) media.get("mediaId"), (String) media.get("mediaSetId"), (String) media.get("url"));
    }

    public static void getMedia(String mediaId, UploadCallback<Media> callback) {
        FirebaseController.getMatchedCollection(TABLE_NAME, FirebaseController.getIdName(TABLE_NAME), mediaId, new UploadCallback<List<HashMap<String, Object>>>(){
            @Override
            public void onSuccess(List<HashMap<String, Object>> result) {
                Media media = mapHashMapToMedia(result.get(0));
                callback.onSuccess(media);
            }
            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
