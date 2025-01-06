package com.example.madguardians.comment.adapter;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.madguardians.comment.EduChildCommentFragment;

public class CommentPagerAdapter extends FragmentStateAdapter {

    private FragmentActivity fragmentActivity;
    public CommentPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.fragmentActivity = fragmentActivity;
    }

    @Override
    public Fragment createFragment(int position) {
        // Return a new fragment for each page
        switch (position) {
            case 0:
                Log.e("viewpager", "Show all");
                return EduChildCommentFragment.newInstance("All", fragmentActivity);  // All comments (unread and read)
            case 1:
                Log.e("viewpager", "Show unread");
                return EduChildCommentFragment.newInstance("Unread", fragmentActivity);  // Unread comments
            case 2:
                Log.e("viewpager", "Show read");
                return EduChildCommentFragment.newInstance("Read", fragmentActivity);   // Read comments
            default:
                Log.e("viewpager", "Show all");
                return EduChildCommentFragment.newInstance("All", fragmentActivity);  // Default to all comments
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Number of pages (tabs)
    }

}
