package com.example.madguardians.ui.staff;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.example.madguardians.database.*;
import com.example.madguardians.notification.NotificationUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tab1ReportedPostFragment extends BaseTab1Fragment<Helpdesk> implements RecycleViewReportedPostAdapter.OnReportedPostActionListener {
    private RecycleViewReportedPostAdapter adapter;
    private NotificationUtils notificationUtils;
    private List<Helpdesk> reportedPostList = new ArrayList<>();
    private String staffId;
    public Tab1ReportedPostFragment() {

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
//fetch course or post?
    protected void fetchData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("helpdesk")
                .whereNotEqualTo("postId", null) // Filter records where postId is not null
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Helpdesk> helpdeskReportedList = new ArrayList<>();

                        // Add Helpdesk records with a postId
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Helpdesk helpdesk = document.toObject(Helpdesk.class);
                            helpdeskReportedList.add(helpdesk);
                        }

                        // Query again for helpdesk records with a courseId (optional chaining)
                        db.collection("helpdesk")
                                .whereNotEqualTo("courseId", null)
                                .get()
                                .addOnCompleteListener(subTask -> {
                                    if (subTask.isSuccessful() && subTask.getResult() != null) {
                                        for (QueryDocumentSnapshot document : subTask.getResult()) {
                                            Helpdesk helpdesk = document.toObject(Helpdesk.class);

                                            // Ensure no duplicates between the two queries
                                            if (!helpdeskReportedList.contains(helpdesk)) {
                                                helpdeskReportedList.add(helpdesk);
                                            }
                                        }

                                        // Sort by timestamp (if present)
                                        helpdeskReportedList.sort((o1, o2) -> {
                                            if (o1.getTimestamp() == null || o2.getTimestamp() == null) return 0;
                                            return o2.getTimestamp().compareTo(o1.getTimestamp());
                                        });

                                        // Handle empty or non-empty result list
                                        if (helpdeskReportedList.isEmpty()) {
                                            System.out.println("No reported helpdesk items found.");
                                            Log.d("Firestore", "No reported helpdesk items found.");
                                            showToast("No reported helpdesk items found.");
                                        } else {
                                            System.out.println("Fetched " + helpdeskReportedList.size() + " helpdesk items.");
                                            Log.d("Firestore", "Fetched " + helpdeskReportedList.size() + " helpdesk items.");
                                            updateRecyclerViewAdapter(helpdeskReportedList);
                                        }
                                    } else {
                                        System.out.println("Error fetching helpdesk records with courseId.");
                                        Log.d("Firestore", "Error fetching helpdesk records with courseId: " + subTask.getException());
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    System.out.println("Error fetching helpdesk records with courseId: " + e.getMessage());
                                    Log.d("Firestore", "Error fetching helpdesk records with courseId: " + e.getMessage());
                                });

                    } else {
                        System.out.println("Failed to retrieve Helpdesk records with postId.");
                        Log.d("Firestore", "Task unsuccessful or no result: " + task.getException());
                        showToast("Failed to retrieve Helpdesk records with postId.");
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error fetching Helpdesk data: " + e.getMessage());
                    Log.d("Firestore", "Error fetching Helpdesk data: " + e.getMessage());
                    showToast("Error fetching Helpdesk data: " + e.getMessage());
                });
    }

    @Override
    protected void updateRecyclerViewAdapter(List<Helpdesk> data) {
        if (adapter == null) {
            adapter = new RecycleViewReportedPostAdapter(data, requireContext(), this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(data); // Make sure updateData is implemented in your adapter
        }
        reportedPostList = data;
    }
    @Override
    public void onKeepClicked(Helpdesk helpdesk, int position) {
        // Update both helpdeskStatus and staffId
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a map to hold the fields to update
        Map<String, Object> updates = new HashMap<>();
        updates.put("helpdeskStatus", "kept");
        updates.put("staffId", staffId);

        db.collection("helpdesk")
                .document(helpdesk.getHelpdeskId())
                .update(updates) // Update multiple fields at once
                .addOnSuccessListener(aVoid -> {
                    logMessage("Helpdesk status and staffId updated for ID: " + helpdesk.getHelpdeskId());

                    // Update the local list
                    helpdesk.setHelpdeskStatus("kept");
                    adapter.notifyItemChanged(position);

                    // Notify the reporter
                    if (helpdesk.getIssueId() != null) {
                        db.collection("issue").document(helpdesk.getIssueId()).get()
                                .addOnSuccessListener(issueSnapshot -> {
                                    if (issueSnapshot.exists()) {
                                        String issueType = issueSnapshot.getString("type");
                                        notificationUtils.createTestNotification(helpdesk.getUserId(),
                                                "Your reported issue with reason " + issueType + " has been reviewed. There is nothing changed.");
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch issue details", e));
                    }

                    // Notify the user
                    if (helpdesk.getCommentId() != null) {
                        db.collection("comment").document(helpdesk.getCommentId()).get()
                                .addOnSuccessListener(commentSnapshot -> {
                                    if (commentSnapshot.exists()) {
                                        String commentUserId = commentSnapshot.getString("userId");
                                        notificationUtils.createTestNotification(commentUserId,
                                                "Your comment has been reviewed. There is nothing changed.");
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch comment details", e));
                    }
                })
                .addOnFailureListener(e -> {
                    logMessage("Failed to update helpdesk status and staffId: " + e.getMessage());
                    showToast("Error updating helpdesk status and staffId: " + e.getMessage());
                });
    }


    @Override
    public void onDeleteClicked(Helpdesk helpdesk, int position) {
        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a map to hold the fields to update
        Map<String, Object> updates = new HashMap<>();
        updates.put("helpdeskStatus", "deleted");
        updates.put("staffId", staffId);

        // Update the helpdesk status to "reviewed"
        db.collection("helpdesk")
                .document(helpdesk.getHelpdeskId())
                .update(updates)  // Update multiple fields at once
                .addOnSuccessListener(updateVoid -> {
                    logMessage("Helpdesk status updated to reviewed for ID: " + helpdesk.getHelpdeskId());

                    // Notify the reporter
                    if (helpdesk.getIssueId() != null) {
                        db.collection("issue")
                                .document(helpdesk.getIssueId())
                                .get()
                                .addOnSuccessListener(issueSnapshot -> {
                                    if (issueSnapshot.exists()) {
                                        String issueType = issueSnapshot.getString("type");
                                        String reporterMessage = "Your reported comment with reason '" + issueType + "' has been reviewed. It has been deleted.";
                                        notificationUtils.createTestNotification(helpdesk.getUserId(), reporterMessage); // Notify reporter
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    logMessage("Failed to fetch issue details: " + e.getMessage());
                                });
                    } else {
                        logMessage("Failed to fetch issue details. " );
                    }

                    // Notify the comment owner (user who posted the comment)
                    db.collection("comment")
                            .document(helpdesk.getCommentId())
                            .get()
                            .addOnSuccessListener(commentSnapshot -> {
                                if (commentSnapshot.exists()) {
                                    String commentUserId = commentSnapshot.getString("userId"); // Comment owner's userId
                                    if (helpdesk.getReason() != null) {
                                        String userMessage = "Your comment has been removed after being reported.";
                                        notificationUtils.createTestNotification(commentUserId, userMessage); // Notify comment owner
                                    }
                                } else {
                                    logMessage("Comment data not found for notification.");
                                }

                                // Delete the comment from Firestore
                                db.collection("comment")
                                        .document(helpdesk.getCommentId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            logMessage("Comment deleted successfully for ID: " + helpdesk.getCommentId());

                                            // Update the adapter
                                            helpdesk.setHelpdeskStatus("deleted");
                                            adapter.notifyItemChanged(position);
                                        })
                                        .addOnFailureListener(deleteError -> {
                                            logMessage("Failed to delete comment: " + deleteError.getMessage());
                                            showToast("Error deleting comment: " + deleteError.getMessage());
                                        });
                            })
                            .addOnFailureListener(e -> logMessage("Failed to fetch comment details: " + e.getMessage()));
                })
                .addOnFailureListener(updateError -> {
                    logMessage("Failed to update helpdesk status: " + updateError.getMessage());
                    showToast("Error updating helpdesk status: " + updateError.getMessage());
                });
    }
//    @Override
//    public void onKeepClicked(Helpdesk helpdesk, int position) {
//        try {
//            String messageToOwner;
//            String messageToReporter;
//            NotificationManager notificationManager = new NotificationManager(requireContext());
//
//            if (helpdesk.getPostId() != null) {
//                Post post = postDao.getById(helpdesk.getPostId()).getValue();
//                messageToOwner = "Your post \"" + post.getTitle() + "\" has been reviewed and kept.";
//                notificationManager.sendNotification(post.getUserId(), messageToOwner);
//
//                messageToReporter = "The post you reported has been reviewed and kept: \"" + post.getTitle() + "\".";
//                notificationManager.sendNotification(helpdesk.getUserId(), messageToReporter);
//
//                showToast("Kept post: " + post.getTitle());
//            } else if (helpdesk.getCourseId() != null) {
//                Course course = courseDao.getById(helpdesk.getCourseId());
//                messageToOwner = "Your course \"" + course.getTitle() + "\" has been reviewed and kept.";
//                notificationManager.sendNotification(course.getPost1(), messageToOwner);
//
//                messageToReporter = "The course you reported has been reviewed and kept: \"" + course.getTitle() + "\".";
//                notificationManager.sendNotification(helpdesk.getUserId(), messageToReporter);
//
//                showToast("Kept course: " + course.getTitle());
//            } else if (helpdesk.getQuizId() != null) {
//                QuizOld quizOld = quizOldDao.getByQuizPostId(helpdesk.getQuizId(), helpdesk.getPostId());
//                messageToOwner = "Your quiz has been reviewed and kept.";
//                notificationManager.sendNotification(quizOld.getPostId(), messageToOwner);
//
//                messageToReporter = "The quiz you reported has been reviewed and kept.";
//                notificationManager.sendNotification(helpdesk.getUserId(), messageToReporter);
//
//                showToast("Kept quiz.");
//            } else {
//                showToast("No valid data found to keep.");
//                return;
//            }
//
//            updateHelpdeskStatus(helpdesk, "reviewed");
//        } catch (Exception e) {
//            handleError("Failed to keep", e);
//        }
//    }
//    @Override
//    public void onDeleteClicked(Helpdesk helpdesk, int position) {
//        try {
//            String messageToOwner;
//            String messageToReporter;
//            NotificationManager notificationManager = new NotificationManager(requireContext());
//
//            if (helpdesk.getPostId() != null) {
//                Post post = postDao.getById(helpdesk.getPostId()).getValue();
//                VerPost verPost = verPostDao.getByPostId(helpdesk.getPostId());
//
//                firestoreManager.onDelete("post", post);
//                firestoreManager.onDelete("verPost", verPost);
//
//                User reporter = userDao.getById(helpdesk.getUserId());
//                Issue issue = issueDao.getById(helpdesk.getIssueId());
//                messageToOwner = "User " + reporter.getName() + " reported you for " + issue.getType() + ". Your post \"" + post.getTitle() + "\" has been deleted.";
//                notificationManager.sendNotification(post.getUserId(), messageToOwner);
//
//                messageToReporter = "You successfully reported " + userDao.getById(post.getUserId()).getName() + "'s post.";
//                notificationManager.sendNotification(helpdesk.getUserId(), messageToReporter);
//
//                showToast("Deleted post: " + post.getTitle());
//            } else if (helpdesk.getCourseId() != null) {
//                Course course = courseDao.getById(helpdesk.getCourseId());
//                firestoreManager.onDelete("course", course);
//
//                messageToOwner = "Your course \"" + course.getTitle() + "\" has been deleted.";
//                notificationManager.sendNotification(course.getPost1(), messageToOwner);
//
//                messageToReporter = "The course you reported has been deleted.";
//                notificationManager.sendNotification(helpdesk.getUserId(), messageToReporter);
//
//                showToast("Deleted course: " + course.getTitle());
//            } else if (helpdesk.getQuizId() != null) {
//                QuizOld quizOld = quizOldDao.getByQuizPostId(helpdesk.getQuizId(), helpdesk.getPostId());
//                firestoreManager.onDelete("quizOld", quizOld);
//
//                messageToOwner = "Your quiz has been deleted.";
//                notificationManager.sendNotification(quizOld.getPostId(), messageToOwner);
//
//                messageToReporter = "The quiz you reported has been deleted.";
//                notificationManager.sendNotification(helpdesk.getUserId(), messageToReporter);
//
//                showToast("Deleted quiz.");
//            } else {
//                showToast("No valid data found to delete.");
//                return;
//            }
//
//            updateHelpdeskStatus(helpdesk, "reviewed");
//        } catch (Exception e) {
//            handleError("Failed to delete", e);
//        }
//    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void handleError(String message, Exception e) {
        Toast.makeText(requireContext(), message + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }
    private void logMessage(String message) {
        Log.d("Tab1PostFragment", message);
    }
}