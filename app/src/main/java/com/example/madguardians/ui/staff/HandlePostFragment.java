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

public class HandlePostFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public HandlePostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.staff_fragment_handle_post_hzw, container, false);

        tabLayout = view.findViewById(R.id.tabLayout_HandlePost);
        viewPager = view.findViewById(R.id.viewPager_HandlePost);

        // Set up the adapter for ViewPager2
        ViewPageAdapter adapter = new ViewPageAdapter(requireActivity());
        adapter.addFragment(new Tab1PostFragment(), "All");
        adapter.addFragment(new Tab2PostFragment(), "Pending");
        adapter.addFragment(new Tab3PostFragment(), "Completed");
        viewPager.setAdapter(adapter);

        // Use TabLayoutMediator to link TabLayout and ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(adapter.getTitle(position));
        }).attach();

        return view;
    }
}

//package com.example.madguardians.ui.staff;
//
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//import androidx.viewpager2.widget.ViewPager2;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.example.madguardians.R;
//import com.google.android.material.tabs.TabLayout;
//import com.google.android.material.tabs.TabLayoutMediator;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class HandlePostFragment extends Fragment {
//
//    private TabLayout tabLayout;
//    private ViewPager2 viewPager;
//
//    public HandlePostFragment() {
//        // Required empty constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_handle_post, container, false);
//
//        tabLayout = view.findViewById(R.id.tabLayout_HandlePost);
//        viewPager = view.findViewById(R.id.viewPager_HandlePost);
//
//        // Prepare mock data
//        List<Post> allPosts = getMockPosts();
//
//        // Set up ViewPager2 with tabs
//        ViewPageAdapter adapter = new ViewPageAdapter(requireActivity());
//        adapter.addFragment(BaseTabFragment.newInstance(allPosts, HandlePostTabFragment.class), "All");
//        adapter.addFragment(BaseTabFragment.newInstance(filterPostsByStatus(allPosts, "Pending"), HandlePostTabFragment.class), "Pending");
//        adapter.addFragment(BaseTabFragment.newInstance(filterPostsByStatus(allPosts, "Completed"), HandlePostTabFragment.class), "Completed");
//        viewPager.setAdapter(adapter);
//
//        // Link TabLayout with ViewPager2
//        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(adapter.getTitle(position))).attach();
//
//        return view;
//    }
//
//    private List<Post> getMockPosts() {
//        List<Post> posts = new ArrayList<>();
//        // Pass null or placeholder values for the missing parameters (image and date)
//        posts.add(new Post(null, "Title 1", "Author 1", "2024-12-04", "Pending"));
//        posts.add(new Post(null, "Title 2", "Author 2", "2024-12-04", "Completed"));
//        posts.add(new Post(null, "Title 3", "Author 3", "2024-12-04", "Pending"));
//        return posts;
//    }
//
//    private List<Post> filterPostsByStatus(List<Post> posts, String status) {
//        List<Post> filtered = new ArrayList<>();
//        for (Post post : posts) {
//            if (post.getStatus().equalsIgnoreCase(status)) {
//                filtered.add(post);
//            }
//        }
//        return filtered;
//    }
//}
