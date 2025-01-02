package com.example.madguardians.utilities;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirebaseController {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String USER = "user";
    public static final String COURSE = "course";
    public static final String POST = "post";
    public static final String DOMAIN = "domain";
    public static final String MEDIA = "media";
    public static final String IMAGE = "image";
    public static final String VIDEO = "video";
    public static final String PDF = "pdf";
    public static final String MEDIASET = "mediaSet";
    public static final String COMMENT = "comment";
    public static final String COLLECTION = "collection";
    public static final String QUIZ = "quiz";
    public static final String FOLDER = "folder";
    public static final String TAG = "FirebaseController";

    public static void insertFirebase(String tableName, String id, HashMap<String, Object> dataHashMap, UploadCallback callback) {
        Log.d("TAG", "insertFirebase: here1");
        String finalTable;
        if (tableName.equals(IMAGE) || tableName.equals(VIDEO) || tableName.equals(PDF))
            finalTable = MEDIA;
        else {
            finalTable = tableName;
        }
        dataHashMap.put(getIdName(finalTable), id);
        db.collection(finalTable).whereEqualTo(getIdName(finalTable), id).get()
            .addOnCompleteListener(task -> {
//            if (task.isSuccessful() && !task.getResult().isEmpty()) {
//                Log.d("TAG", "insertFirebase: here2");
//                callback.onFailure(new Exception("Document already exists"));
//            } else {
                Log.d("TAG", "insertFirebase: here3");
                dataHashMap.put(getIdName(finalTable), id); // Ensure the fieldName and id are part of the new data
                db.collection(finalTable)
                        .document(id)
                        .set(dataHashMap)
                        .addOnSuccessListener(v -> callback.onSuccess(dataHashMap))
                        .addOnFailureListener(e -> callback.onFailure(new Exception("Failed to create a new document")));
//            }
//            Log.d("TAG", "insertFirebase: here4");
        }).addOnFailureListener(callback::onFailure);
    }

    public static void updateFirebase(String tableName, HashMap<String, Object> dataHashMap, UploadCallback callback) {
        String finalTable;
        if (tableName.equals(IMAGE) || tableName.equals(VIDEO) || tableName.equals(PDF))
            finalTable = MEDIA;
        else
            finalTable = tableName;

        String id = (String) dataHashMap.get(getIdName(tableName));
        db.collection(finalTable).whereEqualTo(getIdName(finalTable), id).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Iterate through matching documents (even though there should only be one)
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection(tableName)
                                    .document(document.getId()) // Get the document ID
                                    .set(dataHashMap, SetOptions.merge()) // Update the document
                                    .addOnSuccessListener(v -> callback.onSuccess(document))
                                    .addOnFailureListener(callback::onFailure);
                        }
                    } else {
                        callback.onFailure(new Exception("No matching document found"));
                    }
                }).addOnFailureListener(callback::onFailure);
    }
    public static void getMatchedCollection(String tableName, String fieldName, String fieldValue, UploadCallback<List<HashMap<String, Object>>> callback) {

        db.collection(tableName)
                .whereEqualTo(fieldName, fieldValue) // Query to match fieldName with fieldValue
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<HashMap<String, Object>> results = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        HashMap<String, Object> data = (HashMap<String, Object>) document.getData();
                        results.add(data);
                    }
                    callback.onSuccess(results); // Return the matched data
                })
                .addOnFailureListener(callback::onFailure);
    }

    public static void getMatchedCollection(String tableName, String fieldName, List<String> fieldValues, UploadCallback<List<HashMap<String, Object>>> callback) {
        if (fieldValues.size() > 10) {
            callback.onFailure(new IllegalArgumentException("Firebase whereIn supports a maximum of 10 values per query."));
            return;
        }

        db.collection(tableName)
                .whereIn(fieldName, fieldValues) // Query to match fieldName with any value in fieldValues
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<HashMap<String, Object>> results = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        HashMap<String, Object> data = (HashMap<String, Object>) document.getData();
                        results.add(data);
                    }
                    callback.onSuccess(results); // Return the matched data
                })
                .addOnFailureListener(callback::onFailure);
    }

    public static void getAllCollection(String tableName, @Nullable List<String> keys, UploadCallback<List<HashMap<String, Object>>> callback) {
        db.collection(tableName).get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<HashMap<String, Object>> resultList = new ArrayList<>();
                if (keys != null) {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        HashMap<String, Object> filteredData = new HashMap<>();
                        for (String key : keys) {
                            if (document.getData().containsKey(key))
                                filteredData.put(key, document.getData().get(key));
                        }
                        resultList.add(filteredData);
                    }
                } else {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        resultList.add((HashMap<String, Object>) document.getData());
                    }
                }

                callback.onSuccess(resultList);
            })
            .addOnFailureListener(callback::onFailure);
    }

    public static boolean isExistEntry(String tableName, HashMap<String, Object> dataHashMap) {
        Query query = db.collection(tableName);

        // Add whereEqualTo for each entry in the HashMap
        for (String key : dataHashMap.keySet()) {
            Object value = dataHashMap.get(key);
            query = query.whereEqualTo(key, value);
        }

        // Execute the query
        final boolean[] exists = {false}; // To store the result
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                exists[0] = !task.getResult().isEmpty(); // Check if any document matches
            } else {
                Log.e("isExist", "Error: " + task.getException());
            }
        });

        return exists[0];
    }

    public static void generateDocumentId(String tableName, UploadCallback<String> callback) {
        String starting = findStarting(tableName);
        String finalTable;
        if (tableName.equals(IMAGE) || tableName.equals(VIDEO) || tableName.equals(PDF))
            finalTable = MEDIA;
        else
            finalTable = tableName;

        String idName = getIdName(finalTable);
        Log.d("TAG", "generateDocumentId: here1");

        db.collection(finalTable)
                .whereGreaterThanOrEqualTo(idName, starting) // Filter for IDs with the correct prefix
                .whereLessThanOrEqualTo(idName, starting + "\uffff") // Ensure lexicographical range
                .orderBy(idName, Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        QueryDocumentSnapshot lastDocument = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        String lastId = lastDocument.getString(idName);

                        if (lastId != null) {
                            int idNumber = Integer.parseInt(lastId.substring(starting.length())) + 1;
                            String newId = starting + String.format("%05d", idNumber);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put(getIdName(finalTable), newId);
                            Log.d(TAG, "generateDocumentId: lastId: " + lastId);
                            Log.d(TAG, "generateDocumentId: newId: " + newId);
                            // create a new entry for that table
                            db.collection(finalTable).document(newId)
                                .set(hashMap)
                                .addOnSuccessListener(v -> {
                                    callback.onSuccess(newId);
                                });
                        }
                    } else {
                        String newId = starting + "00001";
                        callback.onSuccess(newId);
                    }
                });
    }

    public static String getIdName(String tableName) {
        return tableName + "Id";
    }

    public static String findStarting(String tableName) {
        if (tableName.equals(COURSE)) return "COU";
        else if (tableName.equals(POST)) return "P";
        else if (tableName.equals(DOMAIN)) return "D";
        else if (tableName.equals(IMAGE)) return "IMG";
        else if (tableName.equals(VIDEO)) return "VID";
        else if (tableName.equals(PDF)) return "PDF";
        else if (tableName.equals(MEDIASET)) return "MST";
        else if (tableName.equals(COMMENT)) return "COM";
        else if (tableName.equals(COLLECTION)) return "COL";
        else if (tableName.equals(FOLDER)) return "F";
        else if (tableName.equals(USER)) return "U";
        else if (tableName.equals(QUIZ)) return "Q";
        else return "";
    }
}
