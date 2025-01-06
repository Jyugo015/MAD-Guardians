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

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.madguardians.R;
import com.example.madguardians.database.Media;
import com.example.madguardians.firebase.MediaFB;
import com.example.madguardians.ui.staff.Helpdesk;
import com.example.madguardians.database.Issue;
import com.example.madguardians.database.IssueDao;
import com.example.madguardians.database.Post;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.madguardians.firebase.PostFB;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewReportedPostAdapter extends RecyclerView.Adapter<RecycleViewReportedPostAdapter.PostViewHolder> {
    private List<Helpdesk> helpdeskList;
    private Context context;
    private OnReportedPostActionListener onReportedPostActionListener;
    private com.example.madguardians.ui.staff.Helpdesk helpdesk;
    private final FirebaseFirestore firestore;
    private final CollectionReference postRef,userRef,issueRef,courseRef;

    // Constructor
    public RecycleViewReportedPostAdapter(List<Helpdesk> helpdeskList, Context context,OnReportedPostActionListener onReportedPostActionListener) {
        this.helpdeskList = helpdeskList!=null?helpdeskList:new ArrayList<>();
        this.context = context;
        this.onReportedPostActionListener = onReportedPostActionListener;

        this.firestore = FirebaseFirestore.getInstance();
        this.postRef = firestore.collection("post");
        this.userRef = firestore.collection("user");
        this.issueRef = firestore.collection("issue");
        this.courseRef = firestore.collection("course");
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

    public String getTableName(String reportedItemId) {
        if (reportedItemId == null || reportedItemId.isEmpty()) {
            throw new IllegalArgumentException("Reported item ID cannot be null or empty.");
        }

        // Check for media types first
        if (reportedItemId.startsWith("IMG") ||
                reportedItemId.startsWith("PDF") ||
                reportedItemId.startsWith("VID")) {
            return "media";
        }

        // Check for post
        if (reportedItemId.startsWith("P") && !reportedItemId.startsWith("PDF")) {
            return "post";
        }

        throw new IllegalArgumentException("Invalid reported item ID format.");
    }


    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Helpdesk helpdesk = helpdeskList.get(position);

        // Reset ViewHolder to avoid stale data
        holder.tvCourseTitle.setText("Loading...");
        holder.tvAuthorName.setText("Loading...");
        holder.tvReason.setText("Loading...");
        holder.tvStatus.setText(helpdesk.getHelpdeskStatus());
        holder.ivPost.setImageResource(R.drawable.placeholder_image); // Reset image

        // Fetch Issue details using issueId
        if (helpdesk.getIssueId() != null) {
            issueRef.document(helpdesk.getIssueId()).get()
                    .addOnSuccessListener(issueSnapshot -> {
                        if (issueSnapshot.exists()) {
                            Issue issue = issueSnapshot.toObject(Issue.class);
                            holder.tvReason.setText(issue != null && issue.getType() != null ? issue.getType() : "No Reason");
                        } else {
                            holder.tvReason.setText("Issue Not Found");
                        }
                    })
                    .addOnFailureListener(e -> holder.tvReason.setText("Error fetching issue"));
        }

        // Fetch details based on reportedItemId
        if (helpdesk.getReportedItemId() != null) {
            String tableName = getTableName(helpdesk.getReportedItemId());
            if ("post".equals(tableName)) {
                // Fetch Post details
                postRef.document(helpdesk.getReportedItemId()).get()
                        .addOnSuccessListener(postSnapshot -> {
                            if (postSnapshot.exists()) {
                                Post post = postSnapshot.toObject(Post.class);
                                if (post != null) {
                                    // Fetch User details using userId
                                    if (post.getUserId() != null) {
                                        userRef.document(post.getUserId()).get()
                                                .addOnSuccessListener(userSnapshot -> {
                                                    if (userSnapshot.exists()) {
                                                        String userName = userSnapshot.getString("name");
                                                        holder.tvAuthorName.setText(userName != null ? userName : "Unknown Author");
                                                    } else {
                                                        holder.tvAuthorName.setText("User Not Found");
                                                    }
                                                })
                                                .addOnFailureListener(e -> holder.tvAuthorName.setText("Error fetching user"));
                                    } else {
                                        holder.tvAuthorName.setText("User Not Associated");
                                    }
                                    // link to post
                                    System.out.println("PostID:"+post.getPostId());
                                    holder.tvCourseTitle.setOnClickListener(v -> navigateToFragment(v, "post", post.getPostId()));

                                    // Set Image if available
                                    if (post.getImageSetId() != null) {
                                        firestore.collection("media")
                                                .whereEqualTo("mediaSetId", post.getImageSetId())
                                                .get()
                                                .addOnSuccessListener(mediaSnapshot -> {
                                                    if (!mediaSnapshot.isEmpty()) {
                                                        Media media = mediaSnapshot.getDocuments().get(0).toObject(Media.class);
                                                        Glide.with(context)
                                                                .load(media != null ? media.getUrl() : null)
                                                                .placeholder(R.drawable.placeholder_image)
                                                                .error(R.drawable.error_image)
                                                                .into(holder.ivPost);
                                                    } else {
                                                        holder.ivPost.setImageResource(R.drawable.placeholder_image);
                                                    }
                                                })
                                                .addOnFailureListener(e -> holder.ivPost.setImageResource(R.drawable.error_image));
                                    }
                                    // Set click listener to navigate to the fragment with the Post object
                                    holder.tvCourseTitle.setOnClickListener(v -> {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("postId", (String) post.getPostId());
                                        Navigation.findNavController(v).navigate(R.id.nav_post, bundle);
                                    });
                                }
                            } else {
                                holder.tvCourseTitle.setText("Post Not Found");
                            }
                        })
                        .addOnFailureListener(e -> holder.tvCourseTitle.setText("Error fetching post"));
            } else if ("media".equals(tableName)) {
                // Handle Media Types
                if (helpdesk.getReportedItemId().startsWith("IMG")) {
                    holder.tvCourseTitle.setText("Reported Image");
                    holder.tvAuthorName.setText("Please Check");
                    holder.ivPost.setImageResource(R.drawable.ic_image);
                    // Set click event for Image
                    holder.ivPost.setOnClickListener(v -> navigateToFragment(v, "image", helpdesk.getReportedItemId()));
                } else if (helpdesk.getReportedItemId().startsWith("PDF")) {
                    holder.tvCourseTitle.setText("Reported Document");
                    holder.tvAuthorName.setText("Please Check");
                    holder.ivPost.setImageResource(R.drawable.ic_pdf);
                    // Set click event for PDF
                    holder.ivPost.setOnClickListener(v -> navigateToFragment(v, "pdf", helpdesk.getReportedItemId()));
                } else if (helpdesk.getReportedItemId().startsWith("VID")) {
                    holder.tvCourseTitle.setText("Reported Video");
                    holder.tvAuthorName.setText("Please Check");
                    holder.ivPost.setImageResource(R.drawable.ic_video);
                    // Set click event for Video
                    holder.ivPost.setOnClickListener(v -> navigateToFragment(v, "video", helpdesk.getReportedItemId()));
                }
            }
        }

        // Set Helpdesk status
        holder.tvStatus.setText(helpdesk.getHelpdeskStatus());

        // Handle actions based on Helpdesk status
        if ("pending".equals(helpdesk.getHelpdeskStatus())) {
            holder.btnKeep.setOnClickListener(v -> {
                if (onReportedPostActionListener != null) {
                    onReportedPostActionListener.onKeepClicked(helpdesk, position);
                    firestore.collection("helpdesk")
                            .document(helpdesk.getHelpdeskId())
                            .update("helpdeskStatus", "reviewed");
                    notifyItemChanged(position);
                }
            });

            holder.btnDelete.setOnClickListener(v -> {
                if (onReportedPostActionListener != null) {
                    onReportedPostActionListener.onDeleteClicked(helpdesk, position);
                    firestore.collection("helpdesk")
                            .document(helpdesk.getHelpdeskId())
                            .delete();
                    helpdeskList.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }
//        holder.tvCourseTitle.setOnClickListener(v -> {
//            if (onReportedPostActionListener != null) {
//                onReportedPostActionListener.onPostTitleClicked(helpdesk, position);
//            }
//        });
    }

    //link to post option1
//    private void navigateToFragment(View view, String type, String mediaId) {
//        Bundle bundle = new Bundle();
//        bundle.putString("mediaId", mediaId);
//
//        if ("image".equalsIgnoreCase(type)) {
//            Navigation.findNavController(view).navigate(R.id.nav_img, bundle);
//        } else if ("video".equalsIgnoreCase(type)) {
//            Navigation.findNavController(view).navigate(R.id.nav_vid, bundle);
//        } else if ("pdf".equalsIgnoreCase(type)) {
//            Navigation.findNavController(view).navigate(R.id.nav_pdf, bundle);
//        } else if("post".equalsIgnoreCase(type)){
//            Navigation.findNavController(view).navigate(R.id.nav_post, bundle);
//            System.out.println();
//        }
//    }

    //link to post, option2, still error
private void navigateToFragment(View view, String type, Object media) {
    Bundle bundle = new Bundle();

    Log.d("TAG", "navigateToFragment: id: " + (String) media);
    if ("image".equalsIgnoreCase(type) || "video".equalsIgnoreCase(type) || "pdf".equalsIgnoreCase(type)) {
        // media is expected to be a String mediaId
        if (media instanceof String) {
            bundle.putString("mediaId", (String) media);
            int destinationId = getDestinationIdForType(type);
            Navigation.findNavController(view).navigate(destinationId, bundle);
        } else {
            Log.e("navigateToFragment", "Expected String for mediaId, got: " + media.getClass().getName());
        }
    } else if ("post".equalsIgnoreCase(type)) {
        // media is expected to be a PostFB object
        if (media instanceof PostFB) {
            bundle.putString("postId", (String) media);
            Navigation.findNavController(view).navigate(R.id.nav_post, bundle);
        } else {
            Log.e("navigateToFragment", "Expected PostFB for post, got: " + media.getClass().getName());
        }
    }
    else {
        Log.e("navigateToFragment", "Invalid type provided: " + type);
    }
}
    private int getDestinationIdForType(String type) {
        switch (type.toLowerCase()) {
            case "image":
                return R.id.nav_img;
            case "video":
                return R.id.nav_vid;
            case "pdf":
                return R.id.nav_pdf;
            default:
                throw new IllegalArgumentException("Unsupported type: " + type);
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
        void onKeepClicked(Helpdesk helpdesk, int position);
        void onDeleteClicked(Helpdesk helpdesk, int position);
        void onPostTitleClicked(Helpdesk helpdesk,int position);
    }
}
