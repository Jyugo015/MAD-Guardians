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

import com.bumptech.glide.Glide;
import com.example.madguardians.R;
import com.example.madguardians.database.Media;
import com.example.madguardians.database.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewVerPostAdapter extends RecyclerView.Adapter<RecycleViewVerPostAdapter.PostViewHolder> {
    private List<VerPost> verPostList;
    private Context context;
    private OnPostActionListener onPostActionListener;

    private final FirebaseFirestore firestore;
    private final CollectionReference postRef;
    private final CollectionReference userRef;

    // Constructor
    public RecycleViewVerPostAdapter(List<VerPost> verPostList, Context context, OnPostActionListener onPostActionListener) {
        this.verPostList = verPostList != null ? verPostList : new ArrayList<>();
        this.context = context;
        this.onPostActionListener = onPostActionListener;

        this.firestore = FirebaseFirestore.getInstance();
        this.postRef = firestore.collection("post");
        this.userRef = firestore.collection("user");
    }

    public void updateData(List<VerPost> newData) {
        this.verPostList = newData; // Assuming postList is the list in your adapter
        notifyDataSetChanged(); // Notify the adapter about data changes
    }

    @Override
    public int getItemViewType(int position) {
        VerPost verPost = verPostList.get(position);
        if ("pending".equals(verPost.getVerifiedStatus())) {
            return R.layout.staff_one_line_handle_post_pending_hzw;
        } else {
            return R.layout.staff_one_line_handle_post_completed_hzw;
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
        VerPost verPost = verPostList.get(position);
//        System.out.println(verPost.getPostId());
//        System.out.println(verPost.getVerPostId());
        // Reset ViewHolder to avoid stale data
        holder.tvCourseTitle.setText("Loading...");
        holder.tvDate.setText("");
        holder.tvAuthorName.setText("Loading...");
        holder.ivPost.setImageResource(R.drawable.placeholder_image);
        holder.tvStatus.setText(verPost.getVerifiedStatus());

        // Fetch Post details using postId
        postRef.document(verPost.getPostId()).get().addOnSuccessListener(postSnapshot -> {
            if (postSnapshot.exists()) {
                Post post = postSnapshot.toObject(Post.class);

                if (post != null) {
                    // Set Post details
                    holder.tvCourseTitle.setText(post.getTitle() != null ? post.getTitle() : "No Title");
                    holder.tvDate.setText(post.getDate() != null ? post.getDate() : "No Date");

                    // Fetch Media details using mediaSetId
                    if (post.getImageSetId() != null) {
                        firestore.collection("media")
                                .whereEqualTo("mediaSetId", post.getImageSetId())
                                .get()
                                .addOnSuccessListener(mediaSnapshot -> {
                                    if (!mediaSnapshot.isEmpty()) {
                                        DocumentSnapshot mediaDoc = mediaSnapshot.getDocuments().get(0);
                                        Media media = mediaDoc.toObject(Media.class);
                                        if (media != null && media.getUrl() != null) {
                                            Glide.with(context)
                                                    .load(media.getUrl())
                                                    .into(holder.ivPost);
                                        } else {
                                            holder.ivPost.setImageResource(R.drawable.placeholder_image); // Default image
                                        }
                                    } else {
                                        holder.ivPost.setImageResource(R.drawable.placeholder_image); // Default image
                                    }
                                })
                                .addOnFailureListener(e -> holder.ivPost.setImageResource(R.drawable.error_image)); // Error placeholder
                    } else {
                        holder.ivPost.setImageResource(R.drawable.placeholder_image); // Default image
                    }

                    // Fetch User details using userId
                    userRef.document(post.getUserId()).get().addOnSuccessListener(userSnapshot -> {
                        if (userSnapshot.exists()) {
                            User user = userSnapshot.toObject(User.class);
                            if (user != null) {
                                holder.tvAuthorName.setText(user.getName() != null ? user.getName() : "Unknown Author");
                            } else {
                                holder.tvAuthorName.setText("Unknown Author");
                            }
                        } else {
                            holder.tvAuthorName.setText("Unknown Author");
                        }
                    }).addOnFailureListener(e -> holder.tvAuthorName.setText("Error fetching author"));
                }
            } else {
                holder.tvCourseTitle.setText("Post Not Found");
            }
        }).addOnFailureListener(e -> holder.tvCourseTitle.setText("Error fetching post"));

        // Set VerPost status
        holder.tvStatus.setText(verPost.getVerifiedStatus());

        // Handle actions based on verifiedStatus
        String verifiedStatus = verPost.getVerifiedStatus();
        if ("pending".equals(verifiedStatus)) {

            holder.btnReject.setOnClickListener(v -> {
                if (onPostActionListener != null) {
                    onPostActionListener.onRejectClicked(verPost, position);
                    firestore.collection("verPost")
                            .document(verPost.getVerPostId())
                            .update("verifiedStatus", "rejected");
                    notifyItemChanged(position);
                }
            });

            holder.btnApprove.setOnClickListener(v -> {
                if (onPostActionListener != null) {
                    onPostActionListener.onApprovedClicked(verPost, position);
                    firestore.collection("verPost")
                            .document(verPost.getVerPostId())
                            .update("verifiedStatus", "approved");
                    notifyItemChanged(position);
                }
            });
        } else {

            holder.btnDelete.setOnClickListener(v -> {
                if (onPostActionListener != null) {
                    onPostActionListener.onDeleteClicked(verPost, position);
                }
            });
        }

        // Click listener for course title
        holder.tvCourseTitle.setOnClickListener(v -> {
            if (onPostActionListener != null) {
                onPostActionListener.onCourseTitleClicked(verPost, position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return verPostList != null ? verPostList.size() : 0;
    }

    public void updateList(List<VerPost> newPostList) {
        verPostList = newPostList != null ? newPostList : new ArrayList<>();
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
        void onRejectClicked(VerPost post, int position);
        void onApprovedClicked(VerPost post, int position);
        void onDeleteClicked(VerPost post, int position);
        void onCourseTitleClicked(VerPost post, int position);
    }
}