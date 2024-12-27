package com.example.madguardians.ui.staff;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.FirestoreManager;
import com.example.madguardians.database.HelpdeskDao;
import com.example.madguardians.database.Post;
import com.example.madguardians.database.PostDao;
import com.example.madguardians.database.UserDao;
import com.example.madguardians.database.VerPost;
import com.example.madguardians.database.VerPostDao;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Tab2PostFragment extends BaseTab2Fragment<Post> implements RecycleViewPostAdapter.OnPostActionListener {
    private FirestoreManager firestoreManager;
    private NotificationManager notificationManager;
    private VerPostDao verPostDao;
    private UserDao userDao;

    public Tab2PostFragment() {

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppDatabase appDatabase = AppDatabase.getDatabase(requireContext());
        firestoreManager = new FirestoreManager(appDatabase);
        notificationManager = new NotificationManager(requireContext());
        verPostDao = appDatabase.verPostDao();
        userDao = appDatabase.userDao();
    }

    @Override
    protected List<Post> getData() {
        PostDao postDao = AppDatabase.getDatabase(requireContext()).postDao();
        List<Post> postList = postDao.getAll().getValue(); // Retrieve all posts
        List<Post> postPendingList = new ArrayList<>();

        if (postList == null || postList.isEmpty()) {
            showToast("No posts available.");
            return postPendingList; // Return empty list if no posts are available
        }

        for (Post post : postList) {
            VerPost verPost = verPostDao.getByPostId(post.getPostId());
            if (verPost != null && "pending".equals(verPost.getVerifiedStatus())) {
                postPendingList.add(post);
            }
        }

        if (postPendingList.isEmpty()) {
            showToast("No pending posts available.");
        }

        return postPendingList;
    }

    @Override
    protected RecyclerView.Adapter<?> getAdapter(List<Post> data) {
        return new RecycleViewPostAdapter(data, requireContext(), this);
    }

    @Override
    public void onRejectClicked(Post post, int position) {
        handlePostAction(post, "rejected", "Rejected");
    }

    @Override
    public void onApprovedClicked(Post post, int position) {
        handlePostAction(post, "approved", "Approved");
    }

    @Override
    public void onDeleteClicked(Post post, int position) {
        try {
            VerPost verPost = verPostDao.getByPostId(post.getPostId());
            if (verPost != null) {
                firestoreManager.onDelete("verPost", verPost);
            }
            firestoreManager.onDelete("post", post);

            sendNotificationToUser(post, "Your post \"" + post.getTitle() + "\" has been deleted.");
            showToast("Deleted: " + post.getTitle());
        } catch (Exception e) {
            logError("Failed to delete post", e);
            showToast("Failed to delete: " + e.getMessage());
        }
    }

    @Override
    public void onCourseTitleClicked(Post post, int position) {
        // Handle course title click
        showToast("Course Title clicked: " + post.getTitle());
        // Add any additional actions here
    }

    private void handlePostAction(Post post, String actionStatus, String actionName) {
        try {
            String currentTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            VerPost verPost = verPostDao.getByPostId(post.getPostId());
            if (verPost != null) {
                VerPost updatedVerPost = new VerPost(
                        verPost.getVerPostId(),
                        post.getPostId(),
                        "staffId",  // Replace with actual staff ID if available
                        actionStatus,
                        currentTimestamp
                );
                firestoreManager.onInsertUpdate("update","verPost", updatedVerPost, requireContext());

                sendNotificationToUser(post, "Your post \"" + post.getTitle() + "\" has been " + actionStatus + ".");
                showToast(actionName + ": " + post.getTitle());
            }
        } catch (Exception e) {
            logError("Failed to " + actionName.toLowerCase() + " post", e);
            showToast("Failed to " + actionName.toLowerCase() + ": " + e.getMessage());
        }
    }

    private void sendNotificationToUser(Post post, String message) {
        try {
            notificationManager.sendNotification(post.getUserId(), message);
        } catch (Exception e) {
            logError("Failed to send notification", e);
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void logError(String message, Exception e) {
        // Replace with proper logging framework
        e.printStackTrace();
    }
}