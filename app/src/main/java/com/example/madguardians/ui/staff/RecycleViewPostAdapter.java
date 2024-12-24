package com.example.madguardians.ui.staff;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Post;
import com.example.madguardians.database.User;
import com.example.madguardians.database.UserDao;
import com.example.madguardians.database.VerPost;
import com.example.madguardians.database.VerPostDao;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewPostAdapter extends RecyclerView.Adapter<RecycleViewPostAdapter.PostViewHolder> {
    private List<Post> postList;
    private Context context;
    private OnPostActionListener onPostActionListener;

    // DAOs
    private VerPostDao verPostDao;
    private UserDao userDao;
    // Constructor
    public RecycleViewPostAdapter(List<Post> postList, Context context, OnPostActionListener onPostActionListener) {
        this.postList = postList != null ? postList : new ArrayList<>();
        this.context = context;
        this.onPostActionListener = onPostActionListener;

        this.verPostDao = AppDatabase.getDatabase(context.getApplicationContext()).verPostDao();
        this.userDao = AppDatabase.getDatabase(context.getApplicationContext()).userDao();
    }

    @Override
    public int getItemViewType(int position) {
        Post post = postList.get(position);


        VerPost verPost = verPostDao.getByPostId(post.getPostId());

        if ("pending".equals(verPost.getVerifiedStatus())) {
            return R.layout.staff_one_line_handle_post_pending_hzw;
        } else {
            return R.layout.staff_one_line_handle_post_completed_hzw;
        }

//        if ("pending".equals(post.getStatus())) {
//            return R.layout.staff_one_line_handle_post_pending;
//        } else {
//            return R.layout.staff_one_line_handle_post_completed;
//        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        User user = userDao.getById(post.getUserId());
        VerPost verPost = verPostDao.getByPostId(post.getPostId());

        // Bind data to views
        holder.tvCourseTitle.setText(post.getTitle());
        holder.tvAuthorName.setText(user.getName());
        holder.tvDate.setText(post.getDate());
        holder.tvStatus.setText(verPost.getVerifiedStatus());

//        post.getImageSetId();
//        // Display the post image using MediaHandler's displayImage method
//        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
//            MediaHandler.displayImage(context, post.getImageUrl(), holder.ivPost);
//        } else {
//            // Optionally set a placeholder or hide the ImageView
//            holder.ivPost.setImageResource(R.drawable.placeholder_image); // Replace with your placeholder resource
//        }

        // Handle course title click
        holder.tvCourseTitle.setOnClickListener(v -> {
            if (onPostActionListener != null) {
                onPostActionListener.onCourseTitleClicked(post, position);
            }
        });
        // Handle buttons based on post status
        if ("pending".equals(verPost.getVerifiedStatus())) {
            if (holder.btnReject != null) {
                holder.btnReject.setOnClickListener(v -> {
                    if (onPostActionListener != null) {
                        onPostActionListener.onRejectClicked(post, position);
                        verPost.setVerifiedStatus("rejected"); //not sure wanna use onInsertUpdate
                        notifyItemChanged(position); // Refresh item
                    }
                });
            }

            if (holder.btnApprove != null) {
                holder.btnApprove.setOnClickListener(v -> {
                    if (onPostActionListener != null) {
                        onPostActionListener.onApprovedClicked(post, position);
                        verPost.setVerifiedStatus("approved"); //not sure
                        notifyItemChanged(position); // Refresh item
                    }
                });
            }
        } else  {
            if (holder.btnDelete != null) {
                holder.btnDelete.setOnClickListener(v -> {
                    if (onPostActionListener != null) {
                        onPostActionListener.onDeleteClicked(post, position);
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return postList != null ? postList.size() : 0;
    }

    public void updateList(List<Post> newPostList) {
        postList = newPostList != null ? newPostList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPost;
        TextView tvCourseTitle, tvAuthorName, tvDate, tvStatus;
        Button btnReject, btnApprove, btnDelete;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPost = itemView.findViewById(R.id.IVPost);
            tvCourseTitle = itemView.findViewById(R.id.TVCourseTitle);
            tvAuthorName = itemView.findViewById(R.id.TVAuthorName);
            tvDate = itemView.findViewById(R.id.TVDate);
            tvStatus = itemView.findViewById(R.id.TVStatus);
            btnReject = itemView.findViewById(R.id.BTNReject);
            btnApprove = itemView.findViewById(R.id.BTNApprove);
            btnDelete = itemView.findViewById(R.id.BTNDelete);
        }
    }
    public interface OnPostActionListener {
        void onRejectClicked(Post post, int position);
        void onApprovedClicked(Post post, int position);
        void onDeleteClicked(Post post, int position);
        void onCourseTitleClicked(Post post, int position);
    }
}

