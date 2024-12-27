package com.example.madguardians;

import static android.content.Context.MODE_PRIVATE;
import static com.google.common.reflect.Reflection.getPackageName;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.madguardians.collection.AllCollectionFragment;
import com.example.madguardians.collection.CollectionFragment;
import com.example.madguardians.database.Achievement;
import com.example.madguardians.database.AchievementDao;
import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Badge;
import com.example.madguardians.database.BadgeDao;
import com.example.madguardians.database.Course;
import com.example.madguardians.database.CourseDao;
import com.example.madguardians.database.MediaSetDao;
import com.example.madguardians.database.Post;
import com.example.madguardians.database.PostDao;
import com.example.madguardians.database.User;
import com.example.madguardians.database.UserDao;
import com.example.madguardians.database.UserHistory;
import com.example.madguardians.database.UserHistoryDao;
import com.example.madguardians.notification.notificationFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {
    private RecyclerView achievementRecyclerView;
    private AchievementAdapter achievementAdapter;
    private List<Achievement> achievementList = new ArrayList<>();
    private AppDatabase appDatabase;
    private UserDao userDao;
    private TextView usernameTextView, emailTextView;
    private ImageView profileImageView;
    private LinearLayout achievementContainer, learningHistoryContainer;
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
        // Initialize views
        usernameTextView = rootView.findViewById(R.id.username);
        emailTextView = rootView.findViewById(R.id.gmail);
        profileImageView = rootView.findViewById(R.id.profile);
//        achievementContainer = rootView.findViewById(R.id.achievementContainer);
        learningHistoryContainer = rootView.findViewById(R.id.learningHistoryContainer);
        viewAllAchievement = rootView.findViewById(R.id.viewAllAchievement);
        viewHistoryTextView = rootView.findViewById(R.id.view_learning_history);
        noAchievementsMessage = rootView.findViewById(R.id.noAchievementsMessage);
        noHistoryMessage = rootView.findViewById(R.id.noHistoryMessage);

        //Load user data
        loadUserData();

        // Load achievements dynamically
        loadAchievements();

        // Load learning history dynamically
        loadLearningHistory();

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
        // Database access on a background thread
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                // Get UserHistory Data
                UserHistoryDao userHistoryDao = AppDatabase.getDatabase(getContext()).userHistoryDao();
                PostDao userPostDao = AppDatabase.getDatabase(getContext()).postDao();
                CourseDao courseDao = AppDatabase.getDatabase(getContext()).courseDao();

                // Fetch user history from the database
                List<UserHistory> learningHistoryList = userHistoryDao.getByUserId(userId);

                // Update UI on the main thread
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Clear the container
                        learningHistoryContainer.removeAllViews();

                        // Check if there are learning history items
                        if (learningHistoryList.isEmpty()) {
                            noHistoryMessage.setVisibility(View.VISIBLE);
                        } else {
                            noHistoryMessage.setVisibility(View.GONE);
                        }

                        // Loop through the user history and populate the UI
                        for (UserHistory history : learningHistoryList) {
                            // Fetch the associated post (make sure it's non-blocking)
                            Post post = userPostDao.getById(history.getPostId()).getValue();
                            Course course = courseDao.getById(post.getPostId());

                            if (post != null) {
                                // Inflate the custom item layout for each post
                                View learningHistoryItemView = LayoutInflater.from(getContext()).inflate(R.layout.learning_history, null);

                                // Set post title
                                TextView historyTitle = learningHistoryItemView.findViewById(R.id.PostTitle);
                                historyTitle.setText(post.getTitle());

                                // Get image URL and load the image
                                ImageView historyImage = learningHistoryItemView.findViewById(R.id.PostImage);
                                if (course.getCoverImage()!= null && !course.getCoverImage().isEmpty()) {
                                    Glide.with(getContext())
                                            .load(course.getCoverImage()) // Assuming this is a valid URL or local path
                                            .into(historyImage);
                                } else {
                                    historyImage.setImageResource(R.drawable.bg_white_shape); // Default image if no image is found
                                }

                                // Set onClickListener for the history item
                                learningHistoryItemView.setOnClickListener(v -> {
                                    Toast.makeText(getContext(), "Clicked on " + post.getTitle(), Toast.LENGTH_SHORT).show();
                                });

                                // Add the item view to the container
                                learningHistoryContainer.addView(learningHistoryItemView);
                            }
                        }
                    }
                });
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
}