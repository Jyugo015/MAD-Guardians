package com.example.madguardians.notification;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class NotificationsPagerAdapter extends FragmentStateAdapter {
    public NotificationsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new UnreadNotificationsFragment();
            case 1:
                return new ReadNotificationsFragment();
            case 2:
                return new AllNotificationsFragment();
            default:
                return new UnreadNotificationsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
