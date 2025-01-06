package com.example.madguardians.comment.adapter;

import static com.example.madguardians.comment.adapter.FirestoreComment.getDateTimestamp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.madguardians.R;
import com.example.madguardians.database.Comments;
import com.example.madguardians.firebase.PostFB;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EduCommentAdapter extends RecyclerView.Adapter<EduCommentAdapter.CommentViewHolder> {
    private List<Comments> commentList = List.of();
//    private OnItemClickListener onItemClickListener;
    private FirestoreComment firestoreManager;
    private String userId;
    private PostFB post;
    private Activity parentActivity;
    private Context context;

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

        holder.itemView.setVisibility(View.GONE);

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

        AtomicInteger loadingCounter = new AtomicInteger(3); // Adjust based on the number of async tasks (e.g., images to load)

        // Fetch post title
        firestoreManager.getPost(comment.getPostId(), post -> {
            this.post = post;
            if (post != null) {
                holder.postTitle.setText(post.getTitle());
            } else {
                holder.postTitle.setText("Unknown Post");
            }

            if (loadingCounter.decrementAndGet() == 0) {
                holder.itemView.setVisibility(View.VISIBLE); // Show item when all tasks complete
            }
        });

        int sizeInPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                20,
                context.getResources().getDisplayMetrics()
        );

        // Fetch course details
        firestoreManager.getCourse(comment.getPostId(), course -> {
            if (course != null) {
                holder.courseTitle.setText(course.getTitle());
                Glide.with(holder.itemView.getContext())
                        .load(course.getCoverImage())
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(sizeInPixels, sizeInPixels)
                        .into(holder.courseCover);
            } else {
                holder.courseTitle.setText("Unknown Course");
                Glide.with(holder.itemView.getContext())
                        .load("https://res.cloudinary.com/dmgpozfee/image/upload/v1732898099/vfp2hoinnc2udodmftyv.jpg")
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(sizeInPixels, sizeInPixels)
                        .into(holder.courseCover);
            }

            if (loadingCounter.decrementAndGet() == 0) {
                holder.itemView.setVisibility(View.VISIBLE); // Show item when all tasks complete
            }
        });

        int sizeInPixel = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                45,
                context.getResources().getDisplayMetrics()
        );

        // Fetch user profile picture
        firestoreManager.getUser(comment.getUserId(), user -> {
            if (user.getProfilePic().equals("url link of default profile pic")){
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.ic_profile)
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(sizeInPixel, sizeInPixel)
                        .into(holder.userProfile);
            }
            else {
                Glide.with(holder.itemView.getContext())
                        .load(user.getProfilePic())
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(sizeInPixel, sizeInPixel)
                        .into(holder.userProfile);
            }
            holder.username.setText(user.getName());
            if (loadingCounter.decrementAndGet() == 0) {
                holder.itemView.setVisibility(View.VISIBLE); // Show item when all tasks complete
            }
        });

//         Set the onClickListener for the item view
        holder.itemView.setOnClickListener(v -> {
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

    public void setContext(Context context){this.context = context;}

//    public void performActionOnVisibleItem(int position) {
//        if (position >= 0 && position < commentList.size()) {
//            Comments comment = commentList.get(position);
//            if (!comment.isAuthorRead()) {
//                if (comment.getAuthorId().equals(userId)) comment.setAuthorRead(true);
////                notifyItemChanged(position); // Refresh the specific item
//            }
//            if (!comment.isRepliedUserRead()) {
//                if (comment.getReplyUserId().equals(userId)) comment.setRepliedUserRead(true);
//            }
//            firestoreManager.updateReadStatus(comment);
//            notifyDataSetChanged();
//        }
//    }

}

