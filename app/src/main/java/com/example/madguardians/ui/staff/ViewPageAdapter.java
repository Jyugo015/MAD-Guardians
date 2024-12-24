package com.example.madguardians.ui.staff;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class ViewPageAdapter extends FragmentStateAdapter {
    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private final ArrayList<String> fragmentTitle = new ArrayList<>();

    public ViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentArrayList.add(fragment);
        fragmentTitle.add(title);
        Log.d("ViewPageAdapter", "Added fragment: " + title);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentArrayList.get(position);
//        switch (position){
//            case 0:
//                return new Tab1Fragment();
//            case 1:
//                return new Tab2Fragment();
//            case 2:
//                return new Tab3Fragment();
//            default:
//                return new Tab1Fragment();
//        }
    }

    @Override
    public int getItemCount() {
        return fragmentArrayList.size();
    }

    public String getTitle(int position) {
        return fragmentTitle.get(position);
    }
}