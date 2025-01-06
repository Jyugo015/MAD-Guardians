package com.example.madguardians.comment.adapter;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.madguardians.database.Comments;
import com.example.madguardians.database.Executor;
import com.example.madguardians.ui.staff.Helpdesk;
import com.example.madguardians.database.Issue;
import com.example.madguardians.database.User;
import com.example.madguardians.firebase.CourseFB;
import com.example.madguardians.firebase.PostFB;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FirestoreComment {

    private final FirebaseFirestore firestore;

    public FirestoreComment() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void getLastDocumentId(String tableName, Consumer<String> callback) {
        Executor.executeTask(() -> {
            firestore.collection(tableName)
//                    .orderBy("timestamp", Query.Direction.DESCENDING) // Order by descending to get the last created document
                    .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING) // Order by document ID to break ties
                    .limit(1) // Only retrieve one document
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            String lastDocumentId = querySnapshot.getDocuments().get(0).getId();
                            Log.d("Firestore", "Last document ID: " + lastDocumentId);
                            callback.accept(lastDocumentId); // Pass the result to the callback
                        } else {
                            Log.d("Firestore", "No documents found in the collection");
                            if (tableName.equals("comment")) {
                                callback.accept("COM0000");
                            } else {
                                callback.accept("H0000");
                            }
                        }
                    })
                    .addOnFailureListener(exception -> {
                        Log.e("Firestore", "Error fetching documents: " + exception.getMessage());
                        callback.accept(null); // Pass null in case of an error
                    });
        });
    }

    public void insertComment(Comments comment){
        Executor.executeTask(() -> {
            firestore.collection("comment").document(comment.getCommentId())
                    .set(comment)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Comment successfully added!"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error adding comment", e));
        });
    }

    public void insertHelpdesk(Helpdesk helpdesk){
        Executor.executeTask(() -> {
            firestore.collection("helpdesk").document(helpdesk.getHelpdeskId())
                    .set(helpdesk)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Helpdesk successfully added!"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error adding helpdesk", e));
        });
    }

    public static String getDateTimestamp(Timestamp timestamp) {
//        // Trim the UTC+8 part from the end
//        return (new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm:ss a z")
//                .format(timestamp.toDate()))
//                .replaceAll(" UTC[+-]\\d+", "");

        String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm")
                .format(timestamp.toDate());

        // Remove the timezone part (this trims only the last part of the string which represents the timezone)
//        int timezoneIndex = formattedDate.lastIndexOf(" ");
//        if (timezoneIndex != -1) {
//            return formattedDate.substring(0, timezoneIndex); // Trim the timezone part
//        }

        return formattedDate;
    }

    public LiveData<List<Comments>> getComment(String userId, String readStatus) {
        MutableLiveData<List<Comments>> liveData = new MutableLiveData<>();

        // Ensure the task runs on a background thread
        Executor.executeTask(() -> {
            com.google.firebase.firestore.Query query = firestore.collection("comment")
                    .whereNotEqualTo("userId", userId)
                    .orderBy("timestamp", Query.Direction.DESCENDING);

            // Apply filter for read/unread status if necessary
//            if (readStatus != null && !readStatus.equalsIgnoreCase("All")) {
//                boolean isRead = readStatus.equalsIgnoreCase("Read");
//                query = query.whereEqualTo("read", isRead);
//            }

            // Add a listener to the query
            query.addSnapshotListener((querySnapshot, e) -> {
                if (e != null) {
                    Log.e("Sync Comment", "Listen failed", e);
                    return;
                }

                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // Convert querySnapshot to a list of Comments objects
                    List<Comments> dataList = querySnapshot.toObjects(Comments.class);

                    if (readStatus != null && !readStatus.equalsIgnoreCase("All")) {
                        boolean isRead = readStatus.equalsIgnoreCase("Read");
                        dataList  = dataList.stream()
                                .filter(comment -> comment.getAuthorId().equals(userId)&&comment.isAuthorRead()==isRead||
                                                    comment.getReplyUserId()!=null&&comment.getReplyUserId().equals(userId)&&
                                                    comment.isRepliedUserRead()==isRead)
                                .collect(Collectors.toList());
                    }

                    liveData.postValue(dataList.stream()
                            .filter(comment -> comment.getAuthorId().equals(userId)||comment.getReplyUserId()!=null&&
                                                comment.getReplyUserId().equals(userId))
                            .collect(Collectors.toList()));

                    Log.w("Sync Comment", "Found " + dataList.size() + " comments.");
                } else {
                    Log.w("Sync Comment", "No comments found.");
                    liveData.postValue(new ArrayList<>());
                }
            });
        });

        return liveData;
    }

    public LiveData<List<Comments>> getRootComment(String postId) {
        MutableLiveData<List<Comments>> liveData = new MutableLiveData<>();

        // Ensure the task runs on a background thread
        Executor.executeTask(() -> {
            firestore.collection("comment")
                    .whereEqualTo("postId", postId)
                    .whereEqualTo("rootComment", null)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener((querySnapshot, e) -> {
                        if (e != null) {
                            Log.e("Sync Root Comment", "Listen failed", e);
                            return;
                        }
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Convert querySnapshot to a list of Comments objects
                            List<Comments> dataList = querySnapshot.toObjects(Comments.class);
                            Log.w("Sync Comment", "Root Comment = " + dataList.size());
                            liveData.postValue(dataList); // Update LiveData for UI
                        } else {
                            Log.w("Sync Comment", "No comments found."+postId);
                            liveData.postValue(new ArrayList<>()); // Ensure LiveData gets an empty list
                        }
                    });
        });

        return liveData;
    }

    public LiveData<List<Comments>> getChildComment(String rootCommentId) {
        MutableLiveData<List<Comments>> liveData = new MutableLiveData<>();

        // Ensure the task runs on a background thread
        Executor.executeTask(() -> {
            firestore.collection("comment")
                    .whereEqualTo("rootComment", rootCommentId)
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener((querySnapshot, e) -> {
                        if (e != null) {
                            Log.e("Sync Child Comment", "Listen failed", e);
                            return;
                        }

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Convert querySnapshot to a list of Comments objects
                            List<Comments> dataList = querySnapshot.toObjects(Comments.class);
                            liveData.postValue(dataList); // Update LiveData for UI
                            Log.w("Sync Child Comment", "Root Comment = " +rootCommentId+ " Child Comment Size = " + String.valueOf(dataList.size()));
                        } else {
                            Log.w("Sync Child Comment", "No child comments found.");
                            liveData.postValue(new ArrayList<>()); // Ensure LiveData gets an empty list
                        }
                    });
        });

        return liveData;
    }

    public void getPost(String postId, Consumer<PostFB> callback){
        Executor.executeTask(() -> {
            firestore.collection("post")
                    .document(postId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            PostFB post = task.getResult().toObject(PostFB.class);
                            callback.accept(post);
                        }else {
                            Log.e("Firestore", "Failed to fetch post: " + task.getException());
                            callback.accept(null);
                        }
                    });
        });
    }

    public void getCourse(String postId, Consumer<CourseFB> callback){
        Executor.executeTask(() -> {
            firestore.collection("course")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<CourseFB> courseList = task.getResult().toObjects(CourseFB.class);
                            callback.accept(courseList.stream()
                                    .filter(courseItem -> courseItem.getPost1().equals(postId)
                                            || courseItem.getPost2().equals(postId)
                                            || courseItem.getPost3().equals(postId))
                                    .findFirst().orElse(null));
                        } else {
                            Log.e("Firestore", "Failed to fetch course: " + task.getException());
                            callback.accept(null);
                        }
                    });
        });
    }

    public void getUser(String userId, Consumer<User> callback){
        Executor.executeTask(() -> {
            firestore.collection("user")
                    .document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            User user = task.getResult().toObject(User.class);
                            callback.accept(user);
                        } else {
                            Log.e("Firestore", "Failed to fetch user profile: " + task.getException());
                            callback.accept(null);
                        }
                    });
        });
    }

    public void isUserEducator(String userId, Consumer<Boolean> callback){
        Executor.executeTask(() -> {
            firestore.collection("verEducator")
                    .whereEqualTo("userId",userId)
                    .whereEqualTo("verifiedStatus", "approved")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            boolean isEducator = !task.getResult().isEmpty();
                            Log.d("Firestore", isEducator ? "User is educator" : "User is not educator");
                            callback.accept(isEducator); // Pass result to callback
                        } else {
                            Log.e("Firestore", "Failed to fetch user educator: " + task.getException());
                            callback.accept(false);
                        }
                    });
        });
    }

    public LiveData<List<Issue>> getIssue() {
        MutableLiveData<List<Issue>> liveData = new MutableLiveData<>();

        // Ensure the task runs on a background thread
        Executor.executeTask(() -> {
            firestore.collection("issue")
                    .addSnapshotListener((querySnapshot, e) -> {
                        if (e != null) {
                            Log.e("Sync Issue", "Listen failed", e);
                            return;
                        }
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Convert querySnapshot to a list of Comments objects
                            List<Issue> dataList = querySnapshot.toObjects(Issue.class);
                            liveData.postValue(dataList); // Update LiveData for UI
                            Log.w("Sync Issue", "Sync successfully.");
                        } else {
                            Log.w("Sync Issue", "No issues found.");
                            liveData.postValue(new ArrayList<>()); // Ensure LiveData gets an empty list
                        }
                    });
        });

        return liveData;
    }

//    public void updateReadStatus(PostFB post){
//        Executor.executeTask(()->{
//            firestore.collection("comment")
//                    .whereEqualTo("postId", post.getPostId()) // Optional query condition
//                    .get()
//                    .addOnSuccessListener(querySnapshot -> {
//                        // Create a WriteBatch instance
//                        WriteBatch batch = firestore.batch();
//
//                        // Iterate through the QuerySnapshot to add update operations to the batch
//                        for (DocumentSnapshot document : querySnapshot) {
//                            DocumentReference docRef = document.getReference();
//
//                            // Define the update data
//                            Map<String, Object> updates = new HashMap<>();
//                            updates.put("read", true);
//
//                            // Add the update operation to the batch
//                            batch.update(docRef, updates);
//                        }
//
//                        // Commit the batch write
//                        batch.commit()
//                                .addOnSuccessListener(aVoid -> {
//                                    // Handle successful batch update
//                                    Log.d("Firestore", "Batch update successful.");
//                                })
//                                .addOnFailureListener(e -> {
//                                    // Handle errors
//                                    Log.e("Firestore", "Batch update failed", e);
//                                });
//                    })
//                    .addOnFailureListener(e -> {
//                        // Handle query failure
//                        Log.e("Firestore", "Query failed", e);
//                    });
//        });
//    }
}