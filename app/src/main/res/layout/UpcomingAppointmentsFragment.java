package com.example.chatbotwithchoice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbotwithchoice.R;
import com.example.chatbotwithchoice.adapter.AppointmentScheduleAdapter;
import com.example.chatbotwithchoice.model.AppointmentModel;
import com.example.chatbotwithchoice.utils.FirebaseUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UpcomingAppointmentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AppointmentScheduleAdapter appointmentAdapter;
    private List<AppointmentModel> appointmentList = new ArrayList<>();

    public UpcomingAppointmentsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming_appointments, container, false);

        recyclerView = view.findViewById(R.id.appointments_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        appointmentAdapter = new AppointmentScheduleAdapter(appointmentList, getContext());
        recyclerView.setAdapter(appointmentAdapter);

        fetchUpcomingAppointments();

        return view;
    }

    private void fetchUpcomingAppointments() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("appointments")
                .whereEqualTo("counselorName", FirebaseUtil.currentUserId())
                .whereGreaterThan("timestamp", Timestamp.now())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        appointmentList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            AppointmentModel appointment = document.toObject(AppointmentModel.class);
                            appointmentList.add(appointment);
                        }
                        appointmentAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to load upcoming appointments.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
