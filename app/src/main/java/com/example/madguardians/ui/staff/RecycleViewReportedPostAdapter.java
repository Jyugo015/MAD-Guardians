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
import com.example.madguardians.database.Course;
import com.example.madguardians.database.CourseDao;
import com.example.madguardians.database.Helpdesk;
import com.example.madguardians.database.Issue;
import com.example.madguardians.database.IssueDao;
import com.example.madguardians.database.Post;
import com.example.madguardians.database.PostDao;
import com.example.madguardians.database.User;
import com.example.madguardians.database.UserDao;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewReportedPostAdapter extends RecyclerView.Adapter<RecycleViewReportedPostAdapter.PostViewHolder> {
    private List<Helpdesk> helpdeskList;
    private Context context;
    private OnReportedPostActionListener onReportedPostActionListener;

    // Constructor
    public RecycleViewReportedPostAdapter(List<Helpdesk> helpdeskList, Context context,OnReportedPostActionListener onReportedPostActionListener) {
        this.helpdeskList = helpdeskList!=null?helpdeskList:new ArrayList<>();
        this.context = context;
        this.onReportedPostActionListener = onReportedPostActionListener;
    }

    @Override
    public int getItemViewType(int position) {
        Helpdesk helpdesk = helpdeskList.get(position);

        if ("pending".equals(helpdesk.getHelpdeskStatus())) {
            return R.layout.staff_one_line_handle_reported_post_pending_hzw;
        } else {
            return R.layout.staff_one_line_handle_reported_post_completed_hzw;
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Helpdesk helpdesk = helpdeskList.get(position);

        PostDao postDao = AppDatabase.getDatabase(context).postDao();
        CourseDao courseDao = AppDatabase.getDatabase(context).courseDao();
        UserDao userDao = AppDatabase.getDatabase(context).userDao();
        IssueDao issueDao = AppDatabase.getDatabase(context).issueDao();

        User user = userDao.getById(helpdesk.getUserId());
        String authorName = "Unknown";
        String title = "Title not found";
        if (helpdesk.getPostId() != null) {
            Post post = postDao.getById(helpdesk.getPostId()).getValue();
            if (post != null) {
                title = post.getTitle();
                User author = userDao.getById(post.getUserId());
                if (author != null) {
                    authorName = author.getName();
                }
            }
        } else if (helpdesk.getCourseId()!=null) {
            Course course = courseDao.getById(helpdesk.getCourseId());
            if (course!=null){
                title = course.getTitle();
                Post post = postDao.getById(course.getPost1()).getValue();
                if (post!=null){
                    User author = userDao.getById(post.getUserId());
                    authorName = author.getName();
                }
            }
        } else if (helpdesk.getQuizId()!=null) {
            Post post = postDao.getById(helpdesk.getQuizId()).getValue();
            if (post!=null){
                title = post.getTitle();
                User author = userDao.getById(post.getUserId());
                if (author!=null){
                    authorName = author.getName();
                }
            }
        }

        //show reason
        Issue issue = issueDao.getById(helpdesk.getIssueId());
        if (issue!=null){
            holder.tvReason.setText(issue.getType());
        }

        // Bind data to views
        holder.tvCourseTitle.setText(title);
        holder.tvAuthorName.setText(authorName);
        holder.tvStatus.setText(helpdesk.getHelpdeskStatus());

        if ("pending".equals(helpdesk.getHelpdeskStatus())) {
            if (holder.btnKeep != null) {
                holder.btnKeep.setOnClickListener(v -> {
                    if (onReportedPostActionListener != null) {
                        onReportedPostActionListener.onKeepClicked(helpdesk, position);
                        helpdesk.setHelpdeskStatus("reviewed");
                        notifyItemChanged(position); // Refresh item
                    }
                });
            }
            if (holder.btnDelete != null) {
                holder.btnDelete.setOnClickListener(v -> {
                    if (onReportedPostActionListener != null) {
                        onReportedPostActionListener.onDeleteClicked(helpdesk, position);
                        notifyItemChanged(position); // Refresh item
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return helpdeskList!=null?helpdeskList.size():0;
    }

    public void updateList(List<Helpdesk> newHelpdeskList) {
        helpdeskList = newHelpdeskList!=null?newHelpdeskList:new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPost;
        TextView tvCourseTitle, tvAuthorName, tvStatus, tvReason;
        Button btnDelete, btnKeep;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPost = itemView.findViewById(R.id.IVPost);
            tvCourseTitle = itemView.findViewById(R.id.TVCourseTitle);
            tvAuthorName = itemView.findViewById(R.id.TVAuthorName);
            tvStatus = itemView.findViewById(R.id.TVStatus);
            tvReason = itemView.findViewById(R.id.TVReason);
            btnDelete = itemView.findViewById(R.id.BTNDelete);
            btnKeep = itemView.findViewById(R.id.BTNKeep);
        }
    }
    public interface OnReportedPostActionListener {
        void onKeepClicked(Helpdesk post, int position);
        void onDeleteClicked(Helpdesk post, int position);
    }
}
