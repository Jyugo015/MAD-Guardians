package com.example.madguardians;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.MediaReadDao;
import com.example.madguardians.database.Post;
import com.example.madguardians.database.UserHistory;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LearningHistoryAdapter extends RecyclerView.Adapter<LearningHistoryAdapter.LearningHistoryViewHolder> {

    private List<UserHistory> learningHistoryList = new ArrayList<>();
    private OnItemClickListener listener;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private Map<String, Pair<String, String>> courseCache = new HashMap<>();

    public LearningHistoryAdapter(List<UserHistory> learningHistoryList, OnItemClickListener listener) {
        this.learningHistoryList = learningHistoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LearningHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.learning_history, parent, false);
        return new LearningHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LearningHistoryViewHolder holder, int position) {
        UserHistory history = learningHistoryList.get(position);
        String courseId = history.getCourseId();

        if (courseId != null) {
            // Show loading message
            holder.learningHistoryTitle.setText("Loading...");
            holder.historyImage.setImageResource(R.drawable.bg_white_shape); // default pic

            // check cache
            if (courseCache.containsKey(courseId)) {
                // load data from cache
                Pair<String, String> courseData = courseCache.get(courseId);
                holder.learningHistoryTitle.setText(courseData.first);
                if (courseData.second != null && !courseData.second.isEmpty()) {
                    Glide.with(holder.itemView.getContext())
                            .load(courseData.second)
                            .into(holder.historyImage);
                } else {
                    holder.historyImage.setImageResource(R.drawable.bg_white_shape);
                }
            } else {
                // Firestore search and save
                firestore.collection("course")
                        .document(courseId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                DocumentSnapshot document = task.getResult();
                                String title = document.getString("title");
                                String coverImage = document.getString("coverImage");

                                // Safe to cache
                                courseCache.put(courseId, new Pair<>(title, coverImage));

                                // Update UI
                                holder.learningHistoryTitle.setText(title);
                                if (coverImage != null && !coverImage.isEmpty()) {
                                    Glide.with(holder.itemView.getContext())
                                            .load(coverImage)
                                            .into(holder.historyImage);
                                } else {
                                    holder.historyImage.setImageResource(R.drawable.bg_white_shape);
                                }
                            } else {
                                holder.learningHistoryTitle.setText("Error loading course");
                                holder.historyImage.setImageResource(R.drawable.bg_white_shape);
                            }
                        });
            }
        } else {
            holder.learningHistoryTitle.setText("Course ID not available");
            holder.historyImage.setImageResource(R.drawable.bg_white_shape); // default pic
        }

        // set onClick listener at item
        holder.itemView.setOnClickListener(v -> listener.onItemClick(history));}

    @Override
    public int getItemCount() {
        return learningHistoryList.size();
    }

    public class LearningHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView learningHistoryTitle;
        ImageView historyImage;

        public LearningHistoryViewHolder(View itemView) {
            super(itemView);
            learningHistoryTitle = itemView.findViewById(R.id.CourseTitle);
            historyImage = itemView.findViewById(R.id.CourseImage);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(UserHistory userHistory);
    }
}

