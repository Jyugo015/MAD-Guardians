package com.example.madguardians.ui.staff;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.madguardians.database.Issue;
import com.example.madguardians.notification.NotificationUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//delete havent do
public class Tab1ReportedCommentFragment extends BaseTab1Fragment<Helpdesk> implements RecycleViewReportedCommentAdapter.OnReportedCommentActionListener{
    private RecycleViewReportedCommentAdapter adapter;
    private NotificationUtils notificationUtils;
    private List<Helpdesk> reportedCommentList = new ArrayList<>();
    private String staffId;
    public Tab1ReportedCommentFragment (){

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
    protected void fetchData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("helpdesk")
                .whereGreaterThan("commentId", "")
                .get() // Fetch all documents without sorting in Firestore
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Helpdesk> helpdeskReportedCommentList = new ArrayList<>();
                        System.out.println("Documents found: " + task.getResult().size());
                        Log.d("Firestore", "Documents found: " + task.getResult().size());

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            System.out.println("Document ID: " + document.getId() + ", Data: " + document.getData());
                            Log.d("Firestore", "Document ID: " + document.getId() + ", Data: " + document.getData());

                            Helpdesk helpdesk = document.toObject(Helpdesk.class);
                            helpdeskReportedCommentList.add(helpdesk);
                        }

                        // Sort the list by Timestamp locally
                        helpdeskReportedCommentList.sort((o1, o2) -> {
                            if (o1.getTimestamp() == null || o2.getTimestamp() == null) return 0;
                            return o2.getTimestamp().compareTo(o1.getTimestamp()); // Descending order
                        });

                        if (helpdeskReportedCommentList.isEmpty()) {
                            System.out.println("No reported comments available.");
                            Log.d("Firestore", "No reported comments available.");
                            showToast("No reported comments available.");
                        } else {
                            System.out.println("Updating adapter with " + helpdeskReportedCommentList.size() + " items.");
                            Log.d("Firestore", "Updating adapter with " + helpdeskReportedCommentList.size() + " items.");
                            updateRecyclerViewAdapter(helpdeskReportedCommentList);
                        }
                    } else {
                        System.out.println("Failed to retrieve Helpdesk records.");
                        Log.d("Firestore", "Task unsuccessful or no result: " + task.getException());
                        showToast("Failed to retrieve Helpdesk records.");
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error fetching Helpdesk data: " + e.getMessage());
                    Log.d("Firestore", "Error fetching Helpdesk data: " + e.getMessage());
                    showToast("Error fetching Helpdesk data: " + e.getMessage());
                });
    }

//    protected void fetchData() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("helpdesk")
//                .whereGreaterThan("commentId", "") // Ensure these fields are indexed together
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        List<Helpdesk> helpdeskReportedCommentList = new ArrayList<>();
//                        System.out.println("Documents found: " + task.getResult().size());
//                        Log.d("Firestore", "Documents found: " + task.getResult().size());
//
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            System.out.println("Document ID: " + document.getId() + ", Data: " + document.getData());
//                            Log.d("Firestore", "Document ID: " + document.getId() + ", Data: " + document.getData());
//
//                            Helpdesk helpdesk = document.toObject(Helpdesk.class);
//                            helpdeskReportedCommentList.add(helpdesk);
//                        }
//
//                        if (helpdeskReportedCommentList.isEmpty()) {
//                            System.out.println("No reported comments available.");
//                            Log.d("Firestore", "No reported comments available.");
//                            showToast("No reported comments available.");
//                        } else {
//                            System.out.println("Updating adapter with " + helpdeskReportedCommentList.size() + " items.");
//                            Log.d("Firestore", "Updating adapter with " + helpdeskReportedCommentList.size() + " items.");
//                            updateRecyclerViewAdapter(helpdeskReportedCommentList);
//                        }
//                    } else {
//                        System.out.println("Failed to retrieve Helpdesk records.");
//                        Log.d("Firestore", "Task unsuccessful or no result: " + task.getException());
//                        showToast("Failed to retrieve Helpdesk records.");
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    System.out.println("Error fetching Helpdesk data: " + e.getMessage());
//                    Log.d("Firestore", "Error fetching Helpdesk data: " + e.getMessage());
//                    showToast("Error fetching Helpdesk data: " + e.getMessage());
//                });
//    }

    @Override
    protected void updateRecyclerViewAdapter(List<Helpdesk> data) {
        if (adapter == null) {
            adapter = new RecycleViewReportedCommentAdapter(data, requireContext(), this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(data); // Make sure updateData is implemented in your adapter
        }
        reportedCommentList = data;
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
                                                "Your reported comment with reason " + issueType + " has been reviewed. There is nothing changed.");
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

    public void onReportedDescrClicked(Helpdesk helpdesk, int position) {
        // Handle course title click
        Toast.makeText(requireContext(), "Reported comment clicked: " , Toast.LENGTH_SHORT).show();
        // Add any additional actions here
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void logMessage(String message) {
        Log.d("Tab1PostFragment", message);
    }
}
