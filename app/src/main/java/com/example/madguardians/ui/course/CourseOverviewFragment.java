package com.example.madguardians.ui.course;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.madguardians.R;
import com.example.madguardians.firebase.CourseFB;
import com.example.madguardians.firebase.DomainFB;
import com.example.madguardians.utilities.UploadCallback;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CourseOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseOverviewFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private CourseFB courseFB;
    private List<CourseFB> cours;
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
        this.courseFB = courseFB;
        if (view != null && courseFB != null) {
            TextView TVTitle = view.findViewById(R.id.TVTitle);
            TextView TVDomain = view.findViewById(R.id.TVDomain);
            TextView TVDate = view.findViewById(R.id.TVDate);
            TextView TVView = view.findViewById(R.id.TVView);
            TextView TVComment = view.findViewById(R.id.TVComment);
            TextView TVAuthor = view.findViewById(R.id.TVAuthor);
            TextView TVDescription = view.findViewById(R.id.TVDescription);

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
            setIsChecked(courseFB, isCollected(courseFB));
            TBCollection.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) setIsChecked(courseFB, true);
                else setIsChecked(courseFB, false);
            });
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
//        bundle.putString("domainId", course.getDomainId());
//        bundle.putString("date", course.getDate());
//        bundle.putString("folderId", course.getFolderId());
        bundle.putString("postId", post);
        return bundle;
    }

    /////////////////////////////////////////////////////////////////////
    private void setIsChecked(CourseFB courseFB, boolean isChecked) {
        TextView TVCollection = view.findViewById(R.id.TVCollection);
        ToggleButton TBCollection = view.findViewById(R.id.TBCollection);
        TBCollection.setChecked(isChecked);
        TVCollection.setText((isChecked) ? "Added to collection" : "Add to collection");

        // change database
    }

    ////////////////////////////////////////////////////////
    // yewoon
    private boolean isCollected(CourseFB courseFB) {
        return true;
    }

    ////////////////////////////////////////////////////////
    // zw
    private void reportCourse(String courseId) {
    }
}

