package com.example.chatbotwithchoice.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.chatbotwithchoice.AllAppointmentsFragment;
import com.example.chatbotwithchoice.AppointmentHistoryFragment;
import com.example.chatbotwithchoice.PastAppointmentsFragment;
import com.example.chatbotwithchoice.UpcomingAppointmentsFragment;

public class AppointmentViewAdapter extends FragmentStateAdapter {


    public AppointmentViewAdapter(@NonNull AppointmentHistoryFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new AllAppointmentsFragment();
            case 1:
                return new PastAppointmentsFragment();
            case 2:
                return new UpcomingAppointmentsFragment();
            default:
                return new AllAppointmentsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
