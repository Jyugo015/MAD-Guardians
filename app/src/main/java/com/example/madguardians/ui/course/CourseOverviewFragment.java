package com.example.madguardians.ui.course;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.madguardians.R;
import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Course;
import com.example.madguardians.database.CourseDao;
import com.example.madguardians.database.PostDao;
import com.example.madguardians.database.UserDao;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CourseOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseOverviewFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private CourseElement course;
    private View view;
    private AppDatabase db;
    private CourseDao courseDao;
    private PostDao postDao;

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
        db = AppDatabase.getDatabase(getContext());
        courseDao = db.courseDao();
        postDao = db.postDao();
        if (getArguments() != null) {
            course = CourseElement.getCourse(getArguments().getString("courseId"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_course_overview, container, false);

        if (course != null) {
            TextView TVTitle = view.findViewById(R.id.TVTitle);
            TextView TVDomain = view.findViewById(R.id.TVDomain);
            TextView TVDate = view.findViewById(R.id.TVDate);
            TextView TVView = view.findViewById(R.id.TVView);
            TextView TVComment = view.findViewById(R.id.TVComment);
            TextView TVAuthor = view.findViewById(R.id.TVAuthor);
            TextView TVDescription = view.findViewById(R.id.TVDescription);

            TVTitle.setText(course.getTitle());
//                TVDomain.setText(course.getDomain());
            TVDate.setText("Date: " + course.getDate());
            TVAuthor.setText(course.getAuthor());
        }
        return view;
    }

    /////////////////////////////////////////////////////////////////////
    private void setIsChecked(CourseElement course, boolean isChecked) {
        TextView TVCollection = view.findViewById(R.id.TVCollection);
        ToggleButton TBCollection = view.findViewById(R.id.TBCollection);
        TBCollection.setChecked(isChecked);
        TVCollection.setText((isChecked) ? "Added to collection" : "Add to collection");

        // change database
    }

    private boolean isCollected(CourseElement course) {
        return true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        androidx.constraintlayout.widget.ConstraintLayout LYLevel1 = view.findViewById(R.id.LYPost1);
        androidx.constraintlayout.widget.ConstraintLayout LYLevel2 = view.findViewById(R.id.LYPost2);
        androidx.constraintlayout.widget.ConstraintLayout LYLevel3 = view.findViewById(R.id.LYPost3);

        Log.d("CourseOverviewFragment", "Post1: " + course.getPost1());
        Log.d("CourseOverviewFragment", "Post2: " + course.getPost2());
        Log.d("CourseOverviewFragment", "Post3: " + course.getPost3());

        if (course.getPost1() == null) {
            LYLevel1.setVisibility(View.GONE);
        } else {
            LYLevel1.setOnClickListener(v -> {
                Log.w("CourseOverviewFragment", "Level 1 clicked");
                Bundle bundle = new Bundle();
                bundle.putString("postId", course.getPost1());
                Navigation.findNavController(view).navigate(R.id.nav_post, bundle);
            });
        }

        if (course.getPost2() == null) {
            LYLevel2.setVisibility(View.GONE);
        } else{
            LYLevel2.setOnClickListener(v -> {
                Log.w("CourseOverviewFragment", "Level 2 clicked");
                Bundle bundle = new Bundle();
                bundle.putString("postId", course.getPost2());
                Navigation.findNavController(view).navigate(R.id.nav_post, bundle);
            });
        }

        if (course.getPost3() == null) {
            LYLevel3.setVisibility(View.GONE);
        } else {
            LYLevel3.setOnClickListener(v -> {
                Log.w("CourseOverviewFragment", "Level 3 clicked");
                Bundle bundle = new Bundle();
                bundle.putString("postId", course.getPost3());
                Navigation.findNavController(view).navigate(R.id.nav_post, bundle);
            });
        }

        ToggleButton TBCollection = view.findViewById(R.id.TBCollection);
        setIsChecked(course, isCollected(course));
        TBCollection.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) setIsChecked(course, true);
            else setIsChecked(course, false);
        });

        view.clearFocus();
    }
}

