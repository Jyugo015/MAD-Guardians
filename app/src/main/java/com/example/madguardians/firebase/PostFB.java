package com.example.madguardians.firebase;

import android.util.Log;

import com.example.madguardians.utilities.FirebaseController;
import com.example.madguardians.utilities.UploadCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PostFB implements Serializable{
    private static final String TABLE_NAME = FirebaseController.POST;
    private String postId;
    private String userId;
    private String title;
    private String description;
    private String imageSetId;
    private String videoSetId;
    private String fileSetId;
    private String domainId;
    private String folderId;
    private String date;
    private static Queue<HashMap<String, Object>> insertQueue = new LinkedList<>();
    private static Queue<UploadCallback<String>> postIdCallbackQueue = new LinkedList<>();

    private static final String TAG = "PostFB";

    public PostFB(String postId, String userId, String title, String description, String imageSetId, String videoSetId, String fileSetId, String domainId, String folderId, String date) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.imageSetId = imageSetId;
        this.videoSetId = videoSetId;
        this.fileSetId = fileSetId;
        this.domainId = domainId;
        this.folderId = folderId;
        this.date = date;
    }

    public PostFB(){}

    public static void getPost(String postId, UploadCallback<PostFB> callback) {
        FirebaseController.getMatchedCollection(TABLE_NAME, FirebaseController.getIdName(TABLE_NAME), postId, new UploadCallback<List<HashMap<String, Object>>>(){
            @Override
            public void onSuccess(List<HashMap<String, Object>> result) {
                if (result != null && ! result.isEmpty()) {
                    Log.d("TAG", "onSuccess: size " + result.size());
                    callback.onSuccess(mapHashMapToPost(result.get(0)));
                } else {
                    callback.onFailure(new NullPointerException("No post is found for this id"));
                }
            }
            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public static PostFB mapHashMapToPost(HashMap<String, Object> data) {
        String postId = (String) data.get("postId");
        String userId = (String) data.get("userId");
        String title = (String) data.get("title");
        String description = (String) data.get("description");
        String imageSetId = (String) data.get("imageSetId");
        String videoSetId = (String) data.get("videoSetId");
        String fileSetId = (String) data.get("fileSetId");
        String domainId = (String) data.get("domainId");
        String folderId = (String) data.get("folderId");
        String date = (String) data.get("date");
        return new PostFB(postId, userId, title, description, imageSetId, videoSetId, fileSetId, domainId, folderId, date);
    }

    public static void intializePosts() {
        Log.d("TAG", "initializeCourseList: here1");
        ArrayList<HashMap<String, Object>> hashMapList = new ArrayList<>();
        hashMapList.add(createPostData("U0001",  "Germany 1",  "This is Germany 1",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00001",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00002",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00003",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00001",  "10/12/2024"));
        hashMapList.add(createPostData("U0001",  "Germany 2",  "This is Germany 2",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00004",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00005",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00006",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00001",  "10/12/2024"));
        hashMapList.add(createPostData("U0001",  "Germany 3",  "This is Germany 3",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00007",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00008",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00009",    FirebaseController.findStarting(FirebaseController.FOLDER) + "00001",  "10/12/2024"));
        hashMapList.add(createPostData("U0001",  "France 1",  "This is France 1",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00010",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00011",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00012",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00001",  "11/12/2024"));
        hashMapList.add(createPostData("U0001",  "France 2",  "This is France 2",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00013",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00014",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00015",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00001",  "11/12/2024"));
        hashMapList.add(createPostData("U0001",  "France 3",  "This is France 3",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00016",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00017",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00018",    FirebaseController.findStarting(FirebaseController.FOLDER) + "00001",  "11/12/2024"));
        hashMapList.add(createPostData("U0002",  "Java 1",  "This is Java 1",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00019",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00020",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00021",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00002",  "12/12/2024"));
        hashMapList.add(createPostData("U0002",  "Java 2",  "This is Java 2",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00022",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00023",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00024",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00002",  "12/12/2024"));
        hashMapList.add(createPostData("U0002",  "Java 3",  "This is Java 3",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00025",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00026",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00027",    FirebaseController.findStarting(FirebaseController.FOLDER) + "00002",  "12/12/2024"));
        hashMapList.add(createPostData("U0002",  "Python 1",  "This is Python 1",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00028",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00029",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00030",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00002",  "13/12/2024"));
        hashMapList.add(createPostData("U0002",  "Python 2",  "This is Python 2",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00031",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00032",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00033",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00002",  "13/12/2024"));
        hashMapList.add(createPostData("U0002",  "Python 3",  "This is Python 3",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00034",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00035",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00036",    FirebaseController.findStarting(FirebaseController.FOLDER) + "00002",  "13/12/2024"));
        hashMapList.add(createPostData("U0003",  "Light 1",  "This is Light 1",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00037",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00038",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00039",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00003",  "14/12/2024"));
        hashMapList.add(createPostData("U0003",  "Light 2",  "This is Light 2",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00040",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00041",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00042",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00003",  "14/12/2024"));
        hashMapList.add(createPostData("U0003",  "Light 3",  "This is Light 3",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00043",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00044",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00045",    FirebaseController.findStarting(FirebaseController.FOLDER) + "00003",  "14/12/2024"));
        hashMapList.add(createPostData("U0003",  "Gravity 1",  "This is Gravity 1",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00046",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00047",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00048",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00003",  "15/12/2024"));
        hashMapList.add(createPostData("U0003",  "Gravity 2",  "This is Gravity 2",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00049",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00050",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00051",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00003",  "15/12/2024"));
        hashMapList.add(createPostData("U0003",  "Gravity 3",  "This is Gravity 3",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00052",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00053",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00054",    FirebaseController.findStarting(FirebaseController.FOLDER) + "00003",  "15/12/2024"));
        hashMapList.add(createPostData("U0004",  "Chemical Equilibrium 1",  "This is Chemical Equilibrium 1",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00055",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00056",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00057",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00004",  "16/12/2024"));
        hashMapList.add(createPostData("U0004",  "Chemical Equilibrium 2",  "This is Chemical Equilibrium 2",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00058",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00059",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00060",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00004",  "16/12/2024"));
        hashMapList.add(createPostData("U0004",  "Chemical Equilibrium 3",  "This is Chemical Equilibrium 3",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00061",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00062",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00063",    FirebaseController.findStarting(FirebaseController.FOLDER) + "00004",  "16/12/2024"));
        hashMapList.add(createPostData("U0004",  "Nuclear 1",  "This is Nuclear 1",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00064",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00065",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00066",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00004",  "20/12/2024"));
        hashMapList.add(createPostData("U0004",  "Nuclear 2",  "This is Nuclear 2",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00067",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00068",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00069",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00004",  "20/12/2024"));
        hashMapList.add(createPostData("U0004",  "Nuclear 3",  "This is Nuclear 3",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00070",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00071",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00072",    FirebaseController.findStarting(FirebaseController.FOLDER) + "00004",  "20/12/2024"));
        hashMapList.add(createPostData("U0005",  "Plant 1",  "This is Plant 1",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00073",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00074",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00075",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00005",  "21/12/2024"));
        hashMapList.add(createPostData("U0005",  "Plant 2",  "This is Plant 2",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00076",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00077",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00078",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00005",  "21/12/2024"));
        hashMapList.add(createPostData("U0005",  "Plant 3",  "This is Plant 3",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00079",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00080",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00081",    FirebaseController.findStarting(FirebaseController.FOLDER) + "00005",  "21/12/2024"));
        hashMapList.add(createPostData("U0005",  "Cellular Biology 1",  "This is Cellular Biology 1",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00082",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00083",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00084",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00005",  "22/12/2024"));
        hashMapList.add(createPostData("U0005",  "Cellular Biology 2",  "This is Cellular Biology 2",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00085",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00086",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00087",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00005",  "22/12/2024"));
        hashMapList.add(createPostData("U0005",  "Cellular Biology 3",  "This is Cellular Biology 3",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00088",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00089",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00090",    FirebaseController.findStarting(FirebaseController.FOLDER) + "00005",  "22/12/2024"));
        hashMapList.add(createPostData("U0006",  "Algebra 1",  "This is Algebra 1",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00091",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00092",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00093",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00006",  "23/12/2024"));
        hashMapList.add(createPostData("U0006",  "Algebra 2",  "This is Algebra 2",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00094",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00095",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00096",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00006",  "23/12/2024"));
        hashMapList.add(createPostData("U0006",  "Algebra 3",  "This is Algebra 3",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00097",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00098",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00099",    FirebaseController.findStarting(FirebaseController.FOLDER) + "00006",  "23/12/2024"));
        hashMapList.add(createPostData("U0006",  "Statistic 1",  "This is Statistic 1",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00100",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00101",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00102",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00006",  "24/12/2024"));
        hashMapList.add(createPostData("U0006",  "Statistic 2",  "This is Statistic 2",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00103",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00104",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00105",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00006",  "24/12/2024"));
        hashMapList.add(createPostData("U0006",  "Statistic 3",  "This is Statistic 3",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00106",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00107",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00108",    FirebaseController.findStarting(FirebaseController.FOLDER) + "00006",  "24/12/2024"));
        hashMapList.add(createPostData("U0007",  "Piano 1",  "This is Piano 1",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00109",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00110",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00111",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00007",  "25/12/2024"));
        hashMapList.add(createPostData("U0007",  "Piano 2",  "This is Piano 2",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00112",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00113",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00114",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00007",  "25/12/2024"));
        hashMapList.add(createPostData("U0007",  "Piano 3",  "This is Piano 3",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00115",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00116",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00117",    FirebaseController.findStarting(FirebaseController.FOLDER) + "00007",  "25/12/2024"));
        hashMapList.add(createPostData("U0007",  "Guitar 1",  "This is Guitar 1",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00118",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00119",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00120",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00007",  "26/12/2024"));
        hashMapList.add(createPostData("U0007",  "Guitar 2",  "This is Guitar 2",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00121",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00122",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00123",   FirebaseController.findStarting(FirebaseController.FOLDER) + "00007",  "26/12/2024"));
        hashMapList.add(createPostData("U0007",  "Guitar 3",  "This is Guitar 3",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00124",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00125",  FirebaseController.findStarting(FirebaseController.MEDIASET) + "00126",    FirebaseController.findStarting(FirebaseController.FOLDER) + "00007",  "26/12/2024"));

        for (HashMap<String, Object> dataHashMap:hashMapList) {
            insertPost(dataHashMap, new UploadCallback<String>() {
                public void onSuccess(String result) {}
                public void onFailure(Exception e) {}
            });
        }
    }

    public static void insertPost(HashMap<String, Object> dataHashMap, UploadCallback<String> postIdCallback) {
        insertQueue.add(dataHashMap);
        postIdCallbackQueue.add(postIdCallback);
        // start for the first, after that the method will call by itself, making sure no repetitive calling
        if (insertQueue.size() == 1) {
            processQueue();
        }
    }

    private static void processQueue() {
        if (!insertQueue.isEmpty()) {
            HashMap<String, Object> dataHashMap = insertQueue.peek();
            UploadCallback<String> postIdCallback = postIdCallbackQueue.peek();
            if (dataHashMap.get(FirebaseController.getIdName(TABLE_NAME)) == null) {
                FirebaseController.generateDocumentId(TABLE_NAME, new UploadCallback<String>() {
                    @Override
                    public void onSuccess(String id) {
                        FirebaseController.insertFirebase(TABLE_NAME, id, dataHashMap, new UploadCallback<HashMap<String, Object>>() {
                            @Override
                            public void onSuccess(HashMap<String, Object> result) {
                                Log.d("initializePostList", "onSuccess");
                                insertQueue.poll();
                                postIdCallbackQueue.poll();
                                postIdCallback.onSuccess((String) result.get(FirebaseController.getIdName(TABLE_NAME)));
                                processQueue();
                            }
                            @Override
                            public void onFailure(Exception e) {
                                Log.e("initializePostList", "onFailure");
                                insertQueue.poll();
                                postIdCallback.onFailure(e);
                                processQueue();
                            }
                        });
                    }
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("TAG", "onFailure: ", e);
                    }
                });
            } else {
                String id = (String) dataHashMap.get(FirebaseController.getIdName(TABLE_NAME));
                FirebaseController.insertFirebase(TABLE_NAME, id, dataHashMap, new UploadCallback<HashMap<String, Object>>() {
                    @Override
                    public void onSuccess(HashMap<String, Object> result) {
                        Log.d("initializeDomainList", "onSuccess");
                        insertQueue.poll();
                        postIdCallback.onSuccess((String) result.get(FirebaseController.getIdName(TABLE_NAME)));
                        processQueue();
                    }
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("initializeDomainList", "onFailure");
                        insertQueue.poll();
                        postIdCallback.onFailure(e);
                        processQueue();
                    }
                });
            }

        }
    }

//    public static void updatePost(HashMap<String, Object> hashMap) {
//        String postId = (String) hashMap.get("postId");
//        FirebaseController.updateFirebase(TABLE_NAME, hashMap, new UploadCallback<HashMap<String, Object>>(){
//            @Override
//            public void onSuccess(HashMap<String, Object> result) {
//                Log.e(TAG, "onSuccess: updatePost " + postId);
//            }
//            @Override
//            public void onFailure(Exception e) {
//                Log.e(TAG, "onFailure: updatePost", e);
//            }
//        });
//    }

    public static HashMap<String, Object> createPostData(String userId, String title, String description, String imageSetId, String videoSetId, String fileSetId, String folderId, String date) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("title", title);
        data.put("description", description);
        data.put("imageSetId", imageSetId);
        data.put("videoSetId", videoSetId);
        data.put("fileSetId", fileSetId);
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
