package com.example.madguardians.ui.staff;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.madguardians.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HandleReportedPostFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public HandleReportedPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.staff_fragment_handle_reported_post_hzw, container, false);

        tabLayout = view.findViewById(R.id.tabLayout_HandleReportedPost);
        viewPager = view.findViewById(R.id.viewPager_HandleReportedPost);

        // Set up the adapter for ViewPager2
        ViewPageAdapter adapter = new ViewPageAdapter(requireActivity());
        adapter.addFragment(new Tab1ReportedPostFragment(), "All");
        adapter.addFragment(new Tab2ReportedPostFragment(), "Pending");
        adapter.addFragment(new Tab3ReportedPostFragment(), "Completed");
        viewPager.setAdapter(adapter);

        // Use TabLayoutMediator to link TabLayout and ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(adapter.getTitle(position));
        }).attach();

        return view;
    }
}