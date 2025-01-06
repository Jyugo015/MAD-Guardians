package com.example.madguardians.collection;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.madguardians.R;
import com.example.madguardians.firebase.CourseFB;
import com.example.madguardians.firebase.DomainFB;
import com.example.madguardians.ui.home.HomeFragment;
import com.example.madguardians.utilities.AdapterCourse;
import com.example.madguardians.utilities.UploadCallback;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CollectionFragment extends Fragment {
    private List<CourseFB> courseFBList = new ArrayList<>();
    private RecyclerView RVCourse;
    private AdapterCourse courseAdapter;
    private FirebaseFirestore db;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);

        db = FirebaseFirestore.getInstance();

        // Get userId
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preferences", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", null);

        // Initialized RecyclerView and Adapter
        courseAdapter = new AdapterCourse(courseFBList, new AdapterCourse.OnItemClickListener() {
            @Override
            public void onStartClick(CourseFB courseFB) {
                Bundle bundle = new Bundle();
                bundle.putString("courseId", courseFB.getCourseId());
                Navigation.findNavController(requireView()).navigate(R.id.nav_course_overview, bundle);
            }

        });
        RVCourse = view.findViewById(R.id.RVCourse);
        RVCourse.setLayoutManager(new LinearLayoutManager(getContext()));
        RVCourse.setAdapter(courseAdapter);

        // load collection
        loadCollections();

        return view;
    }

    private void loadCollections() {
        if (userId == null) {
            Toast.makeText(getContext(), "Haven't Login, Cannot load collection", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("collection")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        List<String> courseIds = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            courseIds.add(document.getString("courseId"));
                        }

                        // Search Information of courses
                        db.collection("course")
                                .whereIn("courseId", courseIds)
                                .get()
                                .addOnCompleteListener(courseTask -> {
                                    if (courseTask.isSuccessful() && !courseTask.getResult().isEmpty()) {
                                        courseFBList.clear();
                                        for (QueryDocumentSnapshot courseDoc : courseTask.getResult()) {
                                            CourseFB courseFB = courseDoc.toObject(CourseFB.class);
                                            courseFBList.add(courseFB);
                                        }
                                        courseAdapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(getContext(), "No collection found.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getContext(), "Haven't collect any course", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Collection Fail:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}