package com.example.madguardians;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madguardians.database.Achievement;
import com.example.madguardians.database.UserHistory;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class viewAllLearningHistoryFragment extends Fragment {

    private RecyclerView historyGridRecyclerView;
    private AchievementAdapter historyAdapter;
    private TextView noHistoryMessage;
    private SharedPreferences sharedPreferences;
    private String userId;
    private List<Achievement> historyList = new ArrayList<>();

    public viewAllLearningHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_all_learning_history, container, false);

        // Get SharedPreferences
        sharedPreferences = getContext().getSharedPreferences("user_preferences", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", null);

        historyGridRecyclerView = view.findViewById(R.id.historyGridRecyclerView);
        noHistoryMessage = view.findViewById(R.id.noHistoryMessage);

        // Set up RecyclerView with GridLayoutManager
        historyGridRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 两列

        // Set the adapter
        historyAdapter = new AchievementAdapter(historyList);
        historyGridRecyclerView.setAdapter(historyAdapter);

        // Load achievements from Firestore
        loadHistory();

        return view;
    }

    private void loadHistory() {
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

        historyGridRecyclerView.setAdapter(adapter);


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
                            historyGridRecyclerView.setVisibility(View.GONE); // hide RecyclerView
                        } else {
                            noHistoryMessage.setVisibility(View.GONE); // hide text
                            historyGridRecyclerView.setVisibility(View.VISIBLE); // show RecyclerView
                        }
                    } else {
                        // If fail loading
                        noHistoryMessage.setVisibility(View.VISIBLE);
                        historyGridRecyclerView.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Failed to load learning history", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}