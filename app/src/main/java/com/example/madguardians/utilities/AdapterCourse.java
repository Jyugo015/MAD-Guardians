package com.example.madguardians.utilities;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.madguardians.R;
import com.example.madguardians.firebase.CourseFB;
import com.example.madguardians.firebase.DomainFB;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdapterCourse extends RecyclerView.Adapter<AdapterCourse.CourseViewHolder> {

    private List<CourseFB> courseFBList;
    private List<CourseFB> originalCourseFBList;
    private OnItemClickListener listener;
    public AdapterCourse(List<CourseFB> courseFBList, OnItemClickListener listener) {
        this.courseFBList = courseFBList;
        this.originalCourseFBList = courseFBList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.segment_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        CourseFB courseFB = courseFBList.get(position);

        // Set the data
        holder.title.setText(courseFB.getTitle());
        holder.author.setText(courseFB.getAuthor());
        holder.date.setText(courseFB.getDate());
//        holder.views.setText(course.getViews());
//        holder.comments.setText(course.getComments());
        showImage(holder, courseFB);
        // check if it is verified
        if (isVerified(courseFB)) {
            holder.verifyStatus.setImageResource(R.drawable.ic_verified);
        } else {
            holder.verifyStatus.setImageResource(R.drawable.ic_verifying);
        }
        // check if it id collected
//        if (isCollected(courseFB)) {
//            holder.button_collection.setChecked(true);
//        }
        checkCollectionStatus(courseFB, holder.button_collection, holder.itemView.getContext());
        holder.button_collection.setOnClickListener(v -> toggleCollection(courseFB, holder.button_collection));


        // Handle button clicks
        holder.button_start.setOnClickListener(v -> listener.onStartClick(courseFB));
//        holder.button_collection.setOnClickListener(v -> listener.onCollectionClick(courseFB));
    }

    ////////////////////////////////////////////////////////////////////////////////////
    // zw
    private boolean isVerified(CourseFB courseFB) {
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////
    // yewoon
    private void checkCollectionStatus(CourseFB courseFB, ToggleButton button_collection, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = getUserId(context);

        db.collection("collection")
                .whereEqualTo("userId", userId)
                .whereEqualTo("courseId", courseFB.getCourseId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // collected
                            button_collection.setChecked(true);
                        } else {
                            // uncollected
                            button_collection.setChecked(false);
                        }
                    } else {
                        // search fail, set as false
                        button_collection.setChecked(false);
                    }
                });
    }

    private void toggleCollection(CourseFB courseFB, ToggleButton button_collection) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = getUserId(button_collection.getContext());

        if (button_collection.isChecked()) {
            generateCollectionId(db, userId, courseFB.getCourseId(), new OnCollectionIdGeneratedListener() {
                @Override
                public void onCollectionIdGenerated(String collectionId) {
                    Map<String, Object> collection = new HashMap<>();
                    collection.put("collectionId", collectionId);
                    collection.put("userId", userId);
                    collection.put("courseId", courseFB.getCourseId());

                    db.collection("collection").document(collectionId)
                            .set(collection)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(button_collection.getContext(), "Collected", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(button_collection.getContext(), "Collect Fail", Toast.LENGTH_SHORT).show();
                                button_collection.setChecked(false); // reset to false
                            });
                }
            });
        } else {
            // Remove collection
            db.collection("collection")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("courseId", courseFB.getCourseId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("collection").document(document.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(button_collection.getContext(), "Remove collection", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(button_collection.getContext(), "Remove collection fail", Toast.LENGTH_SHORT).show();
                                            button_collection.setChecked(true); // reset to true
                                        });
                            }
                        }
                    });
        }
    }

    private void generateCollectionId(FirebaseFirestore db, String userId, String courseId, OnCollectionIdGeneratedListener listener) {
        db.collection("collection")
                .orderBy("collectionId", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            String latestCollectionId = task.getResult().getDocuments().get(0).getString("collectionId");
                            int latestNumber = Integer.parseInt(latestCollectionId.substring(3));
                            int newNumber = latestNumber + 1;
                            String newCollectionId = String.format("COL%06d", newNumber);
                            listener.onCollectionIdGenerated(newCollectionId);
                        } else {
                            // if null set as COL000001
                            listener.onCollectionIdGenerated("COL000001");
                        }
                    } else {
                        Log.e("FirestoreError", "Failed to get the latest collectionId", task.getException());
                        Toast.makeText(db.getApp().getApplicationContext(), "Failed to generate collectionId", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_id", null);
    }

    interface OnCollectionIdGeneratedListener {
        void onCollectionIdGenerated(String collectionId);
    }

    ////////////////////////////////////////////////////////////////////////////////////

    private void showImage(CourseViewHolder holder, CourseFB courseFB) {
        Glide.with(holder.itemView.getContext()).load(courseFB.getCoverImage()).placeholder(R.drawable.placeholder_image).error(R.drawable.error_image).into(holder.image_cover);
    }

    @Override
    public int getItemCount() {
        return courseFBList.size();
    }

    public void filterCourseByDomain(List<DomainFB> domains) {
        if (domains == null || domains.isEmpty()) {
            courseFBList = originalCourseFBList;
        } else {
            courseFBList = originalCourseFBList.stream()
                .filter(course ->{
                        String domainId = course.getDomainId();
                        Log.d("TAG", "domainId: " + domainId);
                        return domains.stream().anyMatch(domain -> domain.getDomainId().equals(course.getDomainId()));
                }).collect(Collectors.toList());
        }
        Log.d("TAG", "filterCourseByDomain: " + courseFBList.toString());
        notifyDataSetChanged();
    }

    public void updateCourseList(List<CourseFB> cours) {
        courseFBList.clear();
        courseFBList.addAll(cours);
        notifyDataSetChanged();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, date, views, comments;
        ImageView image_cover, verifyStatus;
        Button button_start;
        ToggleButton button_collection;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.TVTitle);
            author = itemView.findViewById(R.id.TVAuthor);
            date = itemView.findViewById(R.id.TVDate);
            views = itemView.findViewById(R.id.TVView);
            comments = itemView.findViewById(R.id.TVComment);
            image_cover = itemView.findViewById(R.id.IVCover);
            verifyStatus = itemView.findViewById(R.id.IVVerify);
            button_start = itemView.findViewById(R.id.BTNStart);
            button_collection = itemView.findViewById(R.id.TBCollection);
        }
    }

    public interface OnItemClickListener {
        void onStartClick(CourseFB courseFB);
        void onCollectionClick(CourseFB courseFB);
    }
}
