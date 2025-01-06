package com.example.madguardians.firebase;

import android.util.Log;

import com.example.madguardians.utilities.FirebaseController;
import com.example.madguardians.utilities.UploadCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MediaFB {
    private static String TABLE_NAME = FirebaseController.MEDIA;
    private String mediaId;
    private String mediaSetId;
    private String url;
    private static Queue<HashMap<String, Object>> insertQueue = new LinkedList<>();
    private static Queue<HashMap<String, Object>> insertMediaSetQueue = new LinkedList<>();
    private static Queue<UploadCallback<Boolean>> callbackQueue = new LinkedList<>();

    private static UploadCallback<Boolean> isUploadedCallback = null;

    private MediaFB(String mediaId, String mediaSetId, String url) {
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
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00001", "https://res.cloudinary.com/dmgpozfee/image/upload/a1-german-online-course_image_hzllo1.jpg"));
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00001", "https://res.cloudinary.com/dmgpozfee/image/upload/german-grammar-3_la1wxo.jpg"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00002", "https://res.cloudinary.com/dmgpozfee/video/upload/videogerman_lvyllv.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00003", "https://res.cloudinary.com/dmgpozfee/image/upload/basics-of-german-a-brief-introduction-to-the-german-a1-level_bmqpku.pdf"));
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00004", "https://res.cloudinary.com/dmgpozfee/image/upload/images_1_aq5u45.png"));
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00004", "https://res.cloudinary.com/dmgpozfee/image/upload/Examples-German-Verbs_n4t7wa.webp"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00005", "https://res.cloudinary.com/dmgpozfee/video/upload/videoGerman2_gl00ik.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00006", "https://res.cloudinary.com/dmgpozfee/image/upload/FSI-German-Basic-Course_sdbnhi.pdf"));
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00007", "https://res.cloudinary.com/dmgpozfee/image/upload/images_1_aq5u45.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00008", "https://res.cloudinary.com/dmgpozfee/video/upload/videoGerman_op8ndh.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00009", "https://res.cloudinary.com/dmgpozfee/image/upload/A-Foundation-Course-in-Reading-German-1652727646_kuklju.pdf"));
        for (HashMap<String, Object> dataHashMap:hashMapList) {
            insertMedia(dataHashMap, new UploadCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    Log.d(TABLE_NAME, "onSuccess:  initialise media");
                }
                @Override
                public void onFailure(Exception e) {

                }
            });
        }
    }

    public static void insertMedia(HashMap<String, Object> data, UploadCallback<Boolean> isUploadedCallback) {
        insertQueue.add(data);
        callbackQueue.add(isUploadedCallback);
        // start for the first, after that the method will call by itself, making sure no repetitive calling
        if (insertQueue.size() == 1) {
            processQueue();
        }
    }

    private static void processQueue() {
        if (!insertQueue.isEmpty()) {
            HashMap<String, Object> dataHashMap = insertQueue.peek();
            isUploadedCallback = callbackQueue.peek();
            String tableName = (String) dataHashMap.remove("tableName");
            Log.d(TABLE_NAME, "processQueue: tableName: "+ tableName);
            FirebaseController.generateDocumentId(tableName, new UploadCallback<String>() {
                @Override
                public void onSuccess(String id) {
                    Log.d(TABLE_NAME, "processQueue: mediaId: "+ id);
                    FirebaseController.insertFirebase(TABLE_NAME, id, dataHashMap, new UploadCallback<HashMap<String, Object>>() {
                        @Override
                        public void onSuccess(HashMap<String, Object> result) {
                            Log.d("processQueue media", "onSuccess");
                            insertQueue.poll();
                            callbackQueue.poll();
                            if((Boolean) dataHashMap.get("isLast") != null && (Boolean) dataHashMap.get("isLast")) isUploadedCallback.onSuccess(true);
                            processQueue();
                        }
                        @Override
                        public void onFailure(Exception e) {
                            Log.e("processQueue media", "onFailure", e);
                            insertQueue.poll();
                            callbackQueue.poll();
                            processQueue();
                        }
                    });
                }
                @Override
                public void onFailure(Exception e) {isUploadedCallback.onFailure(e);}
            });
        } else {
            isUploadedCallback.onSuccess(true);
        }
    }

    public static HashMap<String, Object> createMediaData(String tableName, String mediaSetId, String url) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("tableName", tableName);
        data.put("mediaSetId", mediaSetId);
        data.put("url", url);
        return data;
    }

    public static void initialiseMediaSetId() {
        for (int i = 1; i <= 15; i++) {
            insertMediaSet();
        }
    }

    public static void insertMediaSet() {
        insertMediaSetQueue.add(new HashMap<String, Object>());
        // start for the first, after that the method will call by itself, making sure no repetitive calling
        if (insertQueue.size() == 1) {
            processMediaSetQueue();
        }
    }

    private static void processMediaSetQueue() {
        if (!insertQueue.isEmpty()) {
            HashMap<String, Object> dataHashMap = insertQueue.peek();
            FirebaseController.generateDocumentId(FirebaseController.MEDIASET, new UploadCallback<String>() {
                @Override
                public void onSuccess(String id) {}
                @Override
                public void onFailure(Exception e) {}
            });
        }
    }

    public static void getMedias(String mediaSetId, UploadCallback<List<MediaFB>> callback) {
        Log.d(TABLE_NAME, "mediaSetId: " + mediaSetId);
        FirebaseController.getMatchedCollection(TABLE_NAME, FirebaseController.getIdName(FirebaseController.MEDIASET), mediaSetId, new UploadCallback<List<HashMap<String, Object>>>(){
            @Override
            public void onSuccess(List<HashMap<String, Object>> mediaHashMapList) {
                ArrayList<MediaFB> medias = new ArrayList<>();
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

    private static MediaFB mapHashMapToMedia(HashMap<String, Object> media) {
        return new MediaFB((String) media.get("mediaId"), (String) media.get("mediaSetId"), (String) media.get("url"));
    }

    public static void getMedia(String mediaId, UploadCallback<MediaFB> callback) {
        FirebaseController.getMatchedCollection(TABLE_NAME, FirebaseController.getIdName(TABLE_NAME), mediaId, new UploadCallback<List<HashMap<String, Object>>>(){
            @Override
            public void onSuccess(List<HashMap<String, Object>> result) {
                MediaFB media = mapHashMapToMedia(result.get(0));
                callback.onSuccess(media);
            }
            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
