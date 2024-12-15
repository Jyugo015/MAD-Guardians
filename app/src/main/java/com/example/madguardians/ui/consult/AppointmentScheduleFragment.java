package com.example.madguardians.ui.consult;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.madguardians.R;
import com.example.madguardians.ui.consult.adapter_lo.AppointmentViewAdapter;
import com.google.android.material.tabs.TabLayout;


public class AppointmentScheduleFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    AppointmentViewAdapter appointmentViewAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_appointment_schedule, container, false);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager2 = view.findViewById(R.id.view_pager);
        appointmentViewAdapter = new AppointmentViewAdapter(this);
        viewPager2.setAdapter(appointmentViewAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });


        return view;
    }
}