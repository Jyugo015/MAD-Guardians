package com.example.madguardians.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.firebase.CourseFB;
import com.example.madguardians.firebase.DomainFB;
import com.example.madguardians.firebase.FolderFB;
import com.example.madguardians.firebase.MediaFB;
import com.example.madguardians.firebase.PostFB;
import com.example.madguardians.utilities.AdapterCourse;
import com.example.madguardians.utilities.FirebaseController;
import com.example.madguardians.utilities.UploadCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements AdapterCourse.OnItemClickListener {

    private RecyclerView RVCourse;
    private AdapterCourse courseAdapter;
    private Spinner SPDomain;
    private List<CourseFB> courseFBList = new ArrayList<>();
    List<DomainFB> domains = new ArrayList<>();
    List<DomainFB> selectedDomain = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CourseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courseAdapter = new AdapterCourse(courseFBList, HomeFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        CourseFB.getCourses(new UploadCallback<List<CourseFB>>() {
            @Override
            public void onSuccess(List<CourseFB> courseFBList) {

                courseAdapter.updateCourseList(courseFBList);
                Log.d("TAG", "onSuccess: size = " + courseFBList.size());
                Log.d("TAG", "onSuccess: start3" + courseFBList.toString());
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("TAG", "onFailure: " + e.getMessage());
            }
        });
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RVCourse = view.findViewById(R.id.RVCourse);
        SPDomain = view.findViewById(R.id.SNDomain);
        RVCourse.setLayoutManager(new LinearLayoutManager(getContext()));
        RVCourse.setAdapter(courseAdapter);

        SearchView searchView = view.findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                courseAdapter.filterCoursesBySearch(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                courseAdapter.filterCoursesBySearch(newText);
                return true;
            }
        });

        CheckBox checkbox_verified = view.findViewById(R.id.CBVerified);
        checkbox_verified.setOnClickListener(v -> {
            courseAdapter.filterCoursesByVerified(checkbox_verified.isChecked());
        });

        // check if user is verified to upload
        ImageView BTNUpload = view.findViewById(R.id.BTNUpload);
        FirebaseController.getMatchedCollection(FirebaseController.FOLDER, "userId", getUserId(), new UploadCallback<List<HashMap<String, Object>>>() {
            @Override
            public void onSuccess(List<HashMap<String, Object>> result) {
                if (result == null || result.isEmpty()) {
                    BTNUpload.setVisibility(View.GONE);
                } else {
                    BTNUpload.setVisibility(View.VISIBLE);
                    BTNUpload.setOnClickListener(v -> {
                        Navigation.findNavController(view).navigate(R.id.nav_domains);
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("HomeFragment", "onFailure: ", e);
            }
        });

        DomainFB.getDomains(new UploadCallback<List<DomainFB>>() {
            @Override
            public void onSuccess(List<DomainFB> result) {
                domains.clear();
                domains.addAll(result);
                CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), domains);
                SPDomain.setAdapter(adapter);
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("HomeFragment", "onFailure: ", e);
            }
        });

        return view;
    }

    private String getUserId() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString("user_id", null);
    }

    @Override
    public void onStartClick(CourseFB courseFB) {
        Log.w("Home Fragment", "onStartClick" );
        Bundle bundle = new Bundle();
        bundle.putString("courseId", courseFB.getCourseId());
        Navigation.findNavController(this.getView()).navigate(R.id.nav_course_overview, bundle);
    }

    private class CustomSpinnerAdapter extends ArrayAdapter<DomainFB> {

        Context context;
        List<DomainFB> domains;
        public CustomSpinnerAdapter(@NonNull Context context, List<DomainFB> domains) {
            super(context, android.R.layout.simple_list_item_1, domains);
            this.context = context;
            this.domains = domains;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return createCustomView(position, convertView, parent, false);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return createCustomView(position, convertView, parent, true);
        }

        private View createCustomView(int position, View convertView, ViewGroup parent, boolean isDropdown) {
            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.segment_spinner_dropdown_domains, parent, false);

            TextView textView = convertView.findViewById(R.id.text);
            CheckBox checkBox = convertView.findViewById(R.id.checkbox);

            if (isDropdown) {
                DomainFB domain = domains.get(position);
                textView.setText(domain.getDomainName());
                checkBox.setChecked(selectedDomain.contains(domain));

                checkBox.setOnClickListener(v-> {
                    if (checkBox.isChecked()) {
                        selectedDomain.add(domain);
                    } else {
                        selectedDomain.remove(domain);
                    }
                    Log.d("TAG", "createCustomView: ");
                    courseAdapter.filterCourseByDomain(selectedDomain);
                });
            } else {
                checkBox.setVisibility(View.INVISIBLE);
                textView.setText("Filter by Domains");
            }

            return convertView;
        }
    }
}