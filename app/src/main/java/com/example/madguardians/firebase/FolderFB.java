package com.example.madguardians.firebase;

import android.util.Log;

import com.example.madguardians.utilities.FirebaseController;
import com.example.madguardians.utilities.UploadCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FolderFB {
    private static final String TABLE_NAME = FirebaseController.FOLDER;
    private String folderId;
    private String name;
    private String domainId;
    private String userId;
    private String rootFolder = null;
    private static Queue<HashMap<String, Object>> insertQueue = new LinkedList<>();

    public FolderFB(String folderId, String name, String userId, String domainId, String rootFolder) {
        this.folderId = folderId;
        this.name = name;
        this.userId = userId;
        this.domainId = domainId;
        this.rootFolder = rootFolder;
    }

    public static void insertFolder(HashMap<String, Object> data) {
        insertQueue.add(data);
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
                }
            });
        }
    }

    public static void getFolder(String folderId, UploadCallback<FolderFB> callback) {
        FirebaseController.getMatchedCollection(TABLE_NAME, FirebaseController.getIdName(TABLE_NAME), folderId, new UploadCallback<List<HashMap<String, Object>>>(){
            @Override
            public void onSuccess(List<HashMap<String, Object>> result) {
                callback.onSuccess(mapHashMapToFolder(result.get(0)));
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("getData", "Failed to fetch data: " + e.getMessage());
                callback.onFailure(e);
            }
        });
    }

    public static void getFolders(String userId, UploadCallback<List<FolderFB>> callback) {
        ArrayList<FolderFB> list = new ArrayList<>();
        FirebaseController.getMatchedCollection(TABLE_NAME, "userId", userId, new UploadCallback<List<HashMap<String, Object>>>(){
            @Override
            public void onSuccess(List<HashMap<String, Object>> folderHashMapList) {
                for (HashMap<String, Object> folderHashMap : folderHashMapList) {
                    list.add(mapHashMapToFolder(folderHashMap));
                }
                callback.onSuccess(list);
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("getData", "Failed to fetch data: " + e.getMessage());
                callback.onFailure(e);
            }
        });
    }

    private static FolderFB mapHashMapToFolder(HashMap<String, Object> folderHashMap) {
        String folderId = (String) folderHashMap.get("folderId");
        String name = (String) folderHashMap.get("name");
        String userId = (String) folderHashMap.get("userId");
        String domainId = (String) folderHashMap.get("domainId");
        String rootFolder = (String) folderHashMap.get("rootFolder");
        return new FolderFB(folderId, name, userId, domainId, rootFolder);
    }

    public static void initialiseFolders() {
        Log.d("TAG", "initialiseFolders: here1");
        ArrayList<HashMap<String, Object>> hashMapList = new ArrayList<>();
        String userId = "U0001";
        hashMapList.add(createFolderData(userId, "Language", "D00001", null));
        hashMapList.add(createFolderData(userId, "Computer Science", "D00002", null));
        hashMapList.add(createFolderData(userId, "Physics", "D00003", null));
        hashMapList.add(createFolderData(userId, "Chemistry", "D00004", null));
        hashMapList.add(createFolderData(userId, "Biology", "D00005", null));
        hashMapList.add(createFolderData(userId, "Mathematics", "D00006", null));
        hashMapList.add(createFolderData(userId, "Music", "D00007", null));
        for (HashMap<String, Object> dataHashMap:hashMapList) {
            insertFolder(dataHashMap);
        }
    }

    public static HashMap<String, Object> createFolderData(String userId, String name, String domainId, String rootFolder) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("name", name);
        data.put("domainId", domainId);
        data.put("rootFolder", rootFolder);
        return data;
    }

    public String getFolderId() {
        return folderId;
    }

    public String getName() {
        return name;
    }

    public String getDomainId() {
        return domainId;
    }

    public String getRootFolder() {
        return rootFolder;
    }
}
