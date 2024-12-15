package com.example.madguardians.ui.consult;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.madguardians.ui.consult.AllAppointmentsFragment;
import com.example.madguardians.ui.consult.AppointmentScheduleFragment;
import com.example.madguardians.ui.consult.PastAppointmentsFragment;
import com.example.madguardians.ui.consult.UpcomingAppointmentsFragment;

public class AppointmentViewAdapter extends FragmentStateAdapter {


    public AppointmentViewAdapter(@NonNull AppointmentScheduleFragment fragmentActivity) {
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
