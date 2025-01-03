package com.example.madguardians.comment.adapter;

import static com.example.madguardians.comment.adapter.FirestoreComment.getDateTimestamp;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.madguardians.R;
import com.example.madguardians.database.Comments;
import com.example.madguardians.firebase.PostFB;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class EduCommentAdapter extends RecyclerView.Adapter<EduCommentAdapter.CommentViewHolder> {
    private List<Comments> commentList = List.of();
//    private OnItemClickListener onItemClickListener;
    private FirestoreComment firestoreManager;
    private String userId;
    private PostFB post;
    private Activity parentActivity;

//    // Define an interface for the item click listener
//    public interface OnItemClickListener {
//        void onItemClick(int position, String commentId);
//    }
//
//    public EduCommentAdapter(OnItemClickListener listener) {
//        this.onItemClickListener = listener;
//    }

    public void setCommentList(List<Comments> comments) {
        this.commentList = comments;
        notifyDataSetChanged(); // Notify the adapter about data changes
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_edu, parent, false);
        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comments comment = commentList.get(position);

        firestoreManager = new FirestoreComment();

        // Bind data to the views
        holder.commentText.setText(comment.getComment());
        holder.commentTime.setText(getDateTimestamp(comment.getTimestamp()));

        if (comment.getReplyText() == null||comment.getReplyUserId()==null) {
            holder.repliedCommentBody.setVisibility(View.GONE);
        }
        else {
            holder.repliedCommentBody.setVisibility(View.VISIBLE);
            holder.repliedUser.setText(comment.getReplyUserId());
            holder.repliedComment.setText(comment.getReplyText());
        }

        if(comment.getReplyUserId()!= null&&comment.getReplyUserId().equals(userId)){
            holder.commentReply.setText("replies to your comment");
        }
        else holder.commentReply.setText("comments on your course");

        // Fetch post title
        firestoreManager.getPost(comment.getPostId(), post -> {
            this.post = post;
            if (post != null) {
                holder.postTitle.setText(post.getTitle());
            } else {
                holder.postTitle.setText("Unknown Post");
            }
        });

        // Fetch course details
        firestoreManager.getCourse(comment.getPostId(), course -> {
            if (course != null) {
                holder.courseTitle.setText(course.getTitle());
                Glide.with(holder.itemView.getContext())
                        .load(course.getCoverImage())
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .circleCrop()
                        .into(holder.courseCover);
            } else {
                holder.courseTitle.setText("Unknown Course");
                Glide.with(holder.itemView.getContext())
                        .load("https://res.cloudinary.com/dmgpozfee/image/upload/v1732898099/vfp2hoinnc2udodmftyv.jpg")
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .circleCrop()
                        .into(holder.courseCover);
            }
        });

        // Fetch user profile picture
        firestoreManager.getUser(comment.getUserId(), user -> {
            if (user.getProfilePic().equals("url link of default profile pic")){
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.ic_profile)
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .circleCrop()
                        .into(holder.userProfile);
            }
            else {
                Glide.with(holder.itemView.getContext())
                        .load(user.getProfilePic())
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .circleCrop()
                        .into(holder.userProfile);
            }
            holder.username.setText(user.getName());
        });

        // Set the onClickListener for the item view
        holder.itemView.setOnClickListener(v -> {
            firestoreManager.updateReadStatus(post);
            NavController navController = Navigation.findNavController(parentActivity, R.id.NavHostFragment);
            Bundle bundle = new Bundle();
            bundle.putSerializable("post", post);
            bundle.putSerializable("comment", comment);
            navController.navigate(R.id.nav_edu_user_comment, bundle);
        });


    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView username, commentText, commentTime, commentReply, repliedUser,
                repliedComment, courseTitle, postTitle;
        public ShapeableImageView userProfile, courseCover;
        public LinearLayout repliedCommentBody;

        public CommentViewHolder(View itemView) {
            super(itemView);
            // Initialize views
            username = itemView.findViewById(R.id.username);
            commentText = itemView.findViewById(R.id.comment_text);
            commentTime = itemView.findViewById(R.id.comment_time);
            commentReply = itemView.findViewById(R.id.comment_reply);
            repliedUser = itemView.findViewById(R.id.replied_user);
            repliedComment = itemView.findViewById(R.id.replied_comment);
            courseTitle = itemView.findViewById(R.id.course_title);
            postTitle = itemView.findViewById(R.id.post_title);
            userProfile = itemView.findViewById(R.id.user_profile);
            courseCover = itemView.findViewById(R.id.course_cover);
            repliedCommentBody = itemView.findViewById(R.id.replied_comment_body);
        }
    }

    public void setParentActivity(Activity parentActivity) {
        this.parentActivity = parentActivity;
    }
}

