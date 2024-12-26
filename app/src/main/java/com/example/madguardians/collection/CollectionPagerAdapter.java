package com.example.madguardians.collection;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CollectionPagerAdapter extends FragmentStateAdapter {
    public CollectionPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AllCollectionFragment();
            case 1:
                return new CourseCollectionFragment();
            case 2:
                return new VolunteerCollectionFragment();
            default:
                return new AllCollectionFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
