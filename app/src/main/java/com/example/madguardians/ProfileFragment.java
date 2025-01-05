package com.example.madguardians;

import static android.content.Context.MODE_PRIVATE;
import static com.google.common.reflect.Reflection.getPackageName;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.madguardians.database.Achievement;
import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Course;
import com.example.madguardians.database.Notification;
import com.example.madguardians.database.UserDao;
import com.example.madguardians.database.UserHistory;
import com.example.madguardians.notification.NotificationAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProfileFragment extends Fragment {
    private RecyclerView achievementRecyclerView, learningHistoryRecyclerView;
    private AchievementAdapter achievementAdapter, learningHistoryAdapter;
    private List<Achievement> achievementList = new ArrayList<>();
    private AppDatabase appDatabase;
    private UserDao userDao;
    private TextView usernameTextView, emailTextView;
    private ImageView profileImageView;
    private ImageView haveNotificationImageView;
    private TextView viewAllAchievement, viewHistoryTextView;
    private TextView noAchievementsMessage, noHistoryMessage; // New TextView to show the "No achievements" message
    // Get SharedPreferences
    SharedPreferences sharedPreferences;
    String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get SharedPreferences
        sharedPreferences = getContext().getSharedPreferences("user_preferences", MODE_PRIVATE);
        //Get userid
        userId = sharedPreferences.getString("user_id", null);
        // Initialize database
//        appDatabase = AppDatabase.getDatabase(getContext());
//        //Initialize userDao
//        userDao = appDatabase.userDao();


        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        achievementRecyclerView = rootView.findViewById(R.id.achievementRecyclerView);
        achievementRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        achievementRecyclerView.setLayoutManager(layoutManager);
        learningHistoryRecyclerView = rootView.findViewById(R.id.learningHistoryRecyclerView);
        learningHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        LinearLayoutManager learningHistoryLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        learningHistoryRecyclerView.setLayoutManager(learningHistoryLayoutManager);

        // Initialize views
        usernameTextView = rootView.findViewById(R.id.username);
        emailTextView = rootView.findViewById(R.id.gmail);
        profileImageView = rootView.findViewById(R.id.profile);
//        achievementContainer = rootView.findViewById(R.id.achievementContainer);
//        learningHistoryContainer = rootView.findViewById(R.id.learningHistoryContainer);
        viewAllAchievement = rootView.findViewById(R.id.viewAllAchievement);
        viewHistoryTextView = rootView.findViewById(R.id.view_learning_history);
        noAchievementsMessage = rootView.findViewById(R.id.noAchievementsMessage);
        noHistoryMessage = rootView.findViewById(R.id.noHistoryMessage);
        haveNotificationImageView = rootView.findViewById(R.id.IVHaveNotic);

        //Load user data
        loadUserData();

        // Load achievements dynamically
        loadAchievements();

        // Load learning history dynamically
        loadLearningHistory();

        // Check if there are unread notifications
        checkNotificationReadDot();

        // Set up View All onClick listener
        viewAllAchievement.setOnClickListener(v -> {
            // Show all achievements (you can navigate to a new activity or show a dialog)
            Toast.makeText(getContext(), "View All clicked", Toast.LENGTH_SHORT).show();
        });

        // Set up View History onClick listener
        viewHistoryTextView.setOnClickListener(v -> {
            // Show all learning history (you can navigate to a new activity or show a dialog)
            Toast.makeText(getContext(), "View History clicked", Toast.LENGTH_SHORT).show();
        });


        TextView uploadEducationQualificationTextView = rootView.findViewById(R.id.upload_education_qualification);
        uploadEducationQualificationTextView.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.NavHostFragment);

            navController.navigate(R.id.action_nav_profile_to_educationQualificationFragment);
        });

        ImageView collectionImageView = rootView.findViewById(R.id.collection);
        collectionImageView.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.NavHostFragment);

            navController.navigate(R.id.action_nav_profile_to_collectionFragment);
        });

        ImageView editImageView = rootView.findViewById(R.id.edit);
        editImageView.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.NavHostFragment);

            navController.navigate(R.id.action_nav_profile_to_editProfileFragment);
        });

        ImageView notificationImageView = rootView.findViewById(R.id.notification);
        notificationImageView.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.NavHostFragment);

            navController.navigate(R.id.action_nav_profile_to_notificationFragment);
        });

        TextView viewAllAchievement = rootView.findViewById(R.id.viewAllAchievement);
        viewAllAchievement.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.NavHostFragment);
            navController.navigate(R.id.action_nav_profile_to_viewAllAchievementsFragment);
        });

        TextView viewAllHistory = rootView.findViewById(R.id.viewAllHistory);
        viewAllHistory.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.NavHostFragment);
            navController.navigate(R.id.action_nav_profile_to_viewAllLearningHistoryFragment);
        });

        ImageView commentImageView = rootView.findViewById(R.id.comment);
        commentImageView.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.NavHostFragment);

            navController.navigate(R.id.action_nav_profile_to_Edu_CommentFragment);
        });
        return rootView;
    }

    private void loadUserData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("user")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            String name = document.getString("name");
                            String email = document.getString("email");
                            String profilePicUrl = document.getString("profilePic");

                            if (name != null) usernameTextView.setText(name);
                            if (email != null) emailTextView.setText(email);

                            if (profilePicUrl != null && !"url link of default profile pic".equals(profilePicUrl)) {
                                Glide.with(ProfileFragment.this)
                                        .load(profilePicUrl)
                                        .circleCrop()
                                        .into(profileImageView);
                            } else {
                                profileImageView.setImageResource(R.drawable.ic_profile);
                            }
                        } else {
                            Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadLearningHistory() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        List<UserHistory> learningHistoryList = new ArrayList<>();
        Set<String> uniqueCourseIds = new HashSet<>();
        // Initialize RecyclerView
        LearningHistoryAdapter adapter = new LearningHistoryAdapter(learningHistoryList, history -> {
            if (history.getCourseId() != null) {
                Toast.makeText(getContext(), "Clicked on Course ID: " + history.getCourseId(), Toast.LENGTH_SHORT).show();

                // Navigate to Course Overview
                Bundle bundle = new Bundle();
                bundle.putString("courseId", history.getCourseId());
                Navigation.findNavController(requireView()).navigate(R.id.nav_course_overview, bundle);
            } else {
                Toast.makeText(getContext(), "Course ID not available", Toast.LENGTH_SHORT).show();
            }
        });

        learningHistoryRecyclerView.setAdapter(adapter);


        firestore.collection("userHistory")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            UserHistory history = doc.toObject(UserHistory.class);
                            if (history != null && history.getCourseId() != null) {
                                // check courseId repeat or not
                                if (!uniqueCourseIds.contains(history.getCourseId())) {
                                    uniqueCourseIds.add(history.getCourseId()); // add to course list
                                    learningHistoryList.add(history); // add to list
                                }
                            }
                        }

                        adapter.notifyDataSetChanged();
                        // hide/show noHistoryMessage
                        if (learningHistoryList.isEmpty()) {
                            noHistoryMessage.setVisibility(View.VISIBLE); // show text
                            learningHistoryRecyclerView.setVisibility(View.GONE); // hide RecyclerView
                        } else {
                            noHistoryMessage.setVisibility(View.GONE); // hide text
                            learningHistoryRecyclerView.setVisibility(View.VISIBLE); // show RecyclerView
                        }
                    } else {
                        // If fail loading
                        noHistoryMessage.setVisibility(View.VISIBLE);
                        learningHistoryRecyclerView.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Failed to load learning history", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void loadAchievements() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query Firestore
        db.collection("achievement")
                .whereEqualTo("userId", userId) // Filter berdasarkan userId
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Initialize achievementList
                        List<Achievement> achievementList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Achievement achievement = new Achievement();
                            achievement.setUserId(document.getString("userId"));
                            achievement.setBadgeId(document.getString("badgeId"));

                            // Add all achievement to achievementList
                            achievementList.add(achievement);
                        }

                        // Run RecyclerView in main ui thread
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (achievementList.isEmpty()) {
                                    noAchievementsMessage.setVisibility(View.VISIBLE);
                                } else {
                                    noAchievementsMessage.setVisibility(View.GONE);
                                }

                                // set adapter RecyclerView
                                achievementAdapter = new AchievementAdapter(achievementList);
                                achievementRecyclerView.setAdapter(achievementAdapter);
                            });
                        }
                    } else {
                        // Pop toast if fail to load achievements
                        Toast.makeText(getContext(), "Failed to load achievements", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkNotificationReadDot() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query Firestore
        db.collection("notification")
                .whereEqualTo("userId", userId)
                .whereEqualTo("readTime", null)
                .orderBy("deliveredTime", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            haveNotificationImageView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        haveNotificationImageView.setVisibility(View.GONE);
                    }
                });
    }
}