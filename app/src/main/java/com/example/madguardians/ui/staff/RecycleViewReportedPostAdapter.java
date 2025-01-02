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
import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Course;
import com.example.madguardians.database.CourseDao;
import com.example.madguardians.database.Media;
import com.example.madguardians.ui.staff.Helpdesk;
import com.example.madguardians.database.Issue;
import com.example.madguardians.database.IssueDao;
import com.example.madguardians.database.Post;
import com.example.madguardians.database.PostDao;
import com.example.madguardians.database.User;
import com.example.madguardians.database.UserDao;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Helpdesk helpdesk = helpdeskList.get(position);

        // Reset ViewHolder to avoid stale data
        holder.tvCourseTitle.setText("Loading...");
        holder.tvAuthorName.setText("Loading...");
        holder.tvReason.setText("");
        holder.tvStatus.setText(helpdesk.getHelpdeskStatus());

        // Fetch Issue details using issueId
        if (helpdesk.getIssueId() != null) {
            issueRef.document(helpdesk.getIssueId()).get()
                    .addOnSuccessListener(issueSnapshot -> {
                        if (issueSnapshot.exists()) {
                            Issue issue = issueSnapshot.toObject(Issue.class);
                            if (issue != null) {
                                holder.tvReason.setText(issue.getType() != null ? issue.getType() : "No Reason");
                            } else {
                                holder.tvReason.setText("No Reason");
                            }
                        } else {
                            holder.tvReason.setText("Issue Not Found");
                        }
                    })
                    .addOnFailureListener(e -> holder.tvReason.setText("Error fetching issue"));
        }

        // Fetch related details (Post, Course, or Quiz) based on available IDs
        if (helpdesk.getPostId() != null) {
            postRef.document(helpdesk.getPostId()).get()
                    .addOnSuccessListener(postSnapshot -> {
                        if (postSnapshot.exists()) {
                            Post post = postSnapshot.toObject(Post.class);
                            if (post != null) {
                                // Set Title
                                holder.tvCourseTitle.setText(post.getTitle() != null ? post.getTitle() : "No Title");

                                // Set Image if available
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
                                                                .placeholder(R.drawable.placeholder_image) // Placeholder while loading
                                                                .error(R.drawable.error_image)            // Fallback in case of error
                                                                .into(holder.ivPost);
                                                    } else {
                                                        holder.ivPost.setImageResource(R.drawable.placeholder_image);
                                                    }
                                                } else {
                                                    holder.ivPost.setImageResource(R.drawable.placeholder_image);
                                                }
                                            })
                                            .addOnFailureListener(e -> holder.ivPost.setImageResource(R.drawable.error_image));
                                } else {
                                    holder.ivPost.setImageResource(R.drawable.placeholder_image);
                                }

                                // Fetch User details for post author
                                userRef.document(post.getUserId()).get()
                                        .addOnSuccessListener(userSnapshot -> {
                                            if (userSnapshot.exists()) {
                                                User author = userSnapshot.toObject(User.class);
                                                holder.tvAuthorName.setText(author != null && author.getName() != null
                                                        ? author.getName()
                                                        : "Unknown Author");
                                            } else {
                                                holder.tvAuthorName.setText("Unknown Author");
                                            }
                                        })
                                        .addOnFailureListener(e -> holder.tvAuthorName.setText("Error fetching author"));
                            }
                        } else {
                            holder.tvCourseTitle.setText("Post Not Found");
                            holder.ivPost.setImageResource(R.drawable.placeholder_image);
                        }
                    })
                    .addOnFailureListener(e -> {
                        holder.tvCourseTitle.setText("Error fetching post");
                        holder.ivPost.setImageResource(R.drawable.error_image);
                    });

        } else if (helpdesk.getCourseId() != null) {
            courseRef.document(helpdesk.getCourseId()).get()
                    .addOnSuccessListener(courseSnapshot -> {
                        if (courseSnapshot.exists()) {
                            Course course = courseSnapshot.toObject(Course.class);
                            if (course != null) {
                                holder.tvCourseTitle.setText(course.getTitle() != null ? course.getTitle() : "No Title");

                                // Fetch Post details related to the course
                                if (course.getPost1() != null) {
                                    postRef.document(course.getPost1()).get()
                                            .addOnSuccessListener(postSnapshot -> {
                                                if (postSnapshot.exists()) {
                                                    Post post = postSnapshot.toObject(Post.class);
                                                    if (post != null) {
                                                        userRef.document(post.getUserId()).get()
                                                                .addOnSuccessListener(userSnapshot -> {
                                                                    if (userSnapshot.exists()) {
                                                                        User author = userSnapshot.toObject(User.class);
                                                                        holder.tvAuthorName.setText(author != null && author.getName() != null
                                                                                ? author.getName()
                                                                                : "Unknown Author");
                                                                    } else {
                                                                        holder.tvAuthorName.setText("Unknown Author");
                                                                    }
                                                                })
                                                                .addOnFailureListener(e -> holder.tvAuthorName.setText("Error fetching author"));
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(e -> holder.tvAuthorName.setText("Error fetching course post"));
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> holder.tvCourseTitle.setText("Error fetching course"));
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
