package com.example.madguardians.firebase;

import android.util.Log;

import com.example.madguardians.utilities.FirebaseController;
import com.example.madguardians.utilities.UploadCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Post {
    private static final String TABLE_NAME = FirebaseController.POST;
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
    private static List<HashMap<String, Object>> list = new ArrayList<>();
    private static Queue<HashMap<String, Object>> insertQueue = new LinkedList<>();

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

    public static void getPost(String postId, UploadCallback<Post> callback) {
        FirebaseController.getMatchedCollection(TABLE_NAME, FirebaseController.getIdName(TABLE_NAME), postId, new UploadCallback<List<HashMap<String, Object>>>(){
            @Override
            public void onSuccess(List<HashMap<String, Object>> result) {
                Log.d("TAG", "onSuccess: size " + result.size());
                callback.onSuccess(mapHashMapToPost(result.get(0)));
            }
            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public static Post mapHashMapToPost(HashMap<String, Object> data) {
        String postId = (String) data.get("postId");
        String userId = (String) data.get("userId");
        String title = (String) data.get("title");
        String description = (String) data.get("description");
        String imageSetId = (String) data.get("imageSetId");
        String videoSetId = (String) data.get("videoSetId");
        String fileSetId = (String) data.get("fileSetId");
        String quizId = (String) data.get("quizId");
        String domainId = (String) data.get("domainId");
        String folderId = (String) data.get("folderId");
        String date = (String) data.get("date");
        return new Post(postId, userId, title, description, imageSetId, videoSetId, fileSetId, quizId, domainId, folderId, date);
    }

    public static void intializePosts() {
        Log.d("TAG", "initializeCourseList: here1");
        ArrayList<HashMap<String, Object>> hashMapList = new ArrayList<>();
        hashMapList.add(createPostData("James",  "Java1",  "This is description 1",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00001",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00003",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00002",  FirebaseController.findStarting(FirebaseController.QUIZ) + "00001",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00001",  "11/12/2024"));
        hashMapList.add(createPostData("JJ",  "Java2",  "This is description 2",  null,  null,  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00002",  FirebaseController.findStarting(FirebaseController.QUIZ) + "00001",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00002",  "11/12/2024"));
        hashMapList.add(createPostData("Michiel",  "Java3",  "This is description 3",  null,  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00003",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00002",  FirebaseController.findStarting(FirebaseController.QUIZ) + "00001",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00001",  "11/12/2024"));
        hashMapList.add(createPostData("Michiel",  "Java4",  "This is description 4",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00001",  null,  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00002",  FirebaseController.findStarting(FirebaseController.QUIZ) + "00001",    FirebaseController.findStarting(FirebaseController.FOLDER) + "00001",  "11/12/2024"));
        for (HashMap<String, Object> dataHashMap:hashMapList) {
            insertPost(dataHashMap);
        }
    }

    public static void uploadNextPost(Queue<HashMap<String, Object>> postQueue, UploadCallback<List<HashMap<String, Object>>> callback) {
        if (postQueue.isEmpty()) {
            callback.onSuccess(list); // All post is uploaded
            list.clear();
            return;
        }
        HashMap<String, Object> post = postQueue.poll();
        FirebaseController.generateDocumentId(TABLE_NAME, new UploadCallback<String>() {
            @Override
            public void onSuccess(String id) {
                FirebaseController.insertFirebase(TABLE_NAME, id, post, new UploadCallback<HashMap<String, Object>>() {
                    @Override
                    public void onSuccess(HashMap<String, Object> result) {
                        list.add(result);
                        uploadNextPost(postQueue, callback);
                    }
                    @Override
                    public void onFailure(Exception e) {
                        callback.onFailure(e);
                    }
                });
            }
            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    private static void insertPost(HashMap<String, Object> dataHashMap) {
        insertQueue.add(dataHashMap);
        // start for the first, after that the method will call by itself, making sure no repetitive calling
        if (insertQueue.size() == 1) {
            processQueue();
        }
    }

    private static void processQueue() {
        if (!insertQueue.isEmpty()) {
            HashMap<String, Object> dataHashMap = insertQueue.peek();
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
                    Log.e("TAG", "onFailure: ", e);
                }
            });
        }
    }

    public static HashMap<String, Object> createPostData(String userId, String title, String description, String imageSetId, String videoSetId, String fileSetId, String quizId, String folderId, String date) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("title", title);
        data.put("description", description);
        data.put("imageSetId", imageSetId);
        data.put("videoSetId", videoSetId);
        data.put("fileSetId", fileSetId);
        data.put("quizId", quizId);
        data.put("folderId", folderId);
        data.put("date", date);
        return data;
    }

    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageSetId() {
        return imageSetId;
    }

    public String getVideoSetId() {
        return videoSetId;
    }

    public String getFileSetId() {
        return fileSetId;
    }

    public String getQuizId() {
        return quizId;
    }

    public String getDomainId() {
        return domainId;
    }

    public String getFolderId() {
        return folderId;
    }

    public String getDate() {
        return date;
    }
}
