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

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Tab1PostFragment extends BaseTab1Fragment<Post> implements RecycleViewPostAdapter.OnPostActionListener {
    private FirestoreManager firestoreManager;
    private NotificationManager notificationManager;
    private VerPostDao verPostDao;
    private UserDao userDao;

    public Tab1PostFragment() {

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
        List<Post> data = postDao.getAll().getValue();

        if (data == null || data.isEmpty()) {
            showToast("No posts available.");
        }
        return data;
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


//package com.example.madguardians.ui.staff;
//
//import androidx.recyclerview.widget.RecyclerView;
//import android.widget.Toast;
//
//import com.example.madguardians.database.AppDatabase;
//import com.example.madguardians.database.FirestoreManager;
//import com.example.madguardians.database.Helpdesk;
//import com.example.madguardians.database.HelpdeskDao;
//import com.example.madguardians.database.Post;
//import com.example.madguardians.database.PostDao;
//import com.example.madguardians.database.User;
//import com.example.madguardians.database.UserDao;
//import com.example.madguardians.database.VerPost;
//import com.example.madguardians.database.VerPostDao;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class Tab1PostFragment extends BaseTab1Fragment<Post> implements RecycleViewPostAdapter.OnPostActionListener{
//    private VerPost verPost;
//    FirestoreManager firestoreManager = new FirestoreManager(AppDatabase.getDatabase(requireContext()));
//    NotificationManager notificationManager;
//
//    //dao
//    VerPostDao verPostDao = AppDatabase.getDatabase(requireContext()).verPostDao();
//    UserDao userDao = AppDatabase.getDatabase(requireContext()).userDao();
//
//    @Override
//    protected List<Post> getData() {
//        PostDao postDao = AppDatabase.getDatabase(requireContext()).postDao();
//        List<Post> data = postDao.getAll().getValue();
//
//        if (data.isEmpty()) {
//            Toast.makeText(requireContext(), "No posts available.", Toast.LENGTH_SHORT).show();
//        }
//        return data;
//    }
//    @Override
//    protected RecyclerView.Adapter<?> getAdapter(List<Post> data) {
//        return new RecycleViewPostAdapter(data, requireContext(),this);
//    }
//    @Override
//    public void onRejectClicked(Post post, int position) {
//        try {
//            verPost = verPostDao.getByPostId(post.getPostId());
//            VerPost newVerPost = new VerPost(verPost.getVerPostId(), post.getPostId(), "staffId", "rejected");
//
//            firestoreManager.onInsertUpdate("verPost", newVerPost, requireContext());
//
//            // Send notification to the user and update in database
//            String message = "Your post \"" + post.getTitle() + "\" has been rejected.";
//            NotificationManager notificationManager = new NotificationManager(requireContext());
//            notificationManager.sendNotification(post.getUserId(), message);
//
//            Toast.makeText(requireContext(), "Rejected: " + post.getTitle(), Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(requireContext(), "Failed to reject: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onApprovedClicked(Post post, int position) {
//        try {
//            verPost = verPostDao.getByPostId(post.getPostId());
//            VerPost newVerPost = new VerPost(verPost.getVerPostId(), post.getPostId(), "staffId", "approved");
//
//            firestoreManager.onInsertUpdate("verPost", newVerPost, requireContext());
//
//            // Send notification to the user and update in database
//            String message = "Your post \"" + post.getTitle() + "\" has been approved.";
//            NotificationManager notificationManager = new NotificationManager(requireContext());
//            notificationManager.sendNotification(post.getUserId(), message);
//
//            Toast.makeText(requireContext(), "Approved: " + post.getTitle(), Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(requireContext(), "Failed to approve: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onDeleteClicked(Post post, int position) {
//        try {
//            verPost = verPostDao.getByPostId(post.getPostId());
//            firestoreManager.onDelete("verPost",verPost);
//            firestoreManager.onDelete("post",post);
//
//            // Send notification to the user and update in database
//            String message = "Your post \"" + post.getTitle() + "\" has been deleted.";
//            NotificationManager notificationManager = new NotificationManager(requireContext());
//            notificationManager.sendNotification(post.getUserId(), message);
//
//            Toast.makeText(requireContext(), "Deleted: " + post.getTitle(), Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(requireContext(), "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    //转跳
//    @Override
//    public void onCourseTitleClicked(Post post, int position) {
//        // Handle course title click
//        Toast.makeText(requireContext(), "Course Title clicked: " + post.getTitle(), Toast.LENGTH_SHORT).show();
//        // Add any additional actions here
//    }
//}
