package com.example.madguardians;


import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Staff;
import com.example.madguardians.database.StaffDao;


public class StaffFragment extends Fragment {


    private Button handlePostButton;
    private Button handleReportedPostButton;
    private Button handleReportedCommentButton;
    private Button handleEducatorButton;
    private TextView handlePostText;
    private TextView handleReportedPostText;
    private TextView handleReportedCommentText;
    private TextView handleEducatorText;
    // Get SharedPreferences
    SharedPreferences sharedPreferences;
    String staffId;
    private AppDatabase appDatabase;
    private StaffDao staffDao;
    private TextView tvStaffName;
    private TextView tvEmail,tvUserId;
    private TextView tvAge;


    public StaffFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_staff, container, false);


        tvStaffName = view.findViewById(R.id.TVUsername);
        tvEmail = view.findViewById(R.id.TVEmail);
        tvUserId = view.findViewById(R.id.TVUserId);
//        tvAge = view.findViewById(R.id.TVAge);
        // Get SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("staff_preferences", getContext().MODE_PRIVATE);
        staffId = sharedPreferences.getString("staff_id", null); // Retrieve logged-in staff ID


        // Initialize database and DAO
        appDatabase = AppDatabase.getDatabase(getContext());
        staffDao = appDatabase.staffDao();
        displayStaffDetails();
        handlePostText = view.findViewById(R.id.handlePostText);
        handleReportedPostText = view.findViewById(R.id.handleReportedPostText);
        handleReportedCommentText = view.findViewById(R.id.handleReportedCommentText);
        handleEducatorText = view.findViewById(R.id.handleEducatorText);
        View.OnClickListener OCLHandlePost = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_nav_staff_to_handlePostFragment);
            }
        };


        View.OnClickListener OCLHandleReportedPost = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_nav_staff_to_handleReportedPostFragment);
            }
        };
        View.OnClickListener OCLHandleReportedComment = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_nav_staff_to_handleReportedCommentFragment);
            }
        };
        View.OnClickListener OCLHandleEducator = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_nav_staff_to_handleEducatorFragment);
            }
        };
//        handlePostButton.setOnClickListener(OCLHandlePost);
//        handleReportedPostButton.setOnClickListener(OCLHandleReportedPost);
//        handleReportedCommentButton.setOnClickListener(OCLHandleReportedComment);
//        handleEducatorButton.setOnClickListener(OCLHandleEducator);
        handlePostText.setOnClickListener(OCLHandlePost);
        handleReportedPostText.setOnClickListener(OCLHandleReportedPost);
        handleReportedCommentText.setOnClickListener(OCLHandleReportedComment);
        handleEducatorText.setOnClickListener(OCLHandleEducator);


        return view;
    }


    private void displayStaffDetails() {
        if (staffId != null) {
            new Thread(() -> {
                Staff staff = staffDao.getById(staffId);
                if (staff != null) {
                    Log.d("StaffFragment", "Staff found: " + staff.getName());
                    requireActivity().runOnUiThread(() -> {
                        tvStaffName.setText(staff.getName());
                        tvEmail.setText(staff.getEmail());
                        tvUserId.setText(staff.getStaffId());


                    });
                } else {
                    Log.d("StaffFragment", "Staff not found for ID: " + staffId);
                }
            }).start();
        } else {
            Log.d("StaffFragment", "staffId is null");
        }
    }
}
