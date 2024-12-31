package com.example.madguardians;

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
import java.util.List;

public class LearningHistoryAdapter extends RecyclerView.Adapter<LearningHistoryAdapter.LearningHistoryViewHolder> {

    private List<UserHistory> learningHistoryList = new ArrayList<>();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public LearningHistoryAdapter(List<UserHistory> learningHistoryList) {
        this.learningHistoryList = learningHistoryList;
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

        // Search Firestore Course data
        firestore.collection("course")
                .document(history.getCourseId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        String title = document.getString("title");
                        String coverImage = document.getString("coverImage");

                        // Update RecyclerView
                        holder.learningHistoryTitle.setText(title);
                        if (coverImage != null && !coverImage.isEmpty()) {
                            Glide.with(holder.itemView.getContext())
                                    .load(coverImage)
                                    .into(holder.historyImage);
                        } else {
                            holder.historyImage.setImageResource(R.drawable.bg_white_shape); // use default
                        }
                    } else {
                        holder.learningHistoryTitle.setText("Error loading course");
                        holder.historyImage.setImageResource(R.drawable.bg_white_shape);
                    }
                });
    }

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
}

