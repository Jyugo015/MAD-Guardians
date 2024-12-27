package com.example.madguardians.ui.consult;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.example.madguardians.R;
import com.example.madguardians.ui.consult.adapter_lo.CounselorAdapter;
import com.example.madguardians.ui.consult.model_lo.CounselorModel;

import java.util.ArrayList;
import java.util.List;


public class AppointmentBookingFragment extends Fragment {



        CounselorAdapter adapter;
        private CalendarView calanderView;
        private String selectedDate;

        private String userName;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_appointment_booking , container, false);
            RecyclerView recyclerView = view.findViewById(R.id.booking_recycler_view);

            long todayInMillis = System.currentTimeMillis();


            List<CounselorModel> counselors = new ArrayList<>();
            calanderView = view.findViewById(R.id.calendarView);
            calanderView.setMinDate(todayInMillis);
            calanderView.setOnDateChangeListener((calanderView, year, month, dayOfMonth)->{
                selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                counselors.add(new CounselorModel("Test Counselor 1", "Mental Health Specialist", R.drawable.st_counselor, "30 years experience","T7qJoeJsvAXlAHjUXNUB",true));
                counselors.add(new CounselorModel("Mr. John Lee", "Career Guidance Counselor", R.drawable.jl_counselor, "8 years experience","mkK6PE2Ko1R3bW69Stye",false));
                counselors.add(new CounselorModel("Ms. Aisha Rahim", "Family Therapist", R.drawable.ar_counselor, "12 years experience","0q4AGraAU7WOlP4lYEMS",true));
                counselors.add(new CounselorModel("Dr. Emily Wong", "Child Psychologist", R.drawable.ew_counselor, "15 years experience","je37ml81yTnBHoPwc7pN",false));
                counselors.add(new CounselorModel("Mr. Michael Tan", "Stress Management Expert", R.drawable.mt_counselor, "7 years experience","DdTScSe7m4Uv8Y19MGug",false));

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter = new CounselorAdapter(getContext(), counselors, selectedDate);
                recyclerView.setAdapter(adapter);
            });



            return view;
        }
    }