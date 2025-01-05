package com.example.madguardians.ui.course;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.madguardians.R;
import com.example.madguardians.firebase.CourseFB;
import com.example.madguardians.firebase.DomainFB;
import com.example.madguardians.utilities.MediaHandler;
import com.example.madguardians.utilities.UploadCallback;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CourseOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseOverviewFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private CourseFB courseFB;
    private View view;

    public CourseOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CourseOverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CourseOverviewFragment newInstance(String param1, String param2) {
        CourseOverviewFragment fragment = new CourseOverviewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String courseId = getArguments().getString("courseId");
            CourseFB.getCourse(courseId, new UploadCallback<CourseFB>() {
                @Override
                public void onSuccess(CourseFB result) {
                    courseFB = result;
                    setView();
                }
                @Override
                public void onFailure(Exception e) {
                    Log.e("CourseOverviewFragment", "onFailure: failed to get course");
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_course_overview, container, false);
        setView();
        return view;
    }

    private void setView() {
        if (view != null && courseFB != null) {
            TextView TVTitle = view.findViewById(R.id.TVTitle);
            TextView TVDomain = view.findViewById(R.id.TVDomain);
            TextView TVDate = view.findViewById(R.id.TVDate);
            TextView TVAuthor = view.findViewById(R.id.TVAuthor);
            TextView TVDescription = view.findViewById(R.id.TVDescription);
            ImageView IVCover = view.findViewById(R.id.IVCover);

            TVTitle.setText(courseFB.getTitle());
            DomainFB.getDomain(courseFB.getDomainId(), new UploadCallback<DomainFB>() {
                @Override
                public void onSuccess(DomainFB result) {
                    TVDomain.setText("Domain: " + (String) result.getDomainName());
                }
                @Override
                public void onFailure(Exception e) {}
            });
            TVDate.setText("Date: " + courseFB.getDate());
            TVAuthor.setText(courseFB.getAuthor());
            TVDescription.setText(courseFB.getDescription());
            MediaHandler.displayImage(getContext(), courseFB.getCoverImage(), IVCover);

            androidx.constraintlayout.widget.ConstraintLayout LYLevel1 = view.findViewById(R.id.LYPost1);
            androidx.constraintlayout.widget.ConstraintLayout LYLevel2 = view.findViewById(R.id.LYPost2);
            androidx.constraintlayout.widget.ConstraintLayout LYLevel3 = view.findViewById(R.id.LYPost3);

            if (courseFB.getPost1() == null) {
                LYLevel1.setVisibility(View.GONE);
            } else {
                Log.d("CourseOverviewFragment", "Post1: " + courseFB.getPost1());
                LYLevel1.setOnClickListener(v -> {
                    Log.w("CourseOverviewFragment", "Level 1 clicked");
                    Navigation.findNavController(view).navigate(R.id.nav_post, getPassingBundle(courseFB.getPost1()));
                });
            }

            if (courseFB.getPost2() == null) {
                LYLevel2.setVisibility(View.GONE);
            } else{
                Log.d("CourseOverviewFragment", "Post2: " + courseFB.getPost2());
                LYLevel2.setOnClickListener(v -> {
                    Log.w("CourseOverviewFragment", "Level 2 clicked");
                    Navigation.findNavController(view).navigate(R.id.nav_post, getPassingBundle(courseFB.getPost2()));
                });
            }

            if (courseFB.getPost3() == null) {
                LYLevel3.setVisibility(View.GONE);
            } else {
                Log.d("CourseOverviewFragment", "Post3: " + courseFB.getPost3());
                LYLevel3.setOnClickListener(v -> {
                    Log.w("CourseOverviewFragment", "Level 3 clicked");
                    Navigation.findNavController(view).navigate(R.id.nav_post, getPassingBundle(courseFB.getPost3()));
                });
            }

            ToggleButton TBCollection = view.findViewById(R.id.TBCollection);
//            toggleCollection(courseFB, TBCollection);
//            checkCollectionStatus(courseFB, TBCollection, getContext());

            Button btnReport = view.findViewById(R.id.BTNReport);
            btnReport.setOnClickListener(v-> {
                reportCourse(courseFB.getCourseId());
            });

            view.clearFocus();
        }
    }

    private Bundle getPassingBundle (String post) {
        Bundle bundle = new Bundle();
        bundle.putString("courseId", courseFB.getCourseId());
        bundle.putString("postId", post);
        return bundle;
    }

    ////////////////////////////////////////////////////////
    // yewoon
//    private void checkCollectionStatus(CourseFB courseFB, ToggleButton button_collection, Context context) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        String userId = getUserId(context);
//
//        db.collection("collection")
//                .whereEqualTo("userId", userId)
//                .whereEqualTo("courseId", courseFB.getCourseId())
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        if (!task.getResult().isEmpty()) {
//                            // collected
//                            button_collection.setChecked(true);
//                        } else {
//                            // uncollected
//                            button_collection.setChecked(false);
//                        }
//                    } else {
//                        // search fail, set as false
//                        button_collection.setChecked(false);
//                    }
//                });
//    }
//    private void toggleCollection(CourseFB courseFB, ToggleButton button_collection) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        String userId = getUserId(button_collection.getContext());
//
//        if (button_collection.isChecked()) {
//            generateCollectionId(db, userId, courseFB.getCourseId(), new OnCollectionIdGeneratedListener() {
//                @Override
//                public void onCollectionIdGenerated(String collectionId) {
//                    Map<String, Object> collection = new HashMap<>();
//                    collection.put("collectionId", collectionId);
//                    collection.put("userId", userId);
//                    collection.put("courseId", courseFB.getCourseId());
//
//                    db.collection("collection").document(collectionId)
//                            .set(collection)
//                            .addOnSuccessListener(aVoid -> {
//                                Toast.makeText(button_collection.getContext(), "Collected", Toast.LENGTH_SHORT).show();
//                            })
//                            .addOnFailureListener(e -> {
//                                Toast.makeText(button_collection.getContext(), "Collect Fail", Toast.LENGTH_SHORT).show();
//                                button_collection.setChecked(false); // reset to false
//                            });
//                }
//            });
//        } else {
//            // Remove collection
//            db.collection("collection")
//                    .whereEqualTo("userId", userId)
//                    .whereEqualTo("courseId", courseFB.getCourseId())
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                db.collection("collection").document(document.getId())
//                                        .delete()
//                                        .addOnSuccessListener(aVoid -> {
//                                            Toast.makeText(button_collection.getContext(), "Remove collection", Toast.LENGTH_SHORT).show();
//                                        })
//                                        .addOnFailureListener(e -> {
//                                            Toast.makeText(button_collection.getContext(), "Remove collection fail", Toast.LENGTH_SHORT).show();
//                                            button_collection.setChecked(true); // reset to true
//                                        });
//                            }
//                        }
//                    });
//        }
//    }
//
//    interface OnCollectionIdGeneratedListener {
//        void onCollectionIdGenerated(String collectionId);
//    }
//
//    private void generateCollectionId(FirebaseFirestore db, String userId, String courseId, OnCollectionIdGeneratedListener listener) {
//        db.collection("collection")
//                .orderBy("collectionId", Query.Direction.DESCENDING)
//                .limit(1)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        if (!task.getResult().isEmpty()) {
//                            String latestCollectionId = task.getResult().getDocuments().get(0).getString("collectionId");
//                            int latestNumber = Integer.parseInt(latestCollectionId.substring(3));
//                            int newNumber = latestNumber + 1;
//                            String newCollectionId = String.format("COL%06d", newNumber);
//                            listener.onCollectionIdGenerated(newCollectionId);
//                        } else {
//                            // if null set as COL000001
//                            listener.onCollectionIdGenerated("COL000001");
//                        }
//                    } else {
//                        Log.e("FirestoreError", "Failed to get the latest collectionId", task.getException());
//                        Toast.makeText(db.getApp().getApplicationContext(), "Failed to generate collectionId", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private String getUserId(Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
//        return sharedPreferences.getString("user_id", null);
//    }

    ////////////////////////////////////////////////////////
    // zw
    private void reportCourse(String courseId) {
    }
}

