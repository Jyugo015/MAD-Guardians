package com.example.madguardians.comment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.viewpager2.widget.ViewPager2;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.example.madguardians.R;
import com.example.madguardians.comment.adapter.CommentPagerAdapter;
import com.example.madguardians.database.Comments;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class Edu_CommentFragment extends Fragment{
    private TabLayout tabLayout;
    private ViewPager2 viewPager;


    public Edu_CommentFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.comment_edu_fragment, container, false);

//        // Retrieve the title passed to this fragment
//        if (getArguments() != null) {
//            title = getArguments().getString("label");
//        }
//
//        TextView textView = view.findViewById(R.id.textView);
//        textView.setText(title);

        // Initialize TabLayout and ViewPager2
        tabLayout = view.findViewById(R.id.topTabComment);
        viewPager = view.findViewById(R.id.view_pagerComment);

        // Set up ViewPager2 with an adapter (you can pass data if needed)
        CommentPagerAdapter adapter = new CommentPagerAdapter(requireActivity());
        Log.e("viewpager", "before run");
        viewPager.setAdapter(adapter);

        // Connect TabLayout and ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("All");
                    break;
                case 1:
                    tab.setText("Unread");
                    break;
                case 2:
                    tab.setText("Read");
                    break;
            }
        }).attach();

        return view;
    }
        // Set the title for the toolbar programmatically
//        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
//        if (toolbar != null) {
//            toolbar.setTitle("User's Comment");  // Or use a string resource like getString(R.string.collection_label)
//        }
//
//        // Set the title gravity to right
//        TextView title = (TextView) toolbar.getChildAt(0); // Get the first child (the title)
//        title.setGravity(Gravity.END);  // Set to right

        // Initialize the toolbar
//        Toolbar toolbarBottom = view.findViewById(R.id.toolbarBottom);
//        // Set the toolbar as the ActionBar
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbarBottom);
//        // Handle the back icon click
//        toolbarBottom.setNavigationOnClickListener(v -> {
////            requireActivity().onBackPressedDispatcher.onBackPressed();  // Go back to previous screen
//            requireActivity().getSupportFragmentManager().popBackStack();
//        });

//        // Enable the back button in the toolbar
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Show back button
//            getSupportActionBar().setDisplayShowHomeEnabled(true);  // Ensure it appears as an icon
//        }

//        // Handle back button press (if any)
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            if (item.getItemId() == android.R.id.home) {
//                onBackPressed();
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }


        // Set the navigation icon (back button or any icon)
//        toolbar.setNavigationIcon(R.drawable.ic_back);  // Replace with your icon
//        toolbar.setNavigationOnClickListener(view -> {
//            // Handle navigation icon click (back button)
//            onBackPressed();
//        });

//        tvAge = view.findViewById(R.id.TVAge);
        // Get SharedPreferences (retain the item stored until the app is uninstalled)
//        sharedPreferences = requireContext().getSharedPreferences("staff_preferences", getContext().MODE_PRIVATE);
//        staffId = sharedPreferences.getString("staff_id", null); // Retrieve logged-in staff ID
//
//        handlePostText = view.findViewById(R.id.handlePostText);
//        handleReportedPostText = view.findViewById(R.id.handleReportedPostText);
//        handleReportedCommentText = view.findViewById(R.id.handleReportedCommentText);
//        handleEducatorText = view.findViewById(R.id.handleEducatorText);
//        View.OnClickListener OCLHandlePost = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Navigation.findNavController(view).navigate(R.id.action_nav_staff_to_handlePostFragment);
//            }
//        };
//
//
//        View.OnClickListener OCLHandleReportedPost = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Navigation.findNavController(view).navigate(R.id.action_nav_staff_to_handleReportedPostFragment);
//            }
//        };
//        View.OnClickListener OCLHandleReportedComment = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Navigation.findNavController(view).navigate(R.id.action_nav_staff_to_handleReportedCommentFragment);
//            }
//        };
//        View.OnClickListener OCLHandleEducator = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Navigation.findNavController(view).navigate(R.id.action_nav_staff_to_handleEducatorFragment);
//            }
//        };
//        handlePostButton.setOnClickListener(OCLHandlePost);
//        handleReportedPostButton.setOnClickListener(OCLHandleReportedPost);
//        handleReportedCommentButton.setOnClickListener(OCLHandleReportedComment);
//        handleEducatorButton.setOnClickListener(OCLHandleEducator);
//        handlePostText.setOnClickListener(OCLHandlePost);
//        handleReportedPostText.setOnClickListener(OCLHandleReportedPost);
//        handleReportedCommentText.setOnClickListener(OCLHandleReportedComment);
//        handleEducatorText.setOnClickListener(OCLHandleEducator);
//
//        TabLayout tabLayout = view.findViewById(R.id.topTabComment);
//        ViewPager2 viewPager = view.findViewById(R.id.view_pagerComment);
//
//        // Set up the adapter
//        viewPager.setAdapter(new FragmentStateAdapter(this) {
//            @Override
//            public Fragment createFragment(int position) {
//                switch (position) {
//                    case 0: return new AllCommentsFragment();
//                    case 1: return new UnreadCommentsFragment();
//                    case 2: return new ReadCommentsFragment();
//                    default: return new AllCommentsFragment();
//                }
//            }
//
//            @Override
//            public int getItemCount() {
//                return 3; // Number of tabs/pages
//            }
//        });
//
//        // Link TabLayout and ViewPager2
//        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
//            switch (position) {
//                case 0: tab.setText("All"); break;
//                case 1: tab.setText("Unread"); break;
//                case 2: tab.setText("Read"); break;
//            }
//        }).attach();
//
//        return view;
//    }

        // Static method to create a new instance of the fragment with arguments
//        public static Edu_CommentFragment newInstance(String label) {
//            MyFragment fragment = new MyFragment();
//            Bundle args = new Bundle();
//            args.putString("label", label); // Pass label instead of title
//            fragment.setArguments(args);
//            return fragment;
//        }

}
