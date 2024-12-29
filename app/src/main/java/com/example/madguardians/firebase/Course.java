package com.example.madguardians.firebase;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.madguardians.utilities.FirebaseController;
import com.example.madguardians.utilities.UploadCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Course {
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
//    private static ArrayList<Course> courseList = new ArrayList<>();

    private static Queue<HashMap<String, Object>> insertCourseQueue = new LinkedList<>();

    private Course(){
        // Default constructor required for Firebase
    }
    private Course(String courseId, String title, @Nullable String author, @Nullable String description, @Nullable String coverImage, @Nullable String post1, @Nullable String post2, @Nullable String post3, @Nullable String domainId, @Nullable String folderId, @Nullable String date) {
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
            HashMap<String, Object> dataHashMap = insertCourseQueue.poll();
            FirebaseController.generateDocumentId(TABLE_NAME, new UploadCallback<String>() {
                @Override
                public void onSuccess(String id) {
                    FirebaseController.insertFirebase(TABLE_NAME, id, dataHashMap, new UploadCallback<HashMap<String, Object>>() {
                        @Override
                        public void onSuccess(HashMap<String, Object> result) {
                            Log.d("initializeCourseList", "onSuccess");
                            processQueue(callback);
                        }
                        @Override
                        public void onFailure(Exception e) {
                            Log.e("initializeCourseList", "onFailure");
                            processQueue(callback);
                        }
                    });
                }
                @Override
                public void onFailure(Exception e) {

                }
            });
        } else {
            // all done
            callback.onSuccess(null);
        }
    }

    public static void getCourse(String courseId, UploadCallback<Course> callback) {
        Course course = null;
        FirebaseController.getMatchedCollection(TABLE_NAME, FirebaseController.getIdName(TABLE_NAME), courseId, new UploadCallback<List<HashMap<String, Object>>>(){
            @Override
            public void onSuccess(List<HashMap<String, Object>> result) {
                if (!result.isEmpty()) {
                    // Assuming only one match for courseId
                    HashMap<String, Object> data = result.get(0);
                    Course course = mapHashMapToCourse(data);
                    callback.onSuccess(course); // Return the course object via callback
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

    public static void getCoursesByDomainId(String domainId, UploadCallback<List<Course>> callback) {
        ArrayList<Course> courses = new ArrayList<>();
        FirebaseController.getMatchedCollection(TABLE_NAME, "domainId", domainId, new UploadCallback<List<HashMap<String, Object>>>() {
            @Override
            public void onSuccess(List<HashMap<String, Object>> results) {
                for (HashMap<String, Object> data : results) {
                    courses.add(mapHashMapToCourse(data));
                }
                callback.onSuccess(courses);
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("getData", "Failed to fetch data: " + e.getMessage());
                callback.onFailure(e);
            }
        });
    }

    public static void getCoursesByFolderId(String folderId, UploadCallback<List<Course>> callback) {
        ArrayList<Course> courses = new ArrayList<>();
        FirebaseController.getMatchedCollection(TABLE_NAME, "folderId", folderId, new UploadCallback<List<HashMap<String, Object>>>() {
            @Override
            public void onSuccess(List<HashMap<String, Object>> results) {
                for (HashMap<String, Object> data : results) {
                    courses.add(mapHashMapToCourse(data));
                }
                callback.onSuccess(courses);
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("getData", "Failed to fetch data: " + e.getMessage());
                callback.onFailure(e);
            }
        });
    }

    public static void getCourses(UploadCallback<List<Course>> callback) {
        ArrayList<Course> courseList = new ArrayList<>();
        FirebaseController.getAllCollection(TABLE_NAME, null, new UploadCallback<List<HashMap<String, Object>>>(){
            @Override
            public void onSuccess(List<HashMap<String, Object>> result) {
                for (HashMap<String, Object> data : result) {
                    courseList.add(mapHashMapToCourse(data));
                }
                Log.d("TAG", "onSuccess: size = " + courseList.size());
                callback.onSuccess(courseList);
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("getData", "Failed to fetch data: " + e.getMessage());
                callback.onFailure(e);
            }
        });
    }

    public static void initializeCourseList() {
        Log.d("TAG", "initializeCourseList: here1");
        ArrayList<HashMap<String, Object>> courseHashMapList = new ArrayList<>();
        courseHashMapList.add(createCourseData("Python","Daniel Liang", "This is Java", "https://res.cloudinary.com/dmgpozfee/image/upload/v1732898099/vfp2hoinnc2udodmftyv.jpg","P00001", "P00002","P00003", "D00001", "F00001","11/12/2024"));
        courseHashMapList.add(createCourseData("Java", "Daniel Chong", "This is Python", "https://res.cloudinary.com/dmgpozfee/image/upload/v1732898099/vfp2hoinnc2udodmftyv.jpg","P00004", "P00002","P00003", "D00001", "F00002","12/12/2024"));
        courseHashMapList.add(createCourseData("C++", "Michille Liang", "This is C++", "https://res.cloudinary.com/dmgpozfee/image/upload/v1732898099/vfp2hoinnc2udodmftyv.jpg","P00001", "P00002","P00003", "D00002", "F00003","13/12/2024"));
        courseHashMapList.add(createCourseData("JavaScript", "Jane Smith", "This is JavaScript", "https://res.cloudinary.com/dmgpozfee/image/upload/v1732898099/vfp2hoinnc2udodmftyv.jpg","P00001", "P00002","P00003", "D00003", "F00004","14/12/2024"));
        courseHashMapList.add(createCourseData("PHP", "Benz Harris", "This is PHP", "https://res.cloudinary.com/dmgpozfee/image/upload/v1732898099/vfp2hoinnc2udodmftyv.jpg","P00001", "P00002","P00003", "D00003", "F00005","15/12/2024"));
        Log.d("TAG", "initializeCourseList: here2");
        for (HashMap<String, Object> dataHashMap:courseHashMapList) {
            insertCourse(dataHashMap, new UploadCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d("TAG", "onSuccess: " + "course");
                }
                @Override
                public void onFailure(Exception e) {
                    Log.e("TAG", "onFailure: ", e);
                }
            });
        }
    }


    private static Course mapHashMapToCourse(HashMap<String, Object> data) {
        Course course = new Course();
        course.title = ((String) data.get("title"));
        course.author = ((String) data.get("author"));
        course.description = ((String) data.get("description"));
        course.coverImage = ((String) data.get("coverImage"));
        course.post1 = ((String) data.get("post1"));
        course.post2 = ((String) data.get("post2"));
        course.post3 = ((String) data.get("post3"));
        course.domainId = ((String) data.get("domainId"));
        course.folderId = ((String) data.get("folderId"));
        course.date = ((String) data.get("date"));
        course.courseId = ((String) data.get("courseId"));
        Log.d("mapHashMapToCourse", "title: " + course.title);
        Log.d("mapHashMapToCourse", "author: " + course.author);
        Log.d("mapHashMapToCourse", "description: " + course.description);
        Log.d("mapHashMapToCourse", "coverImage: " + course.coverImage);
        Log.d("mapHashMapToCourse", "post1: " + course.post1);
        Log.d("mapHashMapToCourse", "post2: " + course.post2);
        Log.d("mapHashMapToCourse", "post3: " + course.post3);
        Log.d("mapHashMapToCourse", "folderId: " + course.folderId);
        Log.d("mapHashMapToCourse", "date: " + course.date);
        Log.d("mapHashMapToCourse", "courseId: " + course.courseId);
        return course;
    }
    private static HashMap<String, Object> mapCourseToHashMap(Course course) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("title", course.getTitle());
        data.put("author", course.getCourseId());
        data.put("description", course.getCourseId());
        data.put("coverImage", course.getCourseId());
        data.put("post1", course.getCourseId());
        data.put("post2", course.getCourseId());
        data.put("post3", course.getCourseId());
        data.put("domainId", course.getDomainId());
        data.put("folderId", course.getCourseId());
        data.put("date", course.getCourseId());
        data.put("courseId", course.getCourseId());
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
}
