package com.example.madguardians.ui.staff;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.madguardians.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecycleViewReportedCommentAdapter extends RecyclerView.Adapter<RecycleViewReportedCommentAdapter.PostViewHolder> {
    private List<Helpdesk> helpdeskList;
    private Context context;
    private OnReportedCommentActionListener onReportedCommentActionListener;
    private Helpdesk helpdesk;
    private final FirebaseFirestore firestore;
    private final CollectionReference commentRef;

    // Constructor
    public RecycleViewReportedCommentAdapter(List<Helpdesk> helpdeskList, Context context, OnReportedCommentActionListener onReportedCommentActionListener) {
        this.helpdeskList = helpdeskList!=null?helpdeskList:new ArrayList<>();
        this.context = context;
        this.onReportedCommentActionListener = onReportedCommentActionListener;

        this.firestore = FirebaseFirestore.getInstance();
        this.commentRef = firestore.collection("comment");
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

    public void updateData(List<Helpdesk> newData) {
        this.helpdeskList = newData; // Assuming postList is the list in your adapter
        notifyDataSetChanged(); // Notify the adapter about data changes
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

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Set default values for the UI
        if("pending".equals(helpdesk.getHelpdeskStatus())){
            holder.tvDescription.setText("Loading...");
            holder.tvDate.setText("Loading...");
            holder.tvInfo.setText("Loading...");
            holder.tvInfo2.setText("......");
        }else if("deleted".equals(helpdesk.getHelpdeskStatus())) {
            holder.tvInfo.setText("Deleted");
            holder.tvInfo2.setText("The reported comment has been removed.");
        }

//        holder.tvReason.setText("Loading...");
        holder.ivPost.setImageResource(R.drawable.hzw_ic_profile); // Set a placeholder image

        // Fetch User who reported the comment
        if (helpdesk.getUserId() != null) {
            db.collection("user").document(helpdesk.getUserId()).get()
                    .addOnSuccessListener(userSnapshot -> {
                        if (userSnapshot.exists()) {
                            String reportedName = userSnapshot.getString("name");
                            String profilePicUrl = userSnapshot.getString("profilePic");

                            // Load profile picture into ivPost
                            if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                                Glide.with(holder.ivPost.getContext())
                                        .load(profilePicUrl)
                                        .placeholder(R.drawable.hzw_ic_profile)
                                        .error(R.drawable.error_image)
                                        .into(holder.ivPost);
                            }

                            // Fetch Comment details
                            if (helpdesk.getCommentId() != null) {
                                commentRef.document(helpdesk.getCommentId()).get()
                                        .addOnSuccessListener(commentSnapshot -> {
                                            if (commentSnapshot.exists()) {
                                                String commentUserId = commentSnapshot.getString("userId");
                                                String commentOrigin = commentSnapshot.getString("comment");
                                                holder.tvInfo2.setText(commentOrigin);
                                                System.out.println("Comment: "+commentOrigin);
                                                // Fetch User who made the comment
                                                if (commentUserId != null) {
                                                    db.collection("user").document(commentUserId).get()
                                                            .addOnSuccessListener(commentUserSnapshot -> {
                                                                if (commentUserSnapshot.exists()) {
                                                                    String userName = commentUserSnapshot.getString("name");
                                                                    String descr = reportedName + " reported on " + userName + "'s comment:";

                                                                    holder.tvInfo.setText(descr);
                                                                    // Set click listener for description
                                                                    holder.tvDescription.setOnClickListener(v -> {
                                                                        if (onReportedCommentActionListener != null) {
                                                                            onReportedCommentActionListener.onReportedDescrClicked(helpdesk, position);
                                                                        }
                                                                    });
                                                                }
                                                            })
                                                            .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch comment user data", e));
                                                }
                                            }
                                        })
                                        .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch comment details", e));
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch reported user data", e));
        }

        // Fetch Issue details
        if (helpdesk.getIssueId() != null) {
            db.collection("issue").document(helpdesk.getIssueId()).get()
                    .addOnSuccessListener(issueSnapshot -> {
                        if (issueSnapshot.exists()) {
                            String issueType = issueSnapshot.getString("type");
                            holder.tvDescription.setText(issueType);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Failed to fetch issue details", e));
        }

        if (helpdesk.getTimestamp() != null) {
            // Convert Timestamp to String
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String formattedDate = sdf.format(helpdesk.getTimestamp().toDate());
            holder.tvDate.setText(formattedDate);
        } else {
            holder.tvDate.setText("Date not available");
        }
        // Handle actions based on verifiedStatus
        String helpdeskStatus = helpdesk.getHelpdeskStatus();

        if ("pending".equals(helpdeskStatus)) {

            holder.btnKeep.setOnClickListener(v -> {
                if (onReportedCommentActionListener != null) {
                    onReportedCommentActionListener.onKeepClicked(helpdesk, position);
                    firestore.collection("helpdesk")
                            .document(helpdesk.getHelpdeskId())
                            .update("helpdeskStatus", "kept");
                    notifyItemChanged(position);
                }
            });

            holder.btnDelete.setOnClickListener(v -> {
                if (onReportedCommentActionListener != null) {
                    onReportedCommentActionListener.onDeleteClicked(helpdesk, position);

                    // Update Firestore
                    firestore.collection("helpdesk")
                            .document(helpdesk.getHelpdeskId())
                            .update("helpdeskStatus", "deleted")
                            .addOnSuccessListener(aVoid -> {
                                // Update local data model
                                helpdesk.setHelpdeskStatus("reviewed");
                                // Update UI
                                holder.tvInfo.setText("Deleted");
                                holder.tvInfo2.setText("The reported comment has been deleted by staff.");
                                notifyItemChanged(position);
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firestore", "Failed to update helpdesk status", e);
                                Toast.makeText(holder.itemView.getContext(), "Failed to delete comment", Toast.LENGTH_SHORT).show();
                            });
                }
            });
        }
//        holder.tvDescription.setOnClickListener(v -> navigateToFragment(v, "pdf", verEducator.getMediaId()));
//        NavController navController = Navigation.findNavController(requireActivity(), R.id.NavHostsFragmentStaff);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("post", post);
//        navController.navigate(R.id.nav_user_comment, bundle);
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
        TextView tvDescription, tvDate, tvInfo, tvInfo2, tvStatus, tvReason;
        Button btnKeep, btnDelete;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPost = itemView.findViewById(R.id.IVPost);
            tvDescription = itemView.findViewById(R.id.TVDescr);//reason
            tvDate = itemView.findViewById(R.id.TVReportDate);
            tvInfo = itemView.findViewById(R.id.TVInfo);
            tvInfo2 = itemView.findViewById(R.id.TVInfo2);
            tvStatus = itemView.findViewById(R.id.TVStatus);
            btnKeep = itemView.findViewById(R.id.BTNKeep);
            btnDelete = itemView.findViewById(R.id.BTNDelete);
//            tvReason = itemView.findViewById(R.id.TVReason);
        }
    }

    public interface OnReportedCommentActionListener {
        void onReportedDescrClicked(Helpdesk helpdesk, int position);
        void onKeepClicked(Helpdesk helpdesk, int position);
        void onDeleteClicked(Helpdesk helpdesk, int position);
    }
}
