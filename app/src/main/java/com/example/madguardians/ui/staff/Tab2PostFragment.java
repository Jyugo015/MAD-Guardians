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

public class Tab2PostFragment extends BaseTab2Fragment<VerPost> implements RecycleViewVerPostAdapter.OnPostActionListener {
    private RecycleViewVerPostAdapter adapter;
    private NotificationUtils notificationUtils;
    private List<VerPost> verPostList = new ArrayList<>();

    public Tab2PostFragment() {
        // Required empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationUtils = new NotificationUtils();
        fetchData();
    }

    @Override
    protected void fetchData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("verPost")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        logError("Error fetching VerPosts", e);
                        showToast("Error fetching VerPosts: " + e.getMessage());
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        List<VerPost> tempList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            VerPost verPost = document.toObject(VerPost.class);
                            verPost.setVerPostId(document.getId());

                            // Client-side filtering: check if status is "pending"
                            if ("pending".equals(verPost.getVerifiedStatus())) {
                                tempList.add(verPost);
                            }
                        }

                        if (tempList.isEmpty()) {
                            showToast("No pending VerPosts found.");
                        } else {
                            updateRecyclerViewAdapter(tempList);
                        }
                    } else {
                        showToast("No VerPosts found.");
                    }
                });
    }

//    @Override
//    protected void fetchData() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("verPost")
//                .orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        List<VerPost> tempList = new ArrayList<>();
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            VerPost verPost = document.toObject(VerPost.class);
//                            verPost.setVerPostId(document.getId());
//
//                            // Client-side filtering: check if status is "pending"
//                            if ("pending".equals(verPost.getVerifiedStatus())) {
//                                tempList.add(verPost);
//                            }
//                        }
//
//                        // Update adapter with filtered list
//                        if (tempList.isEmpty()) {
//                            showToast("No pending VerPosts found.");
//                        } else {
//                            updateRecyclerViewAdapter(tempList);
//                        }
//                    } else {
//                        showToast("Failed to retrieve VerPosts.");
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    logError("Error fetching VerPosts", e);
//                    showToast("Error fetching VerPosts: " + e.getMessage());
//                });
//    }

//    @Override
//    protected void fetchData() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("verPost")
//                .whereEqualTo("verifiedStatus", "pending") // Filter by verifiedStatus == "pending"
//                .orderBy("timestamp", Query.Direction.DESCENDING) // Sort by timestamp
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        List<VerPost> tempList = new ArrayList<>();
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            System.out.println("Pending verPost"+document.getData());
//                            VerPost verPost = document.toObject(VerPost.class);
//                            verPost.setVerPostId(document.getId());
//                            tempList.add(verPost);
//                        }
//                        updateRecyclerViewAdapter(tempList);
//                    } else {
//                        showToast("No pending VerPosts.");
//                        System.out.println("No pending VerPosts.");
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    logError("Failed to fetch pending VerPosts.", e);
//                    showToast("Failed to fetch pending VerPosts.");
//                    System.out.println("Failed to fetch pending VerPosts.");
//                });
//    }

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
                        updatedVerPost.put("staffId", "staffId"); // Replace with actual staff ID
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
            Log.e("Tab2PostFragment", message, e);
        } else {
            Log.e("Tab2PostFragment", message);
        }
    }

    private void logMessage(String message) {
        Log.d("Tab2PostFragment", message);
    }
}