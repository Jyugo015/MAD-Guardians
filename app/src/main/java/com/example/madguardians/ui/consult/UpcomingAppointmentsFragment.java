package com.example.madguardians.ui.consult;

import static com.example.madguardians.ui.consult.utils_lo.FirebaseUtil.isCounselor;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.ui.consult.adapter_lo.AppointmentScheduleAdapter;
import com.example.madguardians.ui.consult.model_lo.AppointmentModel;
import com.example.madguardians.ui.consult.utils_lo.FirebaseUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UpcomingAppointmentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AppointmentScheduleAdapter appointmentAdapter;
    private List<AppointmentModel> appointmentList = new ArrayList<>();
    private String userName;
    private String counselorName = "Test Counselor 1";

    public UpcomingAppointmentsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_appointments, container, false);

        recyclerView = view.findViewById(R.id.appointments_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        appointmentAdapter = new AppointmentScheduleAdapter(appointmentList, getContext());
        recyclerView.setAdapter(appointmentAdapter);

        String userID = FirebaseUtil.currentUserId(getContext());
        FirebaseUtil.getUserNameById(userID).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userName = task.getResult();
                Log.d("Username", userName != null ? "Retrieved: " + userName : "No such user");
                fetchAppointmentsForDateRange();
            } else {
                Log.d("Username", "Failed to retrieve username");
            }
        });

        return view;
    }

    private void fetchAppointmentsForDateRange() {
        List<String> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // Generate future dates
        for (int i = 0; i <= 14; i++) {
            Calendar tempCalendar = (Calendar) calendar.clone();
            tempCalendar.add(Calendar.DAY_OF_YEAR, i);
            dateList.add(getFormattedDate(tempCalendar.getTime()));
        }

        // counselor list
        String[] counselors = {"Test Counselor 1", "Mr. John Lee", "Ms. Aisha Rahim", "Dr. Emily Wong", "Mr. Michael Tan"};

        isCounselor(new FirebaseUtil.SimpleCallback() {
            @Override
            public void onResult(boolean isCounselor) {
                for (String date : dateList) {
                    if (isCounselor) {
                        fetchAppointmentsSchedule(date);
                    } else {
                        for (String counselor : counselors) {
                            fetchUserAppointments(date, counselor);
                        }
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("Error", "Failed to check if user is counselor", e);
            }
        }, getActivity());
    }

    private void fetchAppointmentsSchedule(String date) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("appointments")
                .document(date)
                .collection(userName)
                .whereEqualTo("counselorName", userName)
                .whereEqualTo("bookStatus", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult()) {
                            AppointmentModel appointment = document.toObject(AppointmentModel.class);
                            if (appointment != null) {
                                populateAppointmentDetails(appointment, document, date, userName);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Error", "Failed to fetch schedule: " + e.getMessage()));
    }

    private void fetchUserAppointments(String date, String counselorName) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("appointments")
                .document(date)
                .collection(counselorName)
                .whereEqualTo("userName", userName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult()) {
                            AppointmentModel appointment = document.toObject(AppointmentModel.class);
                            if (appointment != null) {
                                populateAppointmentDetails(appointment, document, date, counselorName);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Error", "Failed to fetch user appointments: " + e.getMessage()));
    }

    private void populateAppointmentDetails(AppointmentModel appointment, DocumentSnapshot document, String date, String counselorName) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("user")
                .whereEqualTo("name", appointment.getUserName())
                .get()
                .addOnCompleteListener(userTask -> {
                    if (userTask.isSuccessful() && userTask.getResult() != null && !userTask.getResult().isEmpty()) {
                        DocumentSnapshot userDoc = userTask.getResult().getDocuments().get(0);

                        appointment.setUserEmail(userDoc.getString("email"));
                        appointment.setUserName(userDoc.getString("name"));
                        appointment.setCounselorName(counselorName);
                        appointment.setDate(date);
                        appointment.setTimeSlot(document.getString("time"));

                        synchronized (appointmentList) {
                            if (!appointmentList.contains(appointment)) {
                                appointmentList.add(appointment);
                                appointmentAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Error", "Failed to fetch user details: " + e.getMessage()));
    }

    private String getFormattedDate(Date date) {
        return new SimpleDateFormat("yyyy-M-d").format(date);
    }
}