package com.example.madguardians.ui.staff;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Comment;
import com.example.madguardians.database.CommentDao;
import com.example.madguardians.database.Helpdesk;
import com.example.madguardians.database.Issue;
import com.example.madguardians.database.IssueDao;
import com.example.madguardians.database.User;
import com.example.madguardians.database.UserDao;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewReportedCommentAdapter extends RecyclerView.Adapter<RecycleViewReportedCommentAdapter.PostViewHolder> {
    private List<Helpdesk> helpdeskList;
    private Context context;
    private OnReportedCommentActionListener onReportedCommentActionListener;
    private Helpdesk helpdesk;

    // Constructor
    public RecycleViewReportedCommentAdapter(List<Helpdesk> helpdeskList, Context context, OnReportedCommentActionListener onReportedCommentActionListener) {
        this.helpdeskList = helpdeskList!=null?helpdeskList:new ArrayList<>();
        this.context = context;
        this.onReportedCommentActionListener = onReportedCommentActionListener;
    }

    @Override
    public int getItemViewType(int position) {
        Helpdesk helpdesk = helpdeskList.get(position);

        if ("pending".equals(helpdesk.getHelpdeskStatus())) {
            return R.layout.staff_one_line_handle_reported_comment_pending_hzw;
        } else {
            return R.layout.staff_one_line_handle_reported_comment_completed_hzw;
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

        UserDao userDao = AppDatabase.getDatabase(context).userDao();
        CommentDao commentDao = AppDatabase.getDatabase(context).commentDao();
        IssueDao issueDao = AppDatabase.getDatabase(context).issueDao();

        //need add timestamp in db
        //holder.tvDate.setText(helpdesk.getDate());
        holder.tvDate.setText("Date");

        //retrieve data to show descr
        User userReport = userDao.getById(helpdesk.getUserId());
        String reportedName = userReport.getName();

        Comment comment =commentDao.getById(helpdesk.getCommentId());
        User userComment = userDao.getById(comment.getUserId());
        String userName = userComment.getName();
        String descr = reportedName + " reported on "+ userName +" comment.";
        holder.tvDescription.setText(descr);

        //show reason
        Issue issue = issueDao.getById(helpdesk.getIssueId());
        holder.tvReason.setText(issue.getType());

        holder.tvDescription.setOnClickListener(v -> {
            if (onReportedCommentActionListener != null) {
                onReportedCommentActionListener.onReportedDescrClicked(helpdesk, position);
            }
        });

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
        TextView tvDescription, tvDate, tvStatus, tvReason;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPost = itemView.findViewById(R.id.IVPost);
            tvDescription = itemView.findViewById(R.id.TVDescr);
            tvDate = itemView.findViewById(R.id.TVReportDate);
            tvReason = itemView.findViewById(R.id.TVReason);
        }
    }

    public interface OnReportedCommentActionListener {
        void onReportedDescrClicked(Helpdesk helpdesk, int position);
    }
}
