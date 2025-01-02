//package com.example.madguardians.ui.staff;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.madguardians.database.AppDatabase;
//import com.example.madguardians.database.FirestoreManager;
//import com.example.madguardians.database.Helpdesk;
//import com.example.madguardians.database.User;
//import com.example.madguardians.database.UserDao;
//import com.example.madguardians.database.VerEducator;
//import com.example.madguardians.database.VerEducatorDao;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Tab2EducatorFragment extends BaseTab2Fragment<VerEducator>
//        implements RecycleViewEducatorAdapter.OnHandleEducatorActionListener {
//
//    private FirestoreManager firestoreManager;
//    private NotificationManager notificationManager;
//    private VerEducatorDao verEducatorDao;
//    private UserDao userDao;
//
//    public Tab2EducatorFragment() {
//
//    }
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        firestoreManager = new FirestoreManager(AppDatabase.getDatabase(requireContext()));
//        notificationManager = new NotificationManager(requireContext());
//        verEducatorDao = AppDatabase.getDatabase(requireContext()).verEducatorDao();
//        userDao = AppDatabase.getDatabase(requireContext()).userDao();
//    }
//
//    @Override
//    protected List<VerEducator> getData() {
//        List<VerEducator> verEducatorPendingList = new ArrayList<>();
//
//        try {
//            List<VerEducator> verEducatorList = verEducatorDao.getAll();
//
//            for (VerEducator verEducator : verEducatorList) {
//                if ("pending".equals(verEducator.getVerifiedStatus())) {
//                    verEducatorPendingList.add(verEducator);
//                }
//            }
//        } catch (Exception e) {
//            logError("Error while fetching VerEducator data", e);
//            showToast("Failed to load pending educators: " + e.getMessage());
//        }
//
//        return verEducatorPendingList;
//    }
//
//
//    @Override
//    protected RecyclerView.Adapter<?> getAdapter(List<VerEducator> data) {
//        // Bind data to the RecyclerView adapter
//        return new RecycleViewEducatorAdapter(data, requireContext(), this);
//    }
//
//    @Override
//    public void onRejectClicked(VerEducator educator, int position) {
//        handleEducatorAction(educator, "rejected", "Rejected");
//    }
//
//    @Override
//    public void onApprovedClicked(VerEducator educator, int position) {
//        handleEducatorAction(educator, "approved", "Approved");
//    }
//
//    private void saveApprovedFields(VerEducator educator, List<String> selectedFields) {
//        User user = userDao.getById(educator.getUserId());
//        if (selectedFields != null && !selectedFields.isEmpty()) {
//            Log.d("SaveData", "Saving fields for " + user.getName() + ": " + selectedFields);
//            // Implement your logic for saving selected fields
//        } else {
//            Log.d("SaveData", "No fields selected for " + user.getName());
//        }
//    }
//
//    @Override
//    public void onDeleteClicked(VerEducator educator, int position) {
//        handleEducatorAction(educator, "deleted", "Deleted");
//    }
//
//    @Override
//    public void onEducatorNameClicked(VerEducator educator, int position) {
//
//    }
//
//    @Override
//    public void onViewProofClicked(VerEducator educator, int position) {
//        ViewProofDialogFragment dialog = ViewProofDialogFragment.newInstance();
//        dialog.show(getChildFragmentManager(), "ViewProofDialogFragment");
//    }
//    private void handleEducatorAction(VerEducator verEducator, String actionStatus, String actionName) {
//        if (verEducator == null || getContext() == null) {
//            logError(actionName + " failed: Null educator or context", null);
//            return;
//        }
//
//        try {
//            verEducator.setStaffId(""); // Update staff ID (e.g., logged-in staff's ID if available)
//            verEducator.setVerifiedStatus(actionStatus);
//            firestoreManager.onInsertUpdate("update","verEducator", verEducator, requireContext());
//
//            sendNotificationToUser(verEducator, "Your qualification has been " + actionStatus + ".");
//            User applier = userDao.getById(verEducator.getUserId());
//
//            showToast(actionName + ": " + (applier != null ? applier.getName() : "Unknown User"));
//        } catch (Exception e) {
//            logError("Failed to " + actionName.toLowerCase() + " educator", e);
//        }
//    }
//    private void sendNotificationToUser(VerEducator verEducator, String message) {
//        try {
//            notificationManager.sendNotification(verEducator.getUserId(), message);
//        } catch (Exception e) {
//            logError("Failed to send notification", e);
//        }
//    }
//    private void showToast(String message) {
//        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
//    }
//    private void logError(String message, Exception e) {
//        Log.e("Tab1EducatorFragment", message, e);
//        if (e != null) e.printStackTrace(); // Optional: Use proper logging for production
//    }
//}