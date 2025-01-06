package com.example.madguardians.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdapterCourse extends RecyclerView.Adapter<AdapterCourse.CourseViewHolder> {

    private List<CourseFB> courseFBList;
    private List<CourseFB> originalCourseFBList;
    private OnItemClickListener listener;
    private static final String TAG = "AdapterCourse";
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
        FirebaseController.getMatchedCollection(FirebaseController.USER, FirebaseController.getIdName(FirebaseController.USER), courseFB.getAuthor(), new UploadCallback<List<HashMap<String, Object>>>(){
            @Override
            public void onSuccess(List<HashMap<String, Object>> result) {
                if (! result.isEmpty())
                    holder.author.setText((String) result.get(0).get("name"));
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("TAG", "onFailure: failed to get username", e);
            }
        });
        holder.date.setText(courseFB.getDate());
        showImage(holder, courseFB);
        // check if it is verified
        isVerified(courseFB, new UploadCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean isVerified) {
                courseFB.setVerified(isVerified);
                Log.d(TAG, "onSuccess: isVerified: " + isVerified);;
                holder.verifyStatus.setImageResource((isVerified) ? R.drawable.ic_verified : R.drawable.ic_verifying);
            }
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "onFailure: ", e);
            }
        });

        checkCollectionStatus(courseFB, holder.button_collection, holder.itemView.getContext());
        holder.button_collection.setOnClickListener(v -> toggleCollection(courseFB, holder.button_collection));

        // Handle button clicks
        holder.button_start.setOnClickListener(v -> listener.onStartClick(courseFB));
    }

    private void isVerified(CourseFB courseFB, UploadCallback<Boolean> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("verPosts")
                .whereEqualTo("postId", courseFB.getPost1())
                .limit(1) // Assuming only one VerPost per post
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Extract the VerPost data
                        Map<String, Object> data = queryDocumentSnapshots.getDocuments().get(0).getData();
                        if (data != null && "approved".equalsIgnoreCase((String) data.get("verifiedStatus"))) {
                            callback.onSuccess(true); // Post is verified
                        } else {
                            callback.onSuccess(false); // Post is not verified
                        }
                    } else {
                        callback.onSuccess(false); // No VerPost found, consider it not verified
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("isVerified", "Failed to check verification status: " + e.getMessage(), e);
                    callback.onFailure(e); // Handle failure case
                });
    }

    // check all post
    public void arePostsVerified(CourseFB courseFB, UploadCallback<Map<String, Boolean>> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Boolean> verificationResults = new HashMap<>();

        // Get the IDs of the posts to verify
        List<String> postIds = new ArrayList<>();
        if (courseFB.getPost1() != null && !courseFB.getPost1().isEmpty()) postIds.add(courseFB.getPost1());
        if (courseFB.getPost2() != null && !courseFB.getPost2().isEmpty()) postIds.add(courseFB.getPost2());
        if (courseFB.getPost3() != null && !courseFB.getPost3().isEmpty()) postIds.add(courseFB.getPost3());

        // If no posts are present, return an empty map
        if (postIds.isEmpty()) {
            callback.onSuccess(verificationResults);
            return;
        }

        // Use a counter to track completed checks
        final int[] checksCompleted = {0};

        for (String postId : postIds) {
            db.collection("verPosts")
                    .whereEqualTo("postId", postId)
                    .limit(1) // Assuming only one VerPost per post
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        boolean isVerified = false;
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Map<String, Object> data = queryDocumentSnapshots.getDocuments().get(0).getData();
                            if (data != null && "approved".equalsIgnoreCase((String) data.get("verifiedStatus"))) {
                                isVerified = true;
                            }
                        }
                        // Store the result for this post ID
                        verificationResults.put(postId, isVerified);

                        // Check if all posts have been processed
                        checksCompleted[0]++;
                        if (checksCompleted[0] == postIds.size()) {
                            callback.onSuccess(verificationResults);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("arePostsVerified", "Failed to check post: " + postId, e);
                        verificationResults.put(postId, false); // Default to false on failure

                        // Check if all posts have been processed
                        checksCompleted[0]++;
                        if (checksCompleted[0] == postIds.size()) {
                            callback.onFailure(e);
                        }
                    });
        }
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

    public void filterCoursesBySearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            courseFBList = originalCourseFBList;
        } else {
            courseFBList = originalCourseFBList.stream()
                    .filter(course -> course.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                            (course.getAuthor() != null && course.getAuthor().toLowerCase().contains(query.toLowerCase())))
                    .collect(Collectors.toList());
        }
        notifyDataSetChanged();
    }

    public void filterCoursesByVerified (boolean isVerified) {
        Log.d("TAG", "filterCoursesByVerified: " + isVerified);
        if (isVerified) {
            courseFBList = originalCourseFBList.stream()
                    .filter(courseFB -> courseFB.isVerified() == isVerified)
                    .collect(Collectors.toList());
        } else {
            courseFBList = originalCourseFBList;
        }
        notifyDataSetChanged();
    }
    public void updateCourseList(List<CourseFB> cours) {
        courseFBList.clear();
        courseFBList.addAll(cours);
        notifyDataSetChanged();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, date;
        ImageView image_cover, verifyStatus;
        Button button_start;
        ToggleButton button_collection;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.TVTitle);
            author = itemView.findViewById(R.id.TVAuthor);
            date = itemView.findViewById(R.id.TVDate);
            image_cover = itemView.findViewById(R.id.IVCover);
            verifyStatus = itemView.findViewById(R.id.IVVerify);
            button_start = itemView.findViewById(R.id.BTNStart);
            button_collection = itemView.findViewById(R.id.TBCollection);
        }
    }

    public interface OnItemClickListener {
        void onStartClick(CourseFB courseFB);
    }
}
