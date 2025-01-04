package com.example.madguardians.comment.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.ui.staff.Helpdesk;
import com.example.madguardians.database.Issue;
import com.google.firebase.Timestamp;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.IssueViewHolder>{
    private static List<Issue> issueList = List.of();
    private FirestoreComment firestoreManager;
    private String userId;
    private String commentId;
    private Listener.OnIssueListener listener;
    private Listener.onHelpdeskListener helpdeskListener;

    public void setIssueList(List<Issue> issues) {
        this.issueList = issues;
        firestoreManager = new FirestoreComment();
        notifyDataSetChanged();
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public void setCommentId(String commentId){this.commentId = commentId;}

    @Override
//    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    public IssueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_report_item, parent, false);
        return new IssueViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(IssueViewHolder holder, int position) {
        // Bind the data to the ViewHolder
        Issue issue = issueList.get(position);
        // Bind the UI elements with data
        holder.type.setText(issue.getType());

        holder.bind(position, new OnItemPressedListener() {
            @Override
            public void onItemPressed(Issue issue) {
                firestoreManager.getLastDocumentId("helpdesk",lastHelpdeskId -> {
                    if (listener != null) {
                        listener.issueClicked();
                    }
                    if (lastHelpdeskId != null) {
                        Log.d("Result", "Last Helpdesk ID: " + lastHelpdeskId);
                        Helpdesk helpdesk = new Helpdesk(
                                getNumericPart(lastHelpdeskId)>99999?"H"+ String.valueOf(getNumericPart(lastHelpdeskId)):"H" + String.format("%05d", getNumericPart(lastHelpdeskId)),
                                issue.getIssueId(),
                                userId,
                                commentId,
                                null,
                                null,
                                null,
                                "pending",
                                Timestamp.now());
                        firestoreManager.insertHelpdesk(helpdesk);
                        Log.d("Firestore Write", "Comment successfully added");
                        helpdeskListener.helpdeskAdded();
                    } else {
                        Log.e("Firestore Write", "Error adding comment");
                    }
                });
                Log.d("ItemPressed", "Item pressed: " + issue.getType());
            }
        });
    }
    public int getNumericPart(String documentId) {
        // Remove the 'COM' prefix
        String numericPart = documentId.substring(1);

        // Trim leading zeros by parsing the string to an integer and then back to a string
        int numericValue = Integer.parseInt(numericPart)+1;
        return numericValue; // Convert back to string
    }

    @Override
    public int getItemCount() {return issueList.size();}

    public static class IssueViewHolder extends RecyclerView.ViewHolder {
        public TextView type;

        public IssueViewHolder(View itemView) {
            super(itemView);
            type= itemView.findViewById(R.id.issue_type);
        }

        public void bind(int position, ReportAdapter.OnItemPressedListener listener) {
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemPressed(issueList.get(position));
                }
            });
        }
    }

    public interface OnItemPressedListener{
        void onItemPressed(Issue issue);
    }

    public void setIssueListener(Listener.OnIssueListener listener){
        this.listener = listener;
    }

    public void setHelpdeskListener(Listener.onHelpdeskListener listener){
        this.helpdeskListener = listener;
    }
}

