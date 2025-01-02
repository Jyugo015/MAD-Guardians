//package com.example.madguardians.ui.staff;
//
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.madguardians.database.AppDatabase;
//import com.example.madguardians.database.Helpdesk;
//import com.example.madguardians.database.HelpdeskDao;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Tab3ReportedCommentFragment extends BaseTab3Fragment<Helpdesk>
//        implements RecycleViewReportedCommentAdapter.OnReportedCommentActionListener {
//
//    private HelpdeskDao helpdeskDao;
//
//    public Tab3ReportedCommentFragment (){
//
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        helpdeskDao = AppDatabase.getDatabase(requireContext()).helpdeskDao();
//    }
//    @Override
//    protected List<Helpdesk> getData() {
//        List<Helpdesk> helpdeskPendingReportedCommentList = new ArrayList<>();
//        try {
//            List<Helpdesk> helpdeskList = helpdeskDao.getAll();
//
//            for (Helpdesk helpdesk : helpdeskList) {
//                // Filter helpdesk items with non-null commentId and "pending" status
//                if (helpdesk.getCommentId() != null
//                        && "reviewed".equals(helpdesk.getHelpdeskStatus())) {
//                    helpdeskPendingReportedCommentList.add(helpdesk);
//                }
//            }
//
//            if (helpdeskPendingReportedCommentList.isEmpty()) {
//                showToast("No reported comments available.");
//            }
//        } catch (Exception e) {
//            showToast("An error occurred while fetching data.");
//            e.printStackTrace(); // Optional: Log the error for debugging purposes
//        }
//        return helpdeskPendingReportedCommentList;
//    }
//
//    @Override
//    protected RecyclerView.Adapter<?> getAdapter(List<Helpdesk> data) {
//        return new RecycleViewReportedCommentAdapter(data, requireContext(), this);
//    }
//
//    @Override
//    public void onReportedDescrClicked(Helpdesk helpdesk, int position) {
//        // Handle the reported comment click action
//        Toast.makeText(requireContext(), "Reported comment clicked.", Toast.LENGTH_SHORT).show();
//        // Add additional actions if necessary
//    }
//
//    // Helper method to simplify toast messages
//    private void showToast(String message) {
//        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
//    }
//}