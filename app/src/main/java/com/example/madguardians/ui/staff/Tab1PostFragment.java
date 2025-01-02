package com.example.madguardians.ui.staff;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.madguardians.notification.NotificationUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//
//public class Tab1PostFragment extends BaseTab1Fragment<VerPost> implements RecycleViewVerPostAdapter.OnPostActionListener {
//    private RecycleViewVerPostAdapter adapter;
//    private NotificationUtils notificationUtils;
//    private List<VerPost> verPostList = new ArrayList<>();
//    private PostViewModel postViewModel;
//    private String staffId;
//
//    public Tab1PostFragment() {
//        // Required empty constructor
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        postViewModel = new ViewModelProvider(requireActivity()).get(PostViewModel.class);
//        notificationUtils = new NotificationUtils();
//
//        // Retrieve staffId from arguments
//        if (getArguments() != null) {
//            staffId = getArguments().getString("staffId");
//            System.out.println("Staff ID: " + staffId);
//        }
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        // Fetch data when the view is created
//        fetchData();
//    }
//
//    @Override
//    protected void fetchData() {
//        // FirebaseFirestore query to fetch the verPosts
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("verPost")
//                .orderBy("timestamp", Query.Direction.DESCENDING) // Fetch data sorted by timestamp
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        List<VerPost> tempList = new ArrayList<>();
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            VerPost verPost = document.toObject(VerPost.class);
//                            verPost.setVerPostId(document.getId());
//                            tempList.add(verPost);
//                        }
//                        updateRecyclerViewAdapter(tempList);
//                    } else {
//                        showToast("Failed to retrieve VerPosts.");
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    logError("Error fetching VerPosts", e);
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
//            adapter.updateData(data); // Make sure updateData is implemented in your adapter
//        }
//        verPostList = data;
//    }
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
//        firestore.collection("verPost")
//                .document(verPost.getVerPostId())
//                .delete()
//                .addOnSuccessListener(aVoid -> {
//                    logMessage("Successfully deleted VerPost: " + verPost.getVerPostId());
//
//                    firestore.collection("post")
//                            .document(verPost.getPostId())
//                            .get()
//                            .addOnSuccessListener(documentSnapshot -> {
//                                if (documentSnapshot.exists()) {
//                                    Post post = documentSnapshot.toObject(Post.class);
//                                    if (post != null) {
//                                        String postTitle = post.getTitle();
//                                        String postUserId = post.getUserId();
//
//                                        firestore.collection("post")
//                                                .document(verPost.getPostId())
//                                                .delete()
//                                                .addOnSuccessListener(aVoid1 -> {
//                                                    logMessage("Successfully deleted Post: " + verPost.getPostId());
//                                                    showToast("Deleted post: " + postTitle);
//
//                                                    notificationUtils.createTestNotification(
//                                                            postUserId,
//                                                            "Your post \"" + postTitle + "\" has been deleted."
//                                                    );
//
//                                                    verPostList.remove(position);
//                                                    adapter.notifyItemRemoved(position);
//                                                })
//                                                .addOnFailureListener(e -> {
//                                                    logError("Failed to delete Post: " + verPost.getPostId(), e);
//                                                    showToast("Failed to delete Post: " + postTitle);
//                                                });
//                                    } else {
//                                        logError("Post object is null", null);
//                                        showToast("Failed to retrieve Post object.");
//                                    }
//                                } else {
//                                    logError("Post document does not exist", null);
//                                    showToast("Post document does not exist.");
//                                }
//                            })
//                            .addOnFailureListener(e -> {
//                                logError("Failed to retrieve Post", e);
//                                showToast("Failed to retrieve Post.");
//                            });
//                })
//                .addOnFailureListener(e -> {
//                    logError("Failed to delete VerPost: " + verPost.getVerPostId(), e);
//                    showToast("Failed to delete VerPost.");
//                });
//    }
//
//    private void handlePostAction(VerPost post, String verifiedStatus, String actionName) {
//        Timestamp currentTimestamp = Timestamp.now();
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//        postViewModel.updateVerPost(post);
//        firestore.collection("verPost")
//                .whereEqualTo("postId", post.getPostId())
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
//                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
//                        String verPostId = document.getId();
//
//                        Map<String, Object> updatedVerPost = new HashMap<>();
//                        updatedVerPost.put("postId", post.getPostId());
//                        updatedVerPost.put("staffId", staffId);
//                        updatedVerPost.put("verifiedStatus", verifiedStatus);
//                        updatedVerPost.put("timestamp", currentTimestamp);
//
//                        firestore.collection("verPost")
//                                .document(verPostId)
//                                .set(updatedVerPost, SetOptions.merge())
//                                .addOnSuccessListener(aVoid -> {
//                                    post.setVerifiedStatus(verifiedStatus); // Update local data
//                                    int index = verPostList.indexOf(post);
//                                    if (index != -1) {
//                                        verPostList.set(index, post);
//                                        adapter.notifyItemChanged(index); // Notify adapter to rebind this item
//                                    }
//
//                                    firestore.collection("post")
//                                            .document(post.getPostId())
//                                            .get()
//                                            .addOnSuccessListener(postDocument -> {
//                                                if (postDocument.exists()) {
//                                                    Post relatedPost = postDocument.toObject(Post.class);
//                                                    if (relatedPost != null) {
//                                                        notificationUtils.createTestNotification(
//                                                                relatedPost.getUserId(),
//                                                                "Your post \"" + relatedPost.getTitle() + "\" has been " + verifiedStatus + "."
//                                                        );
//                                                        showToast(actionName + ": " + relatedPost.getTitle());
//                                                    } else {
//                                                        showToast("Failed to retrieve post details for notification.");
//                                                    }
//                                                } else {
//                                                    showToast("Post not found for postId: " + post.getPostId());
//                                                }
//                                            })
//                                            .addOnFailureListener(e -> {
//                                                logError("Failed to retrieve post details", e);
//                                                showToast("Failed to retrieve post details: " + e.getMessage());
//                                            });
//                                })
//                                .addOnFailureListener(e -> {
//                                    logError("Failed to update verPost", e);
//                                    showToast("Failed to " + actionName.toLowerCase() + ": " + e.getMessage());
//                                });
//                    } else {
//                        showToast("No verPost found for the post: " + post.getPostId());
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    logError("Failed to retrieve verPost", e);
//                    showToast("Failed to " + actionName.toLowerCase() + ": " + e.getMessage());
//                });
//    }
//    @Override
//    public void onCourseTitleClicked(VerPost post, int position) {
//        showToast("Course Title clicked.");
//    }
//    private void showToast(String message) {
//        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
//    }
//
//    private void logError(String message, Exception e) {
//        if (e != null) {
//            Log.e("Tab1PostFragment", message, e);
//        } else {
//            Log.e("Tab1PostFragment", message);
//        }
//    }
//
//    private void logMessage(String message) {
//        Log.d("Tab1PostFragment", message);
//    }
//}
