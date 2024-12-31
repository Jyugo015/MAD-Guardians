package com.example.madguardians.firebase;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.madguardians.utilities.FirebaseController;
import com.example.madguardians.utilities.UploadCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CourseFB {
    private static final String TABLE_NAME = FirebaseController.COURSE;
    private String courseId;
    private String title;
    private String author;
    private String description;
    private String coverImage;
    private String post1;
    private String post2;
    private String post3;
    private String folderId;
    private String domainId;
    private String date;
    private static final String TAG = "Course";

    private static Queue<HashMap<String, Object>> insertCourseQueue = new LinkedList<>();

    private CourseFB(){
        // Default constructor required for Firebase
    }
    private CourseFB(String courseId, String title, @Nullable String author, @Nullable String description, @Nullable String coverImage, @Nullable String post1, @Nullable String post2, @Nullable String post3, @Nullable String domainId, @Nullable String folderId, @Nullable String date) {
        this.courseId = courseId;
        this.title = title;
        this.author = author;
        this.description = description;
        this.coverImage = coverImage;
        this.post1 = post1;
        this.post2 = post2;
        this.post3 = post3;
        this.domainId = domainId;
        this.folderId = folderId;
        this.date = date;
    }
    public static void insertCourse(HashMap<String, Object> data, UploadCallback<Void> callback) {
        insertCourseQueue.add(data);
        // start for the first, after that the method will call by itself, making sure no repetitive calling
        if (insertCourseQueue.size() == 1) {
            processQueue(callback);
        }
    }
    private static void processQueue(UploadCallback<Void> callback) {
        if (!insertCourseQueue.isEmpty()) {
            HashMap<String, Object> dataHashMap = insertCourseQueue.peek();
            FirebaseController.generateDocumentId(TABLE_NAME, new UploadCallback<String>() {
                @Override
                public void onSuccess(String id) {
                    FirebaseController.insertFirebase(TABLE_NAME, id, dataHashMap, new UploadCallback<HashMap<String, Object>>() {
                        @Override
                        public void onSuccess(HashMap<String, Object> result) {
                            Log.d("initializeDomainList", "onSuccess");
                            insertCourseQueue.poll();
                            processQueue(callback);
                        }
                        @Override
                        public void onFailure(Exception e) {
                            Log.e("initializeDomainList", "onFailure");
                            insertCourseQueue.poll();
                            processQueue(callback);
                        }
                    });
                }
                @Override
                public void onFailure(Exception e) {

                }
            });
        }
    }

    public static void getCourse(String courseId, UploadCallback<CourseFB> callback) {
        CourseFB courseFB = null;
        FirebaseController.getMatchedCollection(TABLE_NAME, FirebaseController.getIdName(TABLE_NAME), courseId, new UploadCallback<List<HashMap<String, Object>>>(){
            @Override
            public void onSuccess(List<HashMap<String, Object>> result) {
                if (!result.isEmpty()) {
                    // Assuming only one match for courseId
                    HashMap<String, Object> data = result.get(0);
                    CourseFB courseFB = mapHashMapToCourse(data);
                    callback.onSuccess(courseFB); // Return the course object via callback
                } else {
                    callback.onSuccess(null); // No matching course found
                }
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("getData", "Failed to fetch data: " + e.getMessage());
                callback.onFailure(e);
            }
        });
    }

    public static void getCoursesByDomainId(String domainId, UploadCallback<List<CourseFB>> callback) {
        ArrayList<CourseFB> cours = new ArrayList<>();
        FirebaseController.getMatchedCollection(TABLE_NAME, "domainId", domainId, new UploadCallback<List<HashMap<String, Object>>>() {
            @Override
            public void onSuccess(List<HashMap<String, Object>> results) {
                for (HashMap<String, Object> data : results) {
                    cours.add(mapHashMapToCourse(data));
                }
                callback.onSuccess(cours);
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("getData", "Failed to fetch data: " + e.getMessage());
                callback.onFailure(e);
            }
        });
    }

    public static void getCoursesByFolderId(String folderId, UploadCallback<List<CourseFB>> callback) {
        ArrayList<CourseFB> cours = new ArrayList<>();
        FirebaseController.getMatchedCollection(TABLE_NAME, "folderId", folderId, new UploadCallback<List<HashMap<String, Object>>>() {
            @Override
            public void onSuccess(List<HashMap<String, Object>> results) {
                for (HashMap<String, Object> data : results) {
                    cours.add(mapHashMapToCourse(data));
                }
                callback.onSuccess(cours);
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("getData", "Failed to fetch data: " + e.getMessage());
                callback.onFailure(e);
            }
        });
    }

    public static void getCourses(UploadCallback<List<CourseFB>> callback) {
        ArrayList<CourseFB> courseFBList = new ArrayList<>();
        FirebaseController.getAllCollection(TABLE_NAME, null, new UploadCallback<List<HashMap<String, Object>>>(){
            @Override
            public void onSuccess(List<HashMap<String, Object>> result) {
                for (HashMap<String, Object> data : result) {
                    courseFBList.add(mapHashMapToCourse(data));
                }
                Log.d(TAG, "onSuccess: size = " + courseFBList.size());
                callback.onSuccess(courseFBList);
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("getData", "Failed to fetch data: " + e.getMessage());
                callback.onFailure(e);
            }
        });
    }

    public static void initializeCourseList() {
        Log.d(TAG, "initializeCourseList: here1");
        ArrayList<HashMap<String, Object>> courseHashMapList = new ArrayList<>();
        courseHashMapList.add(createCourseData("Python","Daniel Liang", "This is Java", "https://res.cloudinary.com/dmgpozfee/image/upload/v1732898099/vfp2hoinnc2udodmftyv.jpg","P00001", "P00002","P00003", "D00001", "F00001", generateDate()));
        courseHashMapList.add(createCourseData("Java", "Daniel Chong", "This is Python", "https://res.cloudinary.com/dmgpozfee/image/upload/v1732898099/vfp2hoinnc2udodmftyv.jpg","P00004", "P00002","P00003", "D00001", "F00002", generateDate()));
        courseHashMapList.add(createCourseData("C++", "Michille Liang", "This is C++", "https://res.cloudinary.com/dmgpozfee/image/upload/v1732898099/vfp2hoinnc2udodmftyv.jpg","P00001", "P00002","P00003", "D00002", "F00003", generateDate()));
        courseHashMapList.add(createCourseData("JavaScript", "Jane Smith", "This is JavaScript", "https://res.cloudinary.com/dmgpozfee/image/upload/v1732898099/vfp2hoinnc2udodmftyv.jpg","P00001", "P00002","P00003", "D00003", "F00004", generateDate()));
        courseHashMapList.add(createCourseData("PHP", "Benz Harris", "This is PHP", "https://res.cloudinary.com/dmgpozfee/image/upload/v1732898099/vfp2hoinnc2udodmftyv.jpg","P00001", "P00002","P00003", "D00003", "F00005", generateDate()));
        Log.d(TAG, "initializeCourseList: here2");
        for (HashMap<String, Object> dataHashMap:courseHashMapList) {
            insertCourse(dataHashMap, new UploadCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "onSuccess: " + "course");
                }
                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "onFailure: ", e);
                }
            });
        }
    }


    private static CourseFB mapHashMapToCourse(HashMap<String, Object> data) {
        CourseFB courseFB = new CourseFB();
        courseFB.title = ((String) data.get("title"));
        courseFB.author = ((String) data.get("author"));
        courseFB.description = ((String) data.get("description"));
        courseFB.coverImage = ((String) data.get("coverImage"));
        courseFB.post1 = ((String) data.get("post1"));
        courseFB.post2 = ((String) data.get("post2"));
        courseFB.post3 = ((String) data.get("post3"));
        courseFB.domainId = ((String) data.get("domainId"));
        courseFB.folderId = ((String) data.get("folderId"));
        courseFB.date = ((String) data.get("date"));
        courseFB.courseId = ((String) data.get("courseId"));
//        Log.d("mapHashMapToCourse", "title: " + course.title);
//        Log.d("mapHashMapToCourse", "author: " + course.author);
//        Log.d("mapHashMapToCourse", "description: " + course.description);
//        Log.d("mapHashMapToCourse", "coverImage: " + course.coverImage);
//        Log.d("mapHashMapToCourse", "post1: " + course.post1);
//        Log.d("mapHashMapToCourse", "post2: " + course.post2);
//        Log.d("mapHashMapToCourse", "post3: " + course.post3);
//        Log.d("mapHashMapToCourse", "folderId: " + course.folderId);
//        Log.d("mapHashMapToCourse", "date: " + course.date);
//        Log.d("mapHashMapToCourse", "courseId: " + course.courseId);
        return courseFB;
    }
    private static HashMap<String, Object> mapCourseToHashMap(CourseFB courseFB) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("title", courseFB.getTitle());
        data.put("author", courseFB.getCourseId());
        data.put("description", courseFB.getCourseId());
        data.put("coverImage", courseFB.getCourseId());
        data.put("post1", courseFB.getCourseId());
        data.put("post2", courseFB.getCourseId());
        data.put("post3", courseFB.getCourseId());
        data.put("domainId", courseFB.getDomainId());
        data.put("folderId", courseFB.getCourseId());
        data.put("date", courseFB.getCourseId());
        data.put("courseId", courseFB.getCourseId());
        return data;
    }

    public static HashMap<String, Object> createCourseData(String title, String author,
                                                            String description, String coverImage, String post1,
                                                            String post2, String post3, String domainId, String folderId, String date) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("author", author);
        data.put("description", description);
        data.put("coverImage", coverImage);
        data.put("post1", post1);
        data.put("post2", post2);
        data.put("post3", post3);
        data.put("domainId", domainId);
        data.put("folderId", folderId);
        data.put("date", date);
        return data;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDomainId() {
        return domainId;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public String getPost1() {
        return post1;
    }

    public String getPost2() {
        return post2;
    }

    public String getPost3() {
        return post3;
    }

    public String getAuthor() {
        return author;
    }

    public String getFolderId() {
        return folderId;
    }

    public String getDate() {
        return date;
    }

    private static String generateDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(new Date());
        Log.d(TAG, "generateDate: " + date);
        return date;
    }
}
