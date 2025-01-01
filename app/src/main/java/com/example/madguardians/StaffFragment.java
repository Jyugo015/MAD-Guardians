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
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Staff;
import com.example.madguardians.database.StaffDao;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


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
//
//        // Set default values
//        tvStaffName.setText("Loading name...");
//        tvEmail.setText("Loading email...");
//        tvUserId.setText("Loading user ID...");

        // Get SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("staff_preferences", getContext().MODE_PRIVATE);
        staffId = sharedPreferences.getString("staff_id", null); // Retrieve logged-in staff ID
        Log.d("StaffFragment", "Retrieved staffId from SharedPreferences: " + staffId);
//        System.out.println(staffId);

        // Initialize database and DAO
//        appDatabase = AppDatabase.getDatabase(getContext());
//        staffDao = appDatabase.staffDao();
        // Load data (Firestore or Room)
        if (staffId != null) {
            loadUserData(); // Load from Firestore
            // displayStaffDetails(); // Uncomment if you prefer using Room
        } else {
            Toast.makeText(getContext(), "Staff ID not found in preferences", Toast.LENGTH_SHORT).show();
        }
        handlePostText = view.findViewById(R.id.handlePostText);
        handleReportedPostText = view.findViewById(R.id.handleReportedPostText);
        handleReportedCommentText = view.findViewById(R.id.handleReportedCommentText);
        handleEducatorText = view.findViewById(R.id.handleEducatorText);
        View.OnClickListener OCLHandlePost = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                System.out.println("Click, pass "+staffId);
                bundle.putString("staffId", staffId);
                Navigation.findNavController(view).navigate(R.id.action_nav_staff_to_handlePostFragment,bundle);
            }
        };


        View.OnClickListener OCLHandleReportedPost = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("staffId", staffId);
                Navigation.findNavController(view).navigate(R.id.action_nav_staff_to_handleReportedPostFragment,bundle);
            }
        };
        View.OnClickListener OCLHandleReportedComment = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("staffId", staffId);
                Navigation.findNavController(view).navigate(R.id.action_nav_staff_to_handleReportedCommentFragment,bundle);
            }
        };
        View.OnClickListener OCLHandleEducator = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("staffId", staffId);
                Navigation.findNavController(view).navigate(R.id.action_nav_staff_to_handleEducatorFragment,bundle);
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

    private void loadUserData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("staff")
                .document(staffId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            String name = document.getString("name");
                            String email = document.getString("email");
                            String staffId = document.getString("staffId");
                            String password = document.getString("password"); // Ensure password is fetched

                            if (name != null) tvStaffName.setText(name);
                            if (email != null) tvEmail.setText(email);
                            if (staffId != null) tvUserId.setText(staffId);
                            // Handle missing password
                            if (password == null) {
                                Log.e("StaffFragment", "Password is missing for staff ID: " + staffId);
                            }
                        } else {
                            Toast.makeText(getContext(), "Staff data not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
//    private void displayStaffDetails() {
//        if (staffId != null) {
//            new Thread(() -> {
//                Staff staff = staffDao.getById(staffId);
//                if (staff != null) {
//                    Log.d("StaffFragment", "Staff found: " + staff.getName());
//                    requireActivity().runOnUiThread(() -> {
//                        tvStaffName.setText(staff.getName());
//                        tvEmail.setText(staff.getEmail());
//                        tvUserId.setText(staff.getStaffId());
//
//
//                    });
//                } else {
//                    Log.d("StaffFragment", "Staff not found for ID: " + staffId);
//                }
//            }).start();
//        } else {
//            Log.d("StaffFragment", "staffId is null");
//        }
//    }
}