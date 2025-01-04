package com.example.madguardians.ui.staff;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.database.*;
import com.example.madguardians.firebase.PostFB;
import com.example.madguardians.notification.NotificationUtils;
import com.example.madguardians.ui.course.PostFragment;
import com.example.madguardians.utilities.UploadCallback;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tab1ReportedPostFragment extends BaseTab1Fragment<Helpdesk> implements RecycleViewReportedPostAdapter.OnReportedPostActionListener {
    private RecycleViewReportedPostAdapter adapter;
    private NotificationUtils notificationUtils;
    private List<Helpdesk> helpdeskList = new ArrayList<>();
    private String staffId;
    private PostFB post;
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
//            PostFB.getPost(getArguments().getString("postId"), new UploadCallback<PostFB>(){
//                @Override
//                public void onSuccess(PostFB result) {
//                    post = result;
//                }
//                @Override
//                public void onFailure(Exception e) {
//                    Log.e("TAG", "onFailure: ", e);
//                }
//            });

//            reportMedia("IMG00002");
//            reportMedia("PDF00004");
//            reportMedia("VID00007");
        }
    }
    private void reportMedia(String mediaId) {
        // Call generateNextHelpdeskId to fetch the next available ID
        generateNextHelpdeskId("helpdesk", new PostFragment.OnIdGeneratedListener() {
            @Override
            public void onIdGenerated(String helpdeskId) {
                if (helpdeskId != null) {
                    // Once the ID is generated, proceed with creating the helpdesk data
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                    // Create a HashMap to store data
                    Map<String, Object> helpdeskData = new HashMap<>();
                    helpdeskData.put("commentId", null); // Left as null
                    helpdeskData.put("helpdeskId", helpdeskId);
                    helpdeskData.put("helpdeskStatus", "pending");
                    helpdeskData.put("issueId", null); // Left as null
                    helpdeskData.put("reason", null); // Left as null
                    helpdeskData.put("reportedItemId", mediaId);
                    helpdeskData.put("staffId", null); // Left as null
                    helpdeskData.put("timestamp", new Timestamp(new Date())); // Current timestamp
                    helpdeskData.put("userId", null); // Left as null

                    // Add the data to Firestore
                    firestore.collection("helpdesk")
                            .document(helpdeskId)
                            .set(helpdeskData)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("reportMedia", "Media reported successfully!");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("reportMedia", "Error reporting media", e);
                            });
                } else {
                    Log.e("reportMedia", "Failed to generate helpdesk ID");
                }
            }
        });
    }
    public void generateNextHelpdeskId(String tableName, PostFragment.OnIdGeneratedListener callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(tableName)
                .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String newHelpdeskId = "H00000"; // Default ID if no documents exist
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String lastDocumentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        // Extract numeric part and increment
                        int numericPart = Integer.parseInt(lastDocumentId.substring(1));
                        numericPart++;
                        newHelpdeskId = String.format("H%05d", numericPart); // Format with leading zeros
                    }
                    callback.onIdGenerated(newHelpdeskId);
                })
                .addOnFailureListener(e -> {
                    Log.e("generateNextHelpdeskId", "Error fetching last document ID", e);
                    callback.onIdGenerated(null); // Notify failure
                });
    }
    protected void fetchData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("helpdesk")
                .whereGreaterThan("reportedItemId", "")
                .get() // Fetch all documents without sorting in Firestore
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Helpdesk> helpdeskList = new ArrayList<>();
                        System.out.println("Documents found: " + task.getResult().size());
                        Log.d("Firestore", "Documents found: " + task.getResult().size());

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            System.out.println("Document ID: " + document.getId() + ", Data: " + document.getData());
                            Log.d("Firestore", "Document ID: " + document.getId() + ", Data: " + document.getData());

                            Helpdesk helpdesk = document.toObject(Helpdesk.class);
                            helpdeskList.add(helpdesk);
                        }

                        // Sort the list by Timestamp locally
                        helpdeskList.sort((o1, o2) -> {
                            if (o1.getTimestamp() == null || o2.getTimestamp() == null) return 0;
                            return o2.getTimestamp().compareTo(o1.getTimestamp()); // Descending order
                        });

                        if (helpdeskList.isEmpty()) {
                            System.out.println("No reported comments available.");
                            Log.d("Firestore", "No reported comments available.");
                            showToast("No reported comments available.");
                        } else {
                            System.out.println("Updating adapter with " + helpdeskList.size() + " items.");
                            Log.d("Firestore", "Updating adapter with " + helpdeskList.size() + " items.");
                            updateRecyclerViewAdapter(helpdeskList);
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
////fetch course or post?
//    protected void fetchData() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("helpdesk")
//                .whereGreaterThan("reportedItemId", "")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        List<Helpdesk> helpdeskReportedList = new ArrayList<>();
//
//                        // Add Helpdesk records with a postId
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Helpdesk helpdesk = document.toObject(Helpdesk.class);
//                            helpdeskReportedList.add(helpdesk);
//                            System.out.println("add post");
//                        }
//
//                        // Query again for helpdesk records with a courseId (optional chaining)
//                        db.collection("helpdesk")
//                                .whereNotEqualTo("courseId", null)
//                                .get()
//                                .addOnCompleteListener(subTask -> {
//                                    if (subTask.isSuccessful() && subTask.getResult() != null) {
//                                        for (QueryDocumentSnapshot document : subTask.getResult()) {
//                                            Helpdesk helpdesk = document.toObject(Helpdesk.class);
//
//                                            // Ensure no duplicates between the two queries
//                                            if (!helpdeskReportedList.contains(helpdesk)) {
//                                                helpdeskReportedList.add(helpdesk);
//                                            }
//                                        }
//
//                                        // Sort by timestamp (if present)
//                                        helpdeskReportedList.sort((o1, o2) -> {
//                                            if (o1.getTimestamp() == null || o2.getTimestamp() == null) return 0;
//                                            return o2.getTimestamp().compareTo(o1.getTimestamp());
//                                        });
//
//                                        // Handle empty or non-empty result list
//                                        if (helpdeskReportedList.isEmpty()) {
//                                            System.out.println("No reported helpdesk items found.");
//                                            Log.d("Firestore", "No reported helpdesk items found.");
//                                            showToast("No reported helpdesk items found.");
//                                        } else {
//                                            System.out.println("Fetched " + helpdeskReportedList.size() + " helpdesk items.");
//                                            Log.d("Firestore", "Fetched " + helpdeskReportedList.size() + " helpdesk items.");
//                                            updateRecyclerViewAdapter(helpdeskReportedList);
//                                        }
//                                    } else {
//                                        System.out.println("Error fetching helpdesk records with courseId.");
//                                        Log.d("Firestore", "Error fetching helpdesk records with courseId: " + subTask.getException());
//                                    }
//                                })
//                                .addOnFailureListener(e -> {
//                                    System.out.println("Error fetching helpdesk records with courseId: " + e.getMessage());
//                                    Log.d("Firestore", "Error fetching helpdesk records with courseId: " + e.getMessage());
//                                });
//
//                    } else {
//                        System.out.println("Failed to retrieve Helpdesk records with postId.");
//                        Log.d("Firestore", "Task unsuccessful or no result: " + task.getException());
//                        showToast("Failed to retrieve Helpdesk records with postId.");
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
            adapter = new RecycleViewReportedPostAdapter(data, requireContext(), this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(data); // Make sure updateData is implemented in your adapter
        }
        helpdeskList = data;
    }
    @Override
    public void onKeepClicked(Helpdesk helpdesk, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> updates = new HashMap<>();
        updates.put("helpdeskStatus", "kept");
        updates.put("staffId", staffId);

        db.collection("helpdesk")
                .document(helpdesk.getHelpdeskId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    logMessage("Helpdesk status and staffId updated for ID: " + helpdesk.getHelpdeskId());

                    helpdesk.setHelpdeskStatus("kept");
                    adapter.notifyItemChanged(position);
                    notifyReporter(helpdesk, db);
                    notifyItemOwner(helpdesk, db);
                })
                .addOnFailureListener(e -> {
                    logMessage("Failed to update helpdesk status and staffId: " + e.getMessage());
                    showToast("Error updating helpdesk status and staffId: " + e.getMessage());
                });
    }

    private void notifyReporter(Helpdesk helpdesk, FirebaseFirestore db) {
        if (helpdesk.getIssueId() != null) {
            db.collection("issue").document(helpdesk.getIssueId()).get()
                    .addOnSuccessListener(issueSnapshot -> {
                        if (issueSnapshot.exists()) {
                            String issueType = issueSnapshot.getString("type");
                            notificationUtils.createTestNotification(
                                    helpdesk.getUserId(),
                                    "Your reported issue with reason '" + issueType + "' has been reviewed. There is nothing changed."
                            );
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch issue details: " + e.getMessage()));
        }
    }
//only send to post owner
    private void notifyItemOwner(Helpdesk helpdesk, FirebaseFirestore db) {
        if (helpdesk.getReportedItemId() != null) {
            db.collection("post").document(helpdesk.getReportedItemId()).get()
                    .addOnSuccessListener(postSnapshot -> {
                        if (postSnapshot.exists()) {
                            String postUserId = postSnapshot.getString("userId");
                            notificationUtils.createTestNotification(
                                    postUserId,
                                    "Your post has been reviewed. There is nothing changed."
                            );
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch post details: " + e.getMessage()));
        }
    }

    public String getTableName(String reportedItemId) {
        if (reportedItemId == null || reportedItemId.isEmpty()) {
            throw new IllegalArgumentException("Reported item ID cannot be null or empty.");
        }

        // Check for media types first
        if (reportedItemId.startsWith("IMG") ||
                reportedItemId.startsWith("PDF") ||
                reportedItemId.startsWith("VID")) {
            return "media";
        }

        // Check for post
        if (reportedItemId.startsWith("P") && !reportedItemId.startsWith("PDF")) {
            return "post";
        }

        throw new IllegalArgumentException("Invalid reported item ID format.");
    }
    public String getMediaType(String mediaId) {
        if (mediaId == null || mediaId.isEmpty()) {
            throw new IllegalArgumentException("Media ID cannot be null or empty.");
        }

        if (mediaId.startsWith("VID")) {
            return "video";
        } else if (mediaId.startsWith("IMG")) {
            return "image";
        } else if (mediaId.startsWith("PDF")) {
            return "pdf";
        } else {
            throw new IllegalArgumentException("Unknown media ID format: " + mediaId);
        }
    }

    @Override
    public void onDeleteClicked(Helpdesk helpdesk, int position) {
        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Validate reportedItemId
        if (helpdesk.getReportedItemId() == null || helpdesk.getReportedItemId().isEmpty()) {
            logMessage("Reported item ID is null or empty.");
            showToast("Invalid reported item ID.");
            return;
        }

        // Determine table based on reportedItemId
        String table = getTableName(helpdesk.getReportedItemId());

        // Create a map to hold the fields to update
        Map<String, Object> updates = new HashMap<>();
        updates.put("helpdeskStatus", "deleted");
        updates.put("staffId", staffId);

        // Update the helpdesk status to "deleted"
        db.collection("helpdesk")
                .document(helpdesk.getHelpdeskId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    logMessage("Helpdesk status updated to 'deleted' for ID: " + helpdesk.getHelpdeskId());

                    // Notify the reporter
                    if (helpdesk.getIssueId() != null) {
                        db.collection("issue")
                                .document(helpdesk.getIssueId())
                                .get()
                                .addOnSuccessListener(issueSnapshot -> {
                                    if (issueSnapshot.exists()) {
                                        String issueType = issueSnapshot.getString("type");
                                        String reporterMessage = "The report you made with reason '" + issueType + "' has been reviewed. It has been deleted.";
                                        notificationUtils.createTestNotification(helpdesk.getUserId(), reporterMessage);
                                    } else {
                                        logMessage("Issue document does not exist.");
                                    }
                                })
                                .addOnFailureListener(e -> logMessage("Failed to fetch issue details: " + e.getMessage()));
                    }

                    // Handle deletion based on the table
                    if ("post".equals(table)) {
                        deleteFromPostTable(db, helpdesk, position);
                    } else if ("media".equals(table)) {
                        deleteFromMediaTable(db, helpdesk, position);
                    } else {
                        logMessage("Unknown table: " + table);
                        showToast("Failed to determine table type.");
                    }
                })
                .addOnFailureListener(updateError -> {
                    logMessage("Failed to update helpdesk status: " + updateError.getMessage());
                    showToast("Error updating helpdesk status: " + updateError.getMessage());
                });
    }

    // Helper method to delete from post table
    private void deleteFromPostTable(FirebaseFirestore db, Helpdesk helpdesk, int position) {
        db.collection("post")
                .document(helpdesk.getReportedItemId())
                .get()
                .addOnSuccessListener(postSnapshot -> {
                    if (postSnapshot.exists()) {
                        Post post = postSnapshot.toObject(Post.class);
                        if (post != null) {
                            String postTitle = post.getTitle();
                            String postUserId = post.getUserId();

                            db.collection("post")
                                    .document(helpdesk.getReportedItemId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        logMessage("Successfully deleted Post: " + helpdesk.getReportedItemId());
                                        showToast("Deleted post: " + postTitle);

                                        notificationUtils.createTestNotification(
                                                postUserId,
                                                "Your post \"" + postTitle + "\" has been deleted."
                                        );

                                        helpdeskList.remove(position);
                                        adapter.notifyItemRemoved(position);
                                    })
                                    .addOnFailureListener(e -> {
                                        logMessage("Failed to delete Post: " + helpdesk.getReportedItemId());
                                        showToast("Failed to delete Post.");
                                    });
                        } else {
                            logMessage("Post object is null.");
                            showToast("Failed to retrieve Post object.");
                        }
                    } else {
                        logMessage("Post document does not exist.");
                        showToast("Post document does not exist.");
                    }
                })
                .addOnFailureListener(e -> {
                    logMessage("Failed to retrieve Post: " + e.getMessage());
                    showToast("Failed to retrieve Post.");
                });
    }

    // Helper method to delete from media table
    private void deleteFromMediaTable(FirebaseFirestore db, Helpdesk helpdesk, int position) {
        db.collection("media")
                .document(helpdesk.getReportedItemId())
                .get()
                .addOnSuccessListener(mediaSnapshot -> {
                    if (mediaSnapshot.exists()) {
                        Media media = mediaSnapshot.toObject(Media.class);
                        if (media != null) {
                            String mediaType = getMediaType(helpdesk.getReportedItemId());

                            db.collection("media")
                                    .document(helpdesk.getReportedItemId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        logMessage("Successfully deleted Media: " + helpdesk.getReportedItemId());
                                        showToast("Deleted media: " + mediaType);
//                                        notificationUtils.createTestNotification(
//                                                helpdesk.getUserId(),
//                                                "Your " + mediaType + " media has been deleted."
//                                        );
                                        helpdeskList.remove(position);
                                        adapter.notifyItemRemoved(position);
                                    })
                                    .addOnFailureListener(e -> {
                                        logMessage("Failed to delete Media: " + helpdesk.getReportedItemId());
                                        showToast("Failed to delete Media.");
                                    });
                        } else {
                            logMessage("Media object is null.");
                            showToast("Failed to retrieve Media object.");
                        }
                    } else {
                        logMessage("Media document does not exist.");
                        showToast("Media document does not exist.");
                    }
                })
                .addOnFailureListener(e -> {
                    logMessage("Failed to retrieve Media: " + e.getMessage());
                    showToast("Failed to retrieve Media.");
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
//    @Override
//    public void onPostTitleClicked(Helpdesk helpdesk, int position){
//        NavController navController = Navigation.findNavController(requireActivity(), R.id.NavHostsFragmentStaff);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("post", post);
//        navController.navigate(R.id.nav_user_comment, bundle);
//    }
}