package com.example.madguardians;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madguardians.database.Achievement;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewAllAchievementsFragment extends Fragment {
    private RecyclerView achievementGridRecyclerView;
    private AchievementAdapter achievementAdapter;
    private TextView noAchievementsMessage;
    private SharedPreferences sharedPreferences;
    private String userId;
    private List<Achievement> achievementList = new ArrayList<>();

    public ViewAllAchievementsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_all_achievements, container, false);

        // Get SharedPreferences
        sharedPreferences = getContext().getSharedPreferences("user_preferences", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", null);

        achievementGridRecyclerView = view.findViewById(R.id.achievementGridRecyclerView);
        noAchievementsMessage = view.findViewById(R.id.noAchievementsMessage);

        // Set up RecyclerView with GridLayoutManager
        achievementGridRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 两列

        // Set the adapter
        achievementAdapter = new AchievementAdapter(achievementList);
        achievementGridRecyclerView.setAdapter(achievementAdapter);

        // Load achievements from Firestore
        loadAchievements();

        return view;
    }

    private void loadAchievements() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query Firestore to get achievements for the user
        db.collection("achievement")
                .whereEqualTo("userId", userId) // Filter by userId
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        achievementList.clear(); // Clear existing data
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Achievement achievement = new Achievement();
                            achievement.setUserId(document.getString("userId"));
                            achievement.setBadgeId(document.getString("badgeId"));

                            // Add achievement to the list
                            achievementList.add(achievement);
                        }

                        // Run UI updates on the main thread
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                // Notify adapter that data has changed
                                achievementAdapter.notifyDataSetChanged();

                                // Show or hide the "No achievements" message
                                if (achievementList.isEmpty()) {
                                    noAchievementsMessage.setVisibility(View.VISIBLE);
                                } else {
                                    noAchievementsMessage.setVisibility(View.GONE);
                                }
                            });
                        }
                    } else {
                        // Show toast message if the data load fails
                        Toast.makeText(getContext(), "Failed to load achievements", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}