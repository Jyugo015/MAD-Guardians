package com.example.madguardians.comment.adapter;

import static com.example.madguardians.comment.ReportFragment.newReport;
import static com.example.madguardians.comment.RootCommentFragment.newRootComment;
import static com.example.madguardians.comment.adapter.FirestoreComment.getDateTimestamp;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.madguardians.R;
import com.example.madguardians.comment.ReportFragment;
import com.example.madguardians.comment.RootCommentFragment;
import com.example.madguardians.comment.User_CommentFragment;
import com.example.madguardians.database.Comments;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class ChildCommentAdapter extends RecyclerView.Adapter<ChildCommentAdapter.CommentViewHolder>
                                 implements Listener.OnViewMorePressedListener{
    private static List<Comments> commentList = List.of();
    private FirestoreComment firestoreManager;
    private String userId;
    private int visibleItemCount = 2; // Initially show 2 items
    Listener.OnItemPressedListener listener;
    Listener.OnReportListener reportListener;
    RootCommentAdapter rootCommentAdapter;
    Listener.OnAdapterItemUpdateListener adapterUpdateListener;

    public void setParentClassInstance(RootCommentAdapter rootCommentAdapter){
        this.rootCommentAdapter = rootCommentAdapter;
    }
    // Setter for comment list
    public void setCommentList(List<Comments> comments) {
        this.commentList = comments;
        notifyDataSetChanged();
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    @Override
//    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item_user, parent, false);
        adapterUpdateListener.onAdapterItemUpdate(canShowMoreItems());
        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Log.w("Child Comment", "Synced");
        Comments comment = commentList.get(position);
        firestoreManager = new FirestoreComment();
        rootCommentAdapter.setOnViewMoreListener(ChildCommentAdapter.this);
        firestoreManager.getUser(comment.getUserId(), user -> {
            if (user!=null&&user.getProfilePic().equals("url link of default profile pic")){
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.ic_profile)
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .circleCrop()
                        .into(holder.userProfile);
            }
            else{
                Glide.with(holder.itemView.getContext())
                        .load(user.getProfilePic())
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .circleCrop()
                        .into(holder.userProfile);
            }
            holder.username.setText(user.getName());

        });
        firestoreManager.isUserEducator(comment.getUserId(), isEducator -> {
            if (isEducator) {
                holder.educatorLabel.setVisibility(View.VISIBLE);
                if (comment.getUserId().equals(comment.getAuthorId())) {
                    holder.role.setText("Author");
                }
            }
            else if (comment.getUserId().equals(comment.getAuthorId())){
                holder.educatorLabel.setVisibility(View.VISIBLE);
                holder.role.setText("Author");
            }else {
                holder.educatorLabel.setVisibility(View.GONE);
            }
        });

        holder.commentTime.setText(getDateTimestamp(comment.getTimestamp()));

        if (comment.getReplyText() == null || comment.getReplyUserId() == null) {
            holder.repliedCommentBody.setVisibility(View.GONE);
        } else {
            holder.repliedCommentBody.setVisibility(View.VISIBLE);
            holder.repliedComment.setText(comment.getReplyText());
        }

        firestoreManager.getUser(comment.getReplyUserId(), user -> {
            holder.repliedUser.setText(user.getName());
        });

        String fullText = comment.getComment();

        // Define a truncated version of the text for the initial state
        String truncatedText = fullText.length() > 100 ? fullText.substring(0, 100) + "..." : fullText;

        SpannableString spannableString;

        // If the text is long enough to show "View More", set up the clickable spans
        if (fullText.length() > 100) {
            spannableString = new SpannableString(truncatedText + " View More");

            ClickableSpan viewMoreSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // Toggle between full text and truncated text
                    if (holder.commentText.getText().toString().contains("View More")) {
                        holder.commentText.setText(fullText + "\n"+"View Less");
                    } else {
                        holder.commentText.setText(truncatedText +" View More");
                    }
                    // Reapply the clickable spans to ensure they remain active
                    applyClickableSpans(holder, fullText, truncatedText);
                }
            };

            // Set the "View More" span after the truncated text
            spannableString.setSpan(viewMoreSpan, truncatedText.length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            // For shorter texts, just display them
            spannableString = new SpannableString(fullText);
        }

        // Set the text with clickable spans
        holder.commentText.setText(spannableString);
        holder.commentText.setMovementMethod(LinkMovementMethod.getInstance());

        // Handle report icon click
        holder.iconReport.setOnClickListener(v -> {
            reportListener.onReport(comment);
        });

        holder.bind(position, listener);
    }

    private void applyClickableSpans(CommentViewHolder holder, String fullText, String truncatedText) {
        SpannableString spannableString;

        if (holder.commentText.getText().toString().contains("View Less")) {
            spannableString = new SpannableString(fullText + "\n"+ "View Less");
            ClickableSpan viewLessSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // Toggle to show the truncated text again
                    holder.commentText.setText(truncatedText+ " View More");
                    applyClickableSpans(holder, fullText, truncatedText); // Reapply spans
                }
            };
            spannableString.setSpan(viewLessSpan, fullText.length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannableString = new SpannableString(truncatedText + " View More");
            ClickableSpan viewMoreSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // Toggle to show the full text
                    holder.commentText.setText(fullText + "\n"+"View Less");
                    applyClickableSpans(holder, fullText, truncatedText); // Reapply spans
                }
            };
            spannableString.setSpan(viewMoreSpan, truncatedText.length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // Set the text again with clickable spans
        holder.commentText.setText(spannableString);
        holder.commentText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
//        return Math.min(visibleItemCount, commentList.size());
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position == getItemCount() - 1 && canShowMoreItems()) return R.layout.comment_footer_view_more; // Footer View
//        else
//            return R.layout.comment_item_user; // Regular item view
//    }

    @Override
    public void onViewMorePressed() {
        showMoreItems(5);
        Log.w("Child Comment Adapter", String.valueOf(getItemCount()));
        adapterUpdateListener.onAdapterItemUpdate(canShowMoreItems());
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView username, commentText, commentTime, repliedUser, repliedComment, role;
        public ShapeableImageView userProfile, iconReport;
        public LinearLayout repliedCommentBody, educatorLabel;;

        public CommentViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            commentText = itemView.findViewById(R.id.comment_text);
            commentTime = itemView.findViewById(R.id.comment_time);
            repliedUser = itemView.findViewById(R.id.replied_user);
            repliedComment = itemView.findViewById(R.id.replied_comment);
            userProfile = itemView.findViewById(R.id.user_profile);
            iconReport = itemView.findViewById(R.id.icon_report);
            repliedCommentBody = itemView.findViewById(R.id.replied_comment_body);
            educatorLabel = itemView.findViewById(R.id.educator_label);
            role = itemView.findViewById(R.id.role);
        }

        public void bind(int position, Listener.OnItemPressedListener listener) {
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemPressed(commentList.get(position));
                }
            });
        }
    }

    public void setOnItemPressedListener(Listener.OnItemPressedListener listener) {
        this.listener = listener;
    }

    public void setAdapterUpdateListener(Listener.OnAdapterItemUpdateListener listener) {
        this.adapterUpdateListener = listener;
    }

    // Method to increase visible items and notify adapter
    public void showMoreItems(int number) {
        if (visibleItemCount < commentList.size()) {
            visibleItemCount += number; // Load 5 more items
        }
    }

    // Method to check if more items can be loaded
    public boolean canShowMoreItems() {
        return getItemCount()!=0&&getItemCount() < commentList.size();
    }
    public void setOnReportListener(Listener.OnReportListener listener){this.reportListener = listener;}

    public int findItemPosition(Comments comment){
        for (int i = 0; i < commentList.size(); i++) {
            if (commentList.get(i).getCommentId().equals(comment.getCommentId())) {
                return i; // Return the position of the item
            }
        }
        return -1;
    }
}
