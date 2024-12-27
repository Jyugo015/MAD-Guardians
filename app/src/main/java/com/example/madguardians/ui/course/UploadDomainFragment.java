package com.example.madguardians.ui.course;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.madguardians.R;
import com.example.madguardians.utilities.AdapterCourse;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UploadDomainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadDomainFragment extends Fragment implements AdapterCourse.OnItemClickListener{

    private String folderId;
    private String domainId;
    private ArrayList<CourseElement> courses;
    private RecyclerView RVCourse;
    private AdapterCourse courseAdapter;
    public static UploadDomainFragment newInstance(String param1, String param2) {
        UploadDomainFragment fragment = new UploadDomainFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courses = new ArrayList<>();

        if (getArguments() != null) {
            domainId = getArguments().getString("domainId");
            courses = CourseElement.getCourses(domainId);
        }
        courseAdapter = new AdapterCourse(courses, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_domain, container, false);
        RVCourse = view.findViewById(R.id.RVCourse);
        RVCourse.setLayoutManager(new LinearLayoutManager(getContext()));
//        courseList = courseDao.getAll();
        RVCourse.setAdapter(courseAdapter);

        Button BTNUpload = view.findViewById(R.id.BTNUpload);
        BTNUpload.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("domainId", domainId);
            Navigation.findNavController(view).navigate(R.id.nav_upload_course, bundle);
        });
        return view;
    }


    @Override
    public void onStartClick(CourseElement course) {
        Log.w("Home Fragment", "onStartClick" );
        Bundle bundle = new Bundle();
        bundle.putString("courseId", course.getCourseId());
        Navigation.findNavController(this.getView()).navigate(R.id.nav_course_overview, bundle);
    }

    @Override
    public void onCollectionClick(CourseElement course) {

    }
}