package com.example.madguardians.firebase;

import android.util.Log;

import com.example.madguardians.utilities.FirebaseController;
import com.example.madguardians.utilities.UploadCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DomainFB {
    private static final String TABLE_NAME = FirebaseController.DOMAIN;
    private String domainId;

    private String domainName;

    private static Queue<HashMap<String, Object>> insertDomainQueue = new LinkedList<>();

    private DomainFB(String domainId, String domainName) {
        this.domainId = domainId;
        this.domainName = domainName;
    }

    public static void insertDomain(HashMap<String, Object> data) {
        insertDomainQueue.add(data);
        // start for the first, after that the method will call by itself, making sure no repetitive calling
        if (insertDomainQueue.size() == 1) {
            processQueue();
        }
    }

    private static void processQueue() {
        if (!insertDomainQueue.isEmpty()) {
            HashMap<String, Object> dataHashMap = insertDomainQueue.peek();
            FirebaseController.generateDocumentId(TABLE_NAME, new UploadCallback<String>() {
                @Override
                public void onSuccess(String id) {
                    FirebaseController.insertFirebase(TABLE_NAME, id, dataHashMap, new UploadCallback<HashMap<String, Object>>() {
                        @Override
                        public void onSuccess(HashMap<String, Object> result) {
                            Log.d("initializeDomainList", "onSuccess");
                            insertDomainQueue.poll();
                            processQueue();
                        }
                        @Override
                        public void onFailure(Exception e) {
                            Log.e("initializeDomainList", "onFailure");
                            insertDomainQueue.poll();
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

    public static void getDomain(String domainId, UploadCallback<DomainFB> callback) {
        FirebaseController.getMatchedCollection(TABLE_NAME, FirebaseController.getIdName(TABLE_NAME), domainId, new UploadCallback<List<HashMap<String, Object>>>(){
            @Override
            public void onSuccess(List<HashMap<String, Object>> result) {
                if (!result.isEmpty()) {
                    callback.onSuccess(mapHashMapToDomain(result.get(0)));
                } else callback.onFailure(new Exception("Domain not found"));
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("getData", "Failed to fetch data: " + e.getMessage());
                callback.onFailure(e);
            }
        });
    }

    private static DomainFB mapHashMapToDomain(HashMap<String, Object> data) {
        return new DomainFB((String) data.get("domainId"), (String) data.get("domainName"));
    }

    public static void initialiseDomains() {
        Log.d("TAG", "initializeCourseList: here1");
        ArrayList<HashMap<String, Object>> hashMapList = new ArrayList<>();
        hashMapList.add(createDomainData("Language"));
        hashMapList.add(createDomainData("Computer Science"));
        hashMapList.add(createDomainData("Physics"));
        hashMapList.add(createDomainData("Chemistry"));
        hashMapList.add(createDomainData("Biology"));
        hashMapList.add(createDomainData("Mathematics"));
        hashMapList.add(createDomainData("Music"));
        for (HashMap<String, Object> dataHashMap:hashMapList) {
            insertDomain(dataHashMap);
        }
    }

    public static HashMap<String, Object> createDomainData(String language) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("domainName", language);
        return data;
    }

    public static void getDomainsByIds(ArrayList<String> domainIds, UploadCallback<ArrayList<DomainFB>> callback){
        FirebaseController.getMatchedCollection(TABLE_NAME, FirebaseController.getIdName(TABLE_NAME), domainIds, new UploadCallback<List<HashMap<String, Object>>>(){
            @Override
            public void onSuccess(List<HashMap<String, Object>> results) {
                ArrayList<DomainFB> domainList = new ArrayList<>();
                for (HashMap<String, Object> result : results) {
                    domainList.add(mapHashMapToDomain(result));
                }
                Log.d("TAG", "onSuccess: " + domainList.toString());
                callback.onSuccess(domainList);
            }
            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public static void getDomains(UploadCallback<List<DomainFB>> callback) {
        FirebaseController.getAllCollection(TABLE_NAME, null, new UploadCallback<List<HashMap<String, Object>>>() {
            @Override
            public void onSuccess(List<HashMap<String, Object>> domainHashMapList) {
                ArrayList<DomainFB> list = new ArrayList<>();
                for (HashMap<String, Object> domainHashMap : domainHashMapList) {
                    list.add(mapHashMapToDomain(domainHashMap));
                }
                callback.onSuccess(list);
            }
            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public String getDomainId() {
        return domainId;
    }

    public String getDomainName() {
        return domainName;
    }
}
