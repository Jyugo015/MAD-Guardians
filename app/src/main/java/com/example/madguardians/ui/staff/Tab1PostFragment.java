//package com.example.madguardians.ui.staff;
//
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.os.Bundle;
//import android.widget.Toast;
//
////import com.example.madguardians.database.FirestoreManager;
//import com.example.madguardians.notification.NotificationUtils;
//import com.google.firebase.Timestamp;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.firestore.SetOptions;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class Tab1PostFragment extends BaseTab1Fragment<Post> implements RecycleViewPostAdapter.OnPostActionListener {
//    //    private FirestoreManager firestoreManager;
//    private NotificationUtils notificationUtils;
//
//    public Tab1PostFragment() {
//
//    }
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        notificationUtils = new NotificationUtils();
//    }
//
//    @Override
//    protected List<Post> getData() {
//        List<Post> postList = new ArrayList<>();
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("post")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        QuerySnapshot querySnapshot = task.getResult();
//                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
//                            for (QueryDocumentSnapshot document : querySnapshot) {
//                                Post post = document.toObject(Post.class);
//                                post.setPostId(document.getId()); // Set Firestore document ID as postId
//                                postList.add(post);
//                            }
//                        } else {
//                            showToast("No posts available.");
//                        }
//                    } else {
//                        showToast("Failed to retrieve data.");
//                    }
//                })
//                .addOnFailureListener(e -> logError("Failed to fetch posts from Firestore", e));
//        return postList;
//    }
//
//    @Override
//    protected RecyclerView.Adapter<?> getAdapter(List<Post> data) {
//        return new RecycleViewPostAdapter(data, requireContext(), this);
//    }
//
//    @Override
//    public void onRejectClicked(Post post, int position) {
//        handlePostAction(post, "rejected", "Rejected");
//    }
//
//    @Override
//    public void onApprovedClicked(Post post, int position) {
//        handlePostAction(post, "approved", "Approved");
//    }
//
//    @Override
//    public void onDeleteClicked(Post post, int position) {
////        try {
////            VerPost verPost = verPostDao.getByPostId(post.getPostId());
////            if (verPost != null) {
////                firestoreManager.onDelete("verPost", verPost);
////            }
////            firestoreManager.onDelete("post", post);
////
////            notificationUtils.createTestNotification(
////                    post.getUserId(),
////                    "Your post \"" + post.getTitle() + "\" has been deleted."
////            );
////            showToast("Deleted: " + post.getTitle());
////        } catch (Exception e) {
////            logError("Failed to delete post", e);
////            showToast("Failed to delete: " + e.getMessage());
////        }
//    }
//
//    @Override
//    public void onCourseTitleClicked(Post post, int position) {
//        // Handle course title click
//        showToast("Course Title clicked: " + post.getTitle());
//        // Add any additional actions here
//    }
//
//    private void handlePostAction(Post post, String actionStatus, String actionName) {
//        try {
//            // Get the current timestamp
//            Timestamp currentTimestamp = Timestamp.now();
//
//            // Reference to the "verPost" collection in Firestore
//            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//            CollectionReference verPostCollection = firestore.collection("verPost");
//
//            // Query Firestore to check if a verPost exists for the given post ID
//            verPostCollection
//                    .whereEqualTo("postId", post.getPostId())
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
//                            // Retrieve the existing verPost document
//                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
//                            String verPostId = document.getId();
//
//                            // Create updated data for the verPost
//                            Map<String, Object> updatedVerPost = new HashMap<>();
//                            updatedVerPost.put("postId", post.getPostId());
//                            updatedVerPost.put("staffId", "staffId"); // Replace with actual staff ID
//                            updatedVerPost.put("actionStatus", actionStatus);
//                            updatedVerPost.put("timestamp", currentTimestamp);
//
//                            // Update the verPost document in Firestore
//                            verPostCollection.document(verPostId)
//                                    .set(updatedVerPost, SetOptions.merge())
//                                    .addOnSuccessListener(aVoid -> {
//                                        notificationUtils.createTestNotification(
//                                                post.getUserId(),
//                                                "Your post \"" + post.getTitle() + "\" has been " + actionStatus + "."
//                                        );
//                                        showToast(actionName + ": " + post.getTitle());
//                                    })
//                                    .addOnFailureListener(e -> {
//                                        logError("Failed to update verPost", e);
//                                        showToast("Failed to " + actionName.toLowerCase() + ": " + e.getMessage());
//                                    });
//
//                        } else {
//                            showToast("No verPost found for the post: " + post.getPostId());
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        logError("Failed to retrieve verPost", e);
//                        showToast("Failed to " + actionName.toLowerCase() + ": " + e.getMessage());
//                    });
//
//        } catch (Exception e) {
//            logError("Failed to " + actionName.toLowerCase() + " post", e);
//            showToast("Failed to " + actionName.toLowerCase() + ": " + e.getMessage());
//        }
//    }
//
//    private void showToast(String message) {
//        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
//    }
//
//    private void logError(String message, Exception e) {
//        // Replace with proper logging framework
//        e.printStackTrace();
//    }
//}
//
//package com.example.madguardians.ui.staff;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
////import com.example.madguardians.database.FirestoreManager;
//import com.example.madguardians.notification.NotificationUtils;
//import com.google.firebase.Timestamp;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.firestore.SetOptions;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class Tab1PostFragment extends BaseTab1Fragment<Post> implements RecycleViewPostAdapter.OnPostActionListener {
//    //    private FirestoreManager firestoreManager;
//    private RecycleViewPostAdapter adapter;
//    private NotificationUtils notificationUtils;
//
//    public Tab1PostFragment() {
//
//    }
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        notificationUtils = new NotificationUtils();
//        fetchData();
//    }
//    @Override
//    protected void fetchData() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("post")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        List<Post> postList = new ArrayList<>();
//                        QuerySnapshot querySnapshot = task.getResult();
//                        for (QueryDocumentSnapshot document : querySnapshot) {
//                            Post post = document.toObject(Post.class);
//                            post.setPostId(document.getId()); // Set Firestore document ID as postId
//                            postList.add(post);
//                        }
//                        updateRecyclerViewAdapter(postList); // Update RecyclerView
//                    } else {
//                        showToast("Failed to retrieve posts.");
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    e.printStackTrace();
//                    showToast("Error fetching posts: " + e.getMessage());
//                });
//    }
//    @Override
//    protected void updateRecyclerViewAdapter(List<Post> data) {
//        if (adapter == null) {
//            adapter = new RecycleViewPostAdapter(data, requireContext(), this);
//            recyclerView.setAdapter(adapter);
//        } else {
//            adapter.updateData(data); // Add this method in your adapter
//        }
//    }
//
////
////    @Override
////    protected RecyclerView.Adapter<?> getAdapter(List<Post> data) {
////        return new RecycleViewPostAdapter(data, requireContext(), this);
////    }
//
//    @Override
//    public void onRejectClicked(Post post, int position) {
//        handlePostAction(post, "rejected", "Rejected");
//    }
//
//    @Override
//    public void onApprovedClicked(Post post, int position) {
//        handlePostAction(post, "approved", "Approved");
//    }
//
//    @Override
//    public void onDeleteClicked(Post post, int position) {
////        try {
////            VerPost verPost = verPostDao.getByPostId(post.getPostId());
////            if (verPost != null) {
////                firestoreManager.onDelete("verPost", verPost);
////            }
////            firestoreManager.onDelete("post", post);
////
////            notificationUtils.createTestNotification(
////                    post.getUserId(),
////                    "Your post \"" + post.getTitle() + "\" has been deleted."
////            );
////            showToast("Deleted: " + post.getTitle());
////        } catch (Exception e) {
////            logError("Failed to delete post", e);
////            showToast("Failed to delete: " + e.getMessage());
////        }
//    }
//
//    @Override
//    public void onCourseTitleClicked(Post post, int position) {
//        // Handle course title click
//        showToast("Course Title clicked: " + post.getTitle());
//        // Add any additional actions here
//    }
//
//    private void handlePostAction(Post post, String actionStatus, String actionName) {
//        try {
//            // Get the current timestamp
//            Timestamp currentTimestamp = Timestamp.now();
//
//            // Reference to the "verPost" collection in Firestore
//            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//            CollectionReference verPostCollection = firestore.collection("verPost");
//
//            // Query Firestore to check if a verPost exists for the given post ID
//            verPostCollection
//                    .whereEqualTo("postId", post.getPostId())
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
//                            // Retrieve the existing verPost document
//                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
//                            String verPostId = document.getId();
//
//                            // Create updated data for the verPost
//                            Map<String, Object> updatedVerPost = new HashMap<>();
//                            updatedVerPost.put("postId", post.getPostId());
//                            updatedVerPost.put("staffId", "staffId"); // Replace with actual staff ID
//                            updatedVerPost.put("actionStatus", actionStatus);
//                            updatedVerPost.put("timestamp", currentTimestamp);
//
//                            // Update the verPost document in Firestore
//                            verPostCollection.document(verPostId)
//                                    .set(updatedVerPost, SetOptions.merge())
//                                    .addOnSuccessListener(aVoid -> {
//                                        notificationUtils.createTestNotification(
//                                                post.getUserId(),
//                                                "Your post \"" + post.getTitle() + "\" has been " + actionStatus + "."
//                                        );
//                                        showToast(actionName + ": " + post.getTitle());
//                                    })
//                                    .addOnFailureListener(e -> {
//                                        logError("Failed to update verPost", e);
//                                        showToast("Failed to " + actionName.toLowerCase() + ": " + e.getMessage());
//                                    });
//
//                        } else {
//                            showToast("No verPost found for the post: " + post.getPostId());
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        logError("Failed to retrieve verPost", e);
//                        showToast("Failed to " + actionName.toLowerCase() + ": " + e.getMessage());
//                    });
//
//        } catch (Exception e) {
//            logError("Failed to " + actionName.toLowerCase() + " post", e);
//            showToast("Failed to " + actionName.toLowerCase() + ": " + e.getMessage());
//        }
//    }
//
//    private void showToast(String message) {
//        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
//    }
//
//    private void logError(String message, Exception e) {
//        // Replace with proper logging framework
//        e.printStackTrace();
//    }
//}

//verPost
package com.example.madguardians.ui.staff;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

//import com.example.madguardians.database.FirestoreManager;
import com.example.madguardians.notification.NotificationUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//
//public class Tab1PostFragment extends BaseTab1Fragment<VerPost> implements RecycleViewVerPostAdapter.OnPostActionListener {
////    private FirestoreManager firestoreManager;
////    private RecycleViewPostAdapter adapter;
//    private RecycleViewVerPostAdapter adapter;
//    private NotificationUtils notificationUtils;
//
//    public Tab1PostFragment() {
//
//    }
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        notificationUtils = new NotificationUtils();
//        fetchData();
//    }
//    @Override
//    protected void fetchData() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("verPost") // Update collection name to "verPost" if applicable
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        List<VerPost> verPostList = new ArrayList<>();
//                        QuerySnapshot querySnapshot = task.getResult();
//                        for (QueryDocumentSnapshot document : querySnapshot) {
//                            VerPost verPost = document.toObject(VerPost.class);
//                            verPost.setVerPostId(document.getId()); // Set Firestore document ID as postId
//                            verPostList.add(verPost);
//                        }
//                        updateRecyclerViewAdapter(verPostList); // Update RecyclerView with VerPost list
//                    } else {
//                        showToast("Failed to retrieve VerPosts.");
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    e.printStackTrace();
//                    showToast("Error fetching VerPosts: " + e.getMessage());
//                });
//    }
//
//    @Override
//    protected void updateRecyclerViewAdapter(List<VerPost> data) {
//        if (adapter == null) {
//            adapter = new RecycleViewVerPostAdapter(data, requireContext(), this);
//            recyclerView.setAdapter(adapter);
//        } else {
//            adapter.updateData(data); // Add this method in your adapter
//        }
//    }
//
////
////    @Override
////    protected RecyclerView.Adapter<?> getAdapter(List<Post> data) {
////        return new RecycleViewPostAdapter(data, requireContext(), this);
////    }
//
//    @Override
//    public void onRejectClicked(VerPost post, int position) {
//        handlePostAction(post, "rejected", "Rejected");
//    }
//
//    @Override
//    public void onApprovedClicked(VerPost verPost, int position) {
//        handlePostAction(verPost, "approved", "Approved");
//    }
//
//    @Override
//    public void onDeleteClicked(VerPost verPost, int position) {
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//        try {
//            // Remove the VerPost document
//            firestore.collection("verPost")
//                    .document(verPost.getVerPostId())
//                    .delete()
//                    .addOnSuccessListener(aVoid -> {
//                        Log.d("onDeleteClicked", "Successfully deleted VerPost: " + verPost.getVerPostId());
//
//                        // Retrieve the Post object using postId
//                        firestore.collection("post")
//                                .document(verPost.getPostId())
//                                .get()
//                                .addOnSuccessListener(documentSnapshot -> {
//                                    if (documentSnapshot.exists()) {
//                                        // Map the document to a Post object
//                                        Post post = documentSnapshot.toObject(Post.class);
//
//                                        if (post != null) {
//                                            // Get title and userId from the Post object
//                                            String postTitle = post.getTitle();
//                                            String postUserId = post.getUserId();
//
//                                            // Delete the Post document
//                                            firestore.collection("post")
//                                                    .document(verPost.getPostId())
//                                                    .delete()
//                                                    .addOnSuccessListener(aVoid1 -> {
//                                                        Log.d("onDeleteClicked", "Successfully deleted Post: " + verPost.getPostId());
//
//                                                        // Notify the user of success
//                                                        showToast("Deleted post: " + postTitle);
//
//                                                        // Optional: Notify the post owner with a notification
//                                                        notificationUtils.createTestNotification(
//                                                                postUserId,
//                                                                "Your post \"" + postTitle + "\" has been deleted."
//                                                        );
//
//                                                        // Update RecyclerView
//                                                        verPostList.remove(position);
//                                                        notifyItemRemoved(position);
//                                                    })
//                                                    .addOnFailureListener(e -> {
//                                                        Log.e("onDeleteClicked", "Failed to delete Post: " + verPost.getPostId(), e);
//                                                        showToast("Failed to delete Post: " + postTitle);
//                                                    });
//                                        } else {
//                                            Log.e("onDeleteClicked", "Post object is null");
//                                            showToast("Failed to retrieve Post object");
//                                        }
//                                    } else {
//                                        Log.e("onDeleteClicked", "Post document does not exist");
//                                        showToast("Post document does not exist");
//                                    }
//                                })
//                                .addOnFailureListener(e -> {
//                                    Log.e("onDeleteClicked", "Failed to retrieve Post: " + verPost.getPostId(), e);
//                                    showToast("Failed to retrieve Post");
//                                });
//                    })
//                    .addOnFailureListener(e -> {
//                        Log.e("onDeleteClicked", "Failed to delete VerPost: " + verPost.getVerPostId(), e);
//                        showToast("Failed to delete VerPost: " + verPost.getVerPostId());
//                    });
//        } catch (Exception e) {
//            Log.e("onDeleteClicked", "Unexpected error while deleting post", e);
//            showToast("Unexpected error: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public void onCourseTitleClicked(VerPost post, int position) {
//        // Handle course title click
//        showToast("Course Title clicked: ");
//        // Add any additional actions here
//    }
//
//    private void handlePostAction(VerPost post, String verifiedStatus, String actionName) {
//        try {
//            // Get the current timestamp
//            Timestamp currentTimestamp = Timestamp.now();
//
//            // Reference to the "verPost" collection in Firestore
//            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//            CollectionReference verPostCollection = firestore.collection("verPost");
//
//            // Query Firestore to check if a verPost exists for the given post ID
//            verPostCollection
//                    .whereEqualTo("postId", post.getPostId())
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
//                            // Retrieve the existing verPost document
//                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
//                            String verPostId = document.getId();
//
//                            // Create updated data for the verPost
//                            Map<String, Object> updatedVerPost = new HashMap<>();
//                            updatedVerPost.put("postId", post.getPostId());
//                            updatedVerPost.put("staffId", "staffId"); // Replace with actual staff ID
//                            updatedVerPost.put("verifiedStatus", verifiedStatus);
//                            updatedVerPost.put("timestamp", currentTimestamp);
//
//                            // Update the verPost document in Firestore
//                            // Update the verPost document in Firestore
//                            verPostCollection.document(verPostId)
//                                    .set(updatedVerPost, SetOptions.merge())
//                                    .addOnSuccessListener(aVoid -> {
//                                        // Fetch the Post object using postId
//                                        firestore.collection("post")
//                                                .document(post.getPostId()) // postId from VerPost
//                                                .get()
//                                                .addOnSuccessListener(postDocument -> {
//                                                    if (postDocument.exists()) {
//                                                        // Convert the document to Post object
//                                                        Post relatedPost = postDocument.toObject(Post.class);
//                                                        if (relatedPost != null) {
//                                                            // Use Post's userId and title for notification
//                                                            notificationUtils.createTestNotification(
//                                                                    relatedPost.getUserId(),
//                                                                    "Your post \"" + relatedPost.getTitle() + "\" has been " + verifiedStatus + "."
//                                                            );
//                                                            showToast(actionName + ": " + relatedPost.getTitle());
//                                                        } else {
//                                                            showToast("Failed to retrieve post details for notification.");
//                                                        }
//                                                    } else {
//                                                        showToast("Post not found for postId: " + post.getPostId());
//                                                    }
//                                                })
//                                                .addOnFailureListener(e -> {
//                                                    logError("Failed to retrieve post details", e);
//                                                    showToast("Failed to retrieve post details: " + e.getMessage());
//                                                });
//                                    })
//                                    .addOnFailureListener(e -> {
//                                        logError("Failed to update verPost", e);
//                                        showToast("Failed to " + actionName.toLowerCase() + ": " + e.getMessage());
//                                    });
//                        } else {
//                            showToast("No verPost found for the post: " + post.getPostId());
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        logError("Failed to retrieve verPost", e);
//                        showToast("Failed to " + actionName.toLowerCase() + ": " + e.getMessage());
//                    });
//
//        } catch (Exception e) {
//            logError("Failed to " + actionName.toLowerCase() + " post", e);
//            showToast("Failed to " + actionName.toLowerCase() + ": " + e.getMessage());
//        }
//    }
//    private void showToast(String message) {
//        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
//    }
//
//    private void logError(String message, Exception e) {
//        // Replace with proper logging framework
//        e.printStackTrace();
//    }
//}

public class Tab1PostFragment extends BaseTab1Fragment<VerPost> implements RecycleViewVerPostAdapter.OnPostActionListener {
    private RecycleViewVerPostAdapter adapter;
    private NotificationUtils notificationUtils;
    private List<VerPost> verPostList = new ArrayList<>();
    String staffId;
    public Tab1PostFragment() {
        // Required empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationUtils = new NotificationUtils();
//        System.out.println("Hi");
        if (getArguments() != null) {
            staffId = getArguments().getString("staffId"); // Retrieve staffId
            System.out.println(staffId);
            System.out.println("Hi "+staffId);
            fetchData(); // Use staffId as needed
        }
    }

    @Override
    protected void fetchData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("verPost")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Fetch data sorted by timestamp
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<VerPost> tempList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            VerPost verPost = document.toObject(VerPost.class);
                            verPost.setVerPostId(document.getId());
                            tempList.add(verPost);
                        }
                        updateRecyclerViewAdapter(tempList);
                    } else {
                        showToast("Failed to retrieve VerPosts.");
                    }
                })
                .addOnFailureListener(e -> {
                    logError("Error fetching VerPosts", e);
                    showToast("Error fetching VerPosts: " + e.getMessage());
                });
    }

    @Override
    protected void updateRecyclerViewAdapter(List<VerPost> data) {
        if (adapter == null) {
            adapter = new RecycleViewVerPostAdapter(data, requireContext(), this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(data); // Make sure updateData is implemented in your adapter
        }
        verPostList = data;
    }

    @Override
    public void onRejectClicked(VerPost post, int position) {
        handlePostAction(post, "rejected", "Rejected");
    }

    @Override
    public void onApprovedClicked(VerPost verPost, int position) {
        handlePostAction(verPost, "approved", "Approved");
    }

    @Override
    public void onDeleteClicked(VerPost verPost, int position) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("verPost")
                .document(verPost.getVerPostId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    logMessage("Successfully deleted VerPost: " + verPost.getVerPostId());

                    firestore.collection("post")
                            .document(verPost.getPostId())
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Post post = documentSnapshot.toObject(Post.class);

                                    if (post != null) {
                                        String postTitle = post.getTitle();
                                        String postUserId = post.getUserId();

                                        firestore.collection("post")
                                                .document(verPost.getPostId())
                                                .delete()
                                                .addOnSuccessListener(aVoid1 -> {
                                                    logMessage("Successfully deleted Post: " + verPost.getPostId());
                                                    showToast("Deleted post: " + postTitle);

                                                    notificationUtils.createTestNotification(
                                                            postUserId,
                                                            "Your post \"" + postTitle + "\" has been deleted."
                                                    );

                                                    verPostList.remove(position);
                                                    adapter.notifyItemRemoved(position);
                                                })
                                                .addOnFailureListener(e -> {
                                                    logError("Failed to delete Post: " + verPost.getPostId(), e);
                                                    showToast("Failed to delete Post: " + postTitle);
                                                });
                                    } else {
                                        logError("Post object is null", null);
                                        showToast("Failed to retrieve Post object.");
                                    }
                                } else {
                                    logError("Post document does not exist", null);
                                    showToast("Post document does not exist.");
                                }
                            })
                            .addOnFailureListener(e -> {
                                logError("Failed to retrieve Post", e);
                                showToast("Failed to retrieve Post.");
                            });
                })
                .addOnFailureListener(e -> {
                    logError("Failed to delete VerPost: " + verPost.getVerPostId(), e);
                    showToast("Failed to delete VerPost.");
                });
    }

    @Override
    public void onCourseTitleClicked(VerPost post, int position) {
        showToast("Course Title clicked.");
    }

    private void handlePostAction(VerPost post, String verifiedStatus, String actionName) {
        Timestamp currentTimestamp = Timestamp.now();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("verPost")
                .whereEqualTo("postId", post.getPostId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String verPostId = document.getId();

                        Map<String, Object> updatedVerPost = new HashMap<>();
                        updatedVerPost.put("postId", post.getPostId());
                        updatedVerPost.put("staffId", staffId);
                        updatedVerPost.put("verifiedStatus", verifiedStatus);
                        updatedVerPost.put("timestamp", currentTimestamp);

                        firestore.collection("verPost")
                                .document(verPostId)
                                .set(updatedVerPost, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    post.setVerifiedStatus(verifiedStatus); // Update local data
                                    int index = verPostList.indexOf(post);
                                    if (index != -1) {
                                        verPostList.set(index, post);
                                        adapter.notifyItemChanged(index); // Notify adapter to rebind this item
                                    }

                                    firestore.collection("post")
                                            .document(post.getPostId())
                                            .get()
                                            .addOnSuccessListener(postDocument -> {
                                                if (postDocument.exists()) {
                                                    Post relatedPost = postDocument.toObject(Post.class);
                                                    if (relatedPost != null) {
                                                        notificationUtils.createTestNotification(
                                                                relatedPost.getUserId(),
                                                                "Your post \"" + relatedPost.getTitle() + "\" has been " + verifiedStatus + "."
                                                        );
                                                        showToast(actionName + ": " + relatedPost.getTitle());
                                                    } else {
                                                        showToast("Failed to retrieve post details for notification.");
                                                    }
                                                } else {
                                                    showToast("Post not found for postId: " + post.getPostId());
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                logError("Failed to retrieve post details", e);
                                                showToast("Failed to retrieve post details: " + e.getMessage());
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    logError("Failed to update verPost", e);
                                    showToast("Failed to " + actionName.toLowerCase() + ": " + e.getMessage());
                                });
                    } else {
                        showToast("No verPost found for the post: " + post.getPostId());
                    }
                })
                .addOnFailureListener(e -> {
                    logError("Failed to retrieve verPost", e);
                    showToast("Failed to " + actionName.toLowerCase() + ": " + e.getMessage());
                });
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void logError(String message, Exception e) {
        if (e != null) {
            Log.e("Tab1PostFragment", message, e);
        } else {
            Log.e("Tab1PostFragment", message);
        }
    }

    private void logMessage(String message) {
        Log.d("Tab1PostFragment", message);
    }
}
