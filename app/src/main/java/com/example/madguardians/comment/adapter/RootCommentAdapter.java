package com.example.madguardians.comment.adapter;

import static android.content.Context.MODE_PRIVATE;
import static com.example.madguardians.comment.ReportFragment.newReport;
import static com.example.madguardians.comment.adapter.FirestoreComment.getDateTimestamp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.madguardians.R;
import com.example.madguardians.comment.ReportFragment;
import com.example.madguardians.database.Comments;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RootCommentAdapter extends RecyclerView.Adapter<RootCommentAdapter.CommentViewHolder>
                                implements Listener.OnAdapterItemUpdateListener, Listener.ScrollListener{
    private static List<Comments> commentList = List.of();
    private FirestoreComment firestoreManager;
    private ChildCommentAdapter adapter;
    SharedPreferences sharedPreferences;
    private String userId;
    private CommentViewModel viewModel;
    LiveData<List<Comments>> commentLiveData;
    Context context;
    Fragment parentFragment;
    LifecycleOwner lifecycleOwner;
    Listener.OnItemPressedListener listener;

    Listener.OnViewMorePressedListener viewMoreListener;
    Listener.OnReportListener reportListener;
    LinearLayout viewMore;
    Comments findMatchedComment;
    boolean findMatched = false;


    public void setCommentList(List<Comments> comments) {
        this.commentList = comments;
        notifyDataSetChanged();
    }


    public RootCommentAdapter(Context context, Fragment parentFragment, LifecycleOwner lifecycleOwner){
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;
        this.parentFragment = parentFragment;
    }


    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_user_root, parent, false);
        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
//        if ()
//        commentLiveData.removeObservers(lifecycleOwner);

        Log.w("Root Comment", "Synced");
        Comments comment = commentList.get(position);
        holder.itemView.setVisibility(View.GONE);


        this.viewMore = holder.viewMore;
        holder.viewMore.setVisibility(View.GONE);

        firestoreManager = new FirestoreComment();
        holder.commentTime.setText(getDateTimestamp(comment.getTimestamp()));

        int sizeInPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                45,
                context.getResources().getDisplayMetrics()
        );
        AtomicInteger loadingCounter = new AtomicInteger(2); // Adjust based on the number of async tasks (e.g., images to load)


        // Fetch user profile and set username
        firestoreManager.getUser(comment.getUserId(), user -> {
            if (user!=null&&user.getProfilePic().equals("url link of default profile pic")){
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.ic_profile)
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(sizeInPixels, sizeInPixels)
                        .into(holder.userProfile);
            }
            else{
                Glide.with(holder.itemView.getContext())
                        .load(user.getProfilePic())
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(sizeInPixels, sizeInPixels)
                        .into(holder.userProfile);
            }
            holder.username.setText(user.getName());
            if (loadingCounter.decrementAndGet() == 0) {
                holder.itemView.setVisibility(View.VISIBLE); // Show item when all tasks complete
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        holder.commentRecyclerView.setLayoutManager(layoutManager);

        adapter = new ChildCommentAdapter(context);
        adapter.setParentClassInstance(this);

        viewModel = new ViewModelProvider(parentFragment).get(CommentViewModel.class);
            sharedPreferences = context.getSharedPreferences("user_preferences", MODE_PRIVATE);
            userId = sharedPreferences.getString("user_id", null);
        adapter.setUserId(userId);
        adapter.setOnItemPressedListener(listener);
        adapter.setOnReportListener(reportListener);
        adapter.setAdapterUpdateListener(RootCommentAdapter.this);
        adapter.setHasStableIds(true);
        commentLiveData = viewModel.getChildComment(comment.getCommentId());

        commentLiveData.observe(lifecycleOwner, comments -> {
            // Update adapter when data changes
            if (comments != null && !comments.isEmpty()) {
                holder.commentRecyclerView.setVisibility(View.VISIBLE);
                adapter.setCommentList(comments);
                if(findMatched) {
                    holder.commentRecyclerView.scrollToPosition(adapter.findItemPosition(findMatchedComment));
                    findMatched=false;
                }
                else holder.commentRecyclerView.smoothScrollToPosition(0);
            }
            else {
                holder.commentRecyclerView.setVisibility(View.GONE);
            }
        });

        holder.commentRecyclerView.setAdapter(adapter);

        Handler scrollHandler = new Handler();
        final Runnable[] scrollRunnable = {null};

        holder.commentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (scrollRunnable[0] != null) {
                    scrollHandler.removeCallbacks(scrollRunnable[0]);
                }

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                scrollRunnable[0] = () -> {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int firstVisible = layoutManager.findFirstVisibleItemPosition();
                        int lastVisible = layoutManager.findLastVisibleItemPosition();

                        if (firstVisible >= 0 && lastVisible >= 0) {
                            List<WriteBatch> batches = new ArrayList<>();
                            WriteBatch currentBatch = firestore.batch();
                            int batchOperationCount = 0;

                            for (int i = firstVisible; i <= lastVisible; i++) {
                                if (i < adapter.getCommentList().size()) {
                                    Comments comment = adapter.getItemAtPosition(i);
                                    DocumentReference docRef = firestore.collection("comment").document(comment.getCommentId());
                                    Map<String, Object> updates = new HashMap<>();

                                    if (!comment.isAuthorRead() && comment.getAuthorId().equals(userId)) {
                                        updates.put("authorRead", true);
                                    }
                                    if (!comment.isRepliedUserRead() && comment.getReplyUserId()!=null &&comment.getReplyUserId().equals(userId)) {
                                        updates.put("repliedUserRead", true);
                                    }

                                    if (!updates.isEmpty()) { // Only add updates if there are changes
                                        currentBatch.update(docRef, updates);
                                        batchOperationCount++;

                                        // If batch reaches 500 operations, store it and create a new batch
                                        if (batchOperationCount == 500) {
                                            batches.add(currentBatch);
                                            currentBatch = firestore.batch();
                                            batchOperationCount = 0;
                                        }
                                    }
                                }
                            }

                            // Add any remaining operations in the last batch
                            if (batchOperationCount > 0) {
                                batches.add(currentBatch);
                            }

                            // Commit each batch sequentially
                            for (WriteBatch batch : batches) {
                                batch.commit().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.d("Firestore", "Batch update successful");
                                    } else {
                                        Log.e("Firestore", "Batch update failed", task.getException());
                                    }
                                });
                            }
                        }
                    }
                };

                scrollHandler.postDelayed(scrollRunnable[0], 200); // Adjust debounce delay
            }
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
            if (loadingCounter.decrementAndGet() == 0) {
                holder.itemView.setVisibility(View.VISIBLE); // Show item when all tasks complete
            }
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
                        holder.commentText.setText(truncatedText + " View More");
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

        // Report icon click listener
        holder.iconReport.setOnClickListener(v -> {
            reportListener.onReport(comment);
        });

//        holder.viewMore.setOnClickListener(v->{
//            viewMoreListener.onViewMorePressed();
//            Log.w("view more", comment.getCommentId());
//        });

        holder.rootCommentBody.setOnClickListener(v -> {
            listener.onItemPressed(comment);
        });
    }

    private void applyClickableSpans(RootCommentAdapter.CommentViewHolder holder, String fullText, String truncatedText) {
        SpannableString spannableString;

        if (holder.commentText.getText().toString().contains("View Less")) {
            spannableString = new SpannableString(fullText + "\n"+"View Less");
            ClickableSpan viewLessSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // Toggle to show the truncated text again
                    holder.commentText.setText(truncatedText + " View More");
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
    }

    @Override
    public void onAdapterItemUpdate(boolean showViewMore) {
//        if (showViewMore){
//            viewMore.setVisibility(View.VISIBLE);
//            Log.w("viewMore visibility", "Visible");
//        }
//        else{
//            viewMore.setVisibility(View.GONE);
//            Log.w("viewMore visibility", "Gone");
//        }
    }

    @Override
    public void onScrollChild(Comments comment) {
        for (int i = 0; i < commentList.size(); i++) {
            Comments rootComment = commentList.get(i);

            if (rootComment.getCommentId().equals(comment.getRootComment())) {
                int position = i;

                RecyclerView recyclerView = parentFragment.getView().findViewById(R.id.recyclerViewComments); // Adjust the ID

                CommentViewHolder holder = (CommentViewHolder) recyclerView.findViewHolderForAdapterPosition(position);

                if (holder != null) {
                    RecyclerView childRecyclerView = holder.commentRecyclerView;
                    LinearLayoutManager layoutManager = (LinearLayoutManager) childRecyclerView.getLayoutManager();

                    if (layoutManager != null) {
                        int position1 = adapter.findItemPosition(comment);
                        if (position1 >= 0 && position1 < adapter.getItemCount()) {
                            // Scroll to the matched comment position within the child RecyclerView
                            layoutManager.scrollToPositionWithOffset(position, 0);
                        }
                    }
                }
                break;
            }
        }
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView username, commentText, commentTime, role;
        public ShapeableImageView userProfile, iconReport;
        public LinearLayout educatorLabel, rootCommentBody, viewMore;
        public RecyclerView commentRecyclerView;

        public CommentViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            commentText = itemView.findViewById(R.id.comment_text);
            commentTime = itemView.findViewById(R.id.comment_time);
            userProfile = itemView.findViewById(R.id.user_profile);
            iconReport = itemView.findViewById(R.id.icon_report);
            educatorLabel = itemView.findViewById(R.id.educator_label);
            commentRecyclerView = itemView.findViewById(R.id.commentRecyclerView);
            role = itemView.findViewById(R.id.role);
            rootCommentBody = itemView.findViewById(R.id.root_comment_body);
            viewMore = itemView.findViewById(R.id.view_more);
        }
    }

    public void setOnItemPressedListener(Listener.OnItemPressedListener listener) {
        this.listener = listener;
    }

    public void setOnViewMoreListener(Listener.OnViewMorePressedListener listener) {
        this.viewMoreListener = listener;
    }

    public void setOnReportListener(Listener.OnReportListener listener){this.reportListener = listener;}

    public int findItemPosition(String commentId){
        for (int i = 0; i < commentList.size(); i++) {
            if (commentList.get(i).getCommentId().equals(commentId)) {
                Log.w("item postiion", String.valueOf(i));
                return i; // Return the position of the item
            }
        }
        return -1;
    }

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

    public Comments getItemAtPosition(int i){
        return commentList.get(i);
    }

    public List<Comments> getCommentList(){
        return commentList;
    }

    @Override
    public long getItemId(int position) {
        return commentList.get(position).getCommentId().hashCode();
    }

}

