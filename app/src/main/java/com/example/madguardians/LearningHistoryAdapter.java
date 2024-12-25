package com.example.madguardians;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.MediaReadDao;
import com.example.madguardians.database.Post;
import com.example.madguardians.database.UserHistory;

import java.util.List;

public class LearningHistoryAdapter extends RecyclerView.Adapter<LearningHistoryAdapter.LearningHistoryViewHolder> {

    private List<UserHistory> learningHistoryList;

    public LearningHistoryAdapter(List<UserHistory> learningHistoryList) {
        this.learningHistoryList = learningHistoryList;
    }

    @Override
    public LearningHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.learning_history, parent, false);
        return new LearningHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LearningHistoryViewHolder holder, int position) {
        UserHistory history = learningHistoryList.get(position);

        // Fetch the post associated with this history
        Post post = AppDatabase.getDatabase(holder.itemView.getContext()).postDao().getById(history.getPostId()).getValue();
        MediaReadDao mediaReadDao = AppDatabase.getDatabase(holder.itemView.getContext()).mediaReadDao();

        if (post != null) {
            // Bind data to your views (e.g., set post title, description)
            holder.learningHistoryTitle.setText(post.getTitle());
            // If you have an image, you can load it using Glide or Picasso
            Glide.with(holder.itemView.getContext()).load(mediaReadDao.getByPostId(post.getPostId())).into(holder.historyImage);
        }
    }

    @Override
    public int getItemCount() {
        return learningHistoryList.size();
    }

    public class LearningHistoryViewHolder extends RecyclerView.ViewHolder {
        // Declare views for learning history item
        TextView learningHistoryTitle;
        ImageView historyImage;

        public LearningHistoryViewHolder(View itemView) {
            super(itemView);
            learningHistoryTitle = itemView.findViewById(R.id.PostTitle);
            historyImage = itemView.findViewById(R.id.PostImage);
        }
    }
}

