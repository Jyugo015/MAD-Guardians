package com.example.madguardians.ui.staff;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.madguardians.comment.adapter.Listener;
import com.example.madguardians.notification.NotificationUtils;
import com.example.madguardians.ui.staff.VerEducator;
import com.example.madguardians.database.VerEducatorDao;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Tab1EducatorFragment extends BaseTab1Fragment<VerEducator>
        implements RecycleViewEducatorAdapter.OnHandleEducatorActionListener, Listener.OnDomainClickListener {

    private RecycleViewEducatorAdapter adapter;
    private NotificationUtils notificationUtils;
    private List<VerEducator> verEducatorList = new ArrayList<>();
    private String staffId;
    public Tab1EducatorFragment() {

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

        db.collection("verEducator")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Fetch data sorted by timestamp
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        logError("Error fetching VerEducators", e);
                        showToast("Error fetching VerEducators: " + e.getMessage());
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        List<VerEducator> tempList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            VerEducator verEducator = document.toObject(VerEducator.class);

                            // Handle the domainId field
                            Object domainId = document.get("domainId");
                            if (domainId instanceof String) {
                                // Convert single String to List<String>
                                List<String> domainIdList = Collections.singletonList((String) domainId);
                                verEducator.setDomainId(domainIdList);  // Set it to the VerEducator object
                            } else if (domainId instanceof List) {
                                // If it's already a List, do nothing
                                List<String> domainIdList = (List<String>) domainId;
                                verEducator.setDomainId(domainIdList);
                            }

                            verEducator.setVerEducatorId(document.getId());
                            tempList.add(verEducator);
                        }

                        // Update RecyclerView with new data
                        updateRecyclerViewAdapter(tempList);
                    } else {
                        System.out.println("No VerEducators found.");
                        showToast("No VerEducators found.");
                    }
                });
    }

//    @Override
//    protected void fetchData() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("verEducator")
//                .orderBy("timestamp", Query.Direction.DESCENDING) // Fetch data sorted by timestamp
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        List<VerEducator> tempList = new ArrayList<>();
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            VerEducator verEducator = document.toObject(VerEducator.class);
//
//                            // Handle the domainId field
//                            Object domainId = document.get("domainId");
//                            if (domainId instanceof String) {
//                                // Convert single String to List<String>
//                                List<String> domainIdList = Collections.singletonList((String) domainId);
//                                verEducator.setDomainId(domainIdList);  // Set it to the VerEducator object
//                            } else if (domainId instanceof List) {
//                                // If it's already a List, do nothing
//                                List<String> domainIdList = (List<String>) domainId;
//                                verEducator.setDomainId(domainIdList);
//                            }
//
//                            verEducator.setVerEducatorId(document.getId());
//                            tempList.add(verEducator);
//                        }
//                        updateRecyclerViewAdapter(tempList);
//                    } else {
//                        showToast("Failed to retrieve VerEducators.");
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    logError("Error fetching VerEducators", e);
//                    showToast("Error fetching VerEducators: " + e.getMessage());
//                });
//    }

//    @Override
//    protected void fetchData() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("verEducator")
//                .orderBy("timestamp", Query.Direction.DESCENDING) // Fetch data sorted by timestamp
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        List<VerEducator> tempList = new ArrayList<>();
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            // Retrieve fields from the document
//                            String verEducatorId = document.getId(); // Firestore document ID
//                            String userId = document.getString("userId");
//                            String imageSetId = document.getString("imageSetId");
//                            String fileSetId = document.getString("fileSetId");
//                            String domainId = document.getString("domainId");
//                            String staffId = document.getString("staffId");
//                            String verifiedStatus = document.getString("verifiedStatus");
//                            Timestamp timestamp = document.getTimestamp("timestamp"); // Firestore Timestamp field
//
//                            // Ensure all required fields are non-null before creating VerEducator
//                            if (userId != null && verifiedStatus != null && timestamp != null) {
//                                // Create the VerEducator object using the constructor
//                                VerEducator verEducator = new VerEducator(
//                                        verEducatorId,
//                                        userId,
//                                        imageSetId,
//                                        fileSetId,
//                                        domainId,
//                                        staffId,
//                                        verifiedStatus,
//                                        timestamp
//                                );
//                                tempList.add(verEducator);
//                            }
//                        }
//                        updateRecyclerViewAdapter(tempList);
//                    } else {
//                        showToast("Failed to retrieve VerEducators.");
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    logError("Error fetching VerEducators", e);
//                    showToast("Error fetching VerEducators: " + e.getMessage());
//                });
//    }

    @Override
    protected void updateRecyclerViewAdapter(List<VerEducator> data) {
        if (adapter == null) {
            adapter = new RecycleViewEducatorAdapter(data, requireContext(), this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(data); // Make sure updateData is implemented in your adapter
        }
        verEducatorList = data;
    }

    @Override
    public void onRejectClicked(VerEducator educator, int position) {
        handleEducatorAction(educator, "rejected", "Rejected");
    }

    @Override
    public void onApprovedClicked(VerEducator educator, int position) {
        handleEducatorAction(educator, "approved", "Approved");
    }

    @Override
    public void onDeleteClicked(VerEducator verEducator, int position) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Delete VerEducator document
        firestore.collection("verEducator")
                .document(verEducator.getVerEducatorId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    showToast("Successfully deleted VerEducator: " + verEducator.getVerEducatorId());

                    // Notify user about deletion
                    String userId = verEducator.getUserId();
                    notificationUtils.createTestNotification(
                            userId,
                            "Your educator verification record has been deleted."
                    );

                    //not sure
                    // Remove user
                    verEducatorList.remove(position);
                    adapter.notifyItemRemoved(position);

                    if(verEducator.getUserId()!=null){
                        firestore.collection("user")
                                .document(verEducator.getUserId())
                                .delete()
                                .addOnSuccessListener(aVoid1 -> showToast("Deleted user: " + verEducator.getUserId()))
                                .addOnFailureListener(e -> showToast("Failed to delete user: " + verEducator.getUserId()));
                    }
                    // Handle related data deletion
//                    if (verEducator.getImageSetId() != null) {
//                        firestore.collection("images")
//                                .document(verEducator.getImageSetId())
//                                .delete()
//                                .addOnSuccessListener(aVoid1 -> showToast("Deleted associated ImageSet: " + verEducator.getImageSetId()))
//                                .addOnFailureListener(e -> showToast("Failed to delete associated ImageSet: " + verEducator.getImageSetId()));
//                    }
//
//                    if (verEducator.getFileSetId() != null) {
//                        firestore.collection("files")
//                                .document(verEducator.getFileSetId())
//                                .delete()
//                                .addOnSuccessListener(aVoid2 -> showToast("Deleted associated FileSet: " + verEducator.getFileSetId()))
//                                .addOnFailureListener(e -> showToast("Failed to delete associated FileSet: " + verEducator.getFileSetId()));
//                    }
                })
                .addOnFailureListener(e -> {
                    showToast("Failed to delete VerEducator: " + verEducator.getVerEducatorId());
                });
    }

    @Override
    public void onEducatorNameClicked(VerEducator educator, int position) {

    }
    @Override
    public void onViewProofClicked(VerEducator educator, int position) {
//        ViewProofDialogFragment dialog = ViewProofDialogFragment.newInstance();
//        dialog.show(getChildFragmentManager(), "ViewProofDialogFragment");
//        showToast("view proof clicked");
    }

    @Override
    public void onDomainClicked(VerEducator educator, int position) {
        CheckDomainDialogFragment dialog = CheckDomainDialogFragment.newInstance(educator.getVerEducatorId());
        dialog.setDomainListener(Tab1EducatorFragment.this);
//                if (context instanceof AppCompatActivity) {
        Log.w("tvdomain","got run");
//        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        dialog.show(getParentFragmentManager(), "CheckDomainDialogFragment");
//                } else {
        Log.e("DialogError", "Context is not an instance of AppCompatActivity");
//                }
    }

    private void handleEducatorAction(VerEducator verEducator, String verifiedStatus, String actionName) {
        if (verEducator == null || getContext() == null) {
            logError(actionName + " failed: Null educator or context", null);
            return;
        }

//        Timestamp currentTimestamp = Timestamp.now();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("verEducator")
                .whereEqualTo("verEducatorId", verEducator.getVerEducatorId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String verEducatorId = document.getId();

                        Map<String, Object> updatedVerEducator = new HashMap<>();
                        updatedVerEducator.put("verEducatorId", verEducator.getVerEducatorId());
                        updatedVerEducator.put("staffId", staffId);
                        updatedVerEducator.put("verifiedStatus", verifiedStatus);
//                        updatedVerEducator.put("timestamp", currentTimestamp);

                        firestore.collection("verEducator")
                                .document(verEducatorId)
                                .set(updatedVerEducator, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    verEducator.setVerifiedStatus(verifiedStatus); // Update local data
                                    int index = verEducatorList.indexOf(verEducator);
                                    if (index != -1) {
                                        verEducatorList.set(index, verEducator);
                                        adapter.notifyItemChanged(index); // Notify adapter to rebind this item
                                    }

                                    // Create notification message
                                    String notificationMessage = "Your qualification has been " + verifiedStatus + ".";

                                    // Send notification to the user associated with the VerEducator
                                    notificationUtils.createTestNotification(
                                            verEducator.getUserId(),
                                            notificationMessage
                                    );

                                    showToast(actionName + ": " + verifiedStatus + " for educator " + verEducator.getVerEducatorId());
                                })
                                .addOnFailureListener(e -> {
                                    logError("Failed to update VerEducator", e);
                                    showToast("Failed to " + actionName.toLowerCase() + ": " + e.getMessage());
                                });
                    } else {
                        showToast("No VerEducator found for the educator ID: " + verEducator.getVerEducatorId());
                    }
                })
                .addOnFailureListener(e -> {
                    logError("Failed to retrieve VerEducator", e);
                    showToast("Failed to " + actionName.toLowerCase() + ": " + e.getMessage());
                });
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void logError(String message, Exception e) {
        Log.e("Tab1EducatorFragment", message, e);
        if (e != null) e.printStackTrace(); // Optional: Use proper logging for production
    }

    @Override
    public void onDomainClick(VerEducator educator) {
        CheckDomainDialogFragment dialog = CheckDomainDialogFragment.newInstance(educator.getVerEducatorId());
        dialog.setDomainListener(Tab1EducatorFragment.this);
//                if (context instanceof AppCompatActivity) {
        Log.w("tvdomain","got run");
//        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        dialog.show(getParentFragmentManager(), "CheckDomainDialogFragment");
//                } else {
        Log.e("DialogError", "Context is not an instance of AppCompatActivity");
//                }
    }
}