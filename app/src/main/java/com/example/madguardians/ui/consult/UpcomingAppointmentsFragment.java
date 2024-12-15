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
    private String userName = "User2";
    private String counselorName = "Test Counselor 1";

    public UpcomingAppointmentsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_appointments, container, false);

        recyclerView = view.findViewById(R.id.appointments_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        appointmentAdapter = new AppointmentScheduleAdapter(appointmentList, getContext());
        recyclerView.setAdapter(appointmentAdapter);

        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 14; i++) {
            final String date = getFormattedDate(calendar.getTime()); // Get formatted date

            isCounselor(new FirebaseUtil.SimpleCallback() {
                @Override
                public void onResult(boolean isCounselor) {
                    if (isCounselor) {
                        fetchAppoinmentsSchedule(date);
                    } else {
                        fetchUserAppointments(date);
                    }
                }

                @Override
                public void onError(Exception e) {
                    // Handle error, maybe log it
                    Log.e("Error", "Failed to check if user is counselor", e);
                }
            });

            // Move to the next day in the loop
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return view;
    }



    private void fetchAppoinmentsSchedule(String date){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("appointments")
                .document(date)
                .collection(counselorName)
                .whereEqualTo("counselorName",counselorName)
                .whereEqualTo("bookStatus",true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();

                        for (DocumentSnapshot document : documents) {
                            AppointmentModel appointment = document.toObject(AppointmentModel.class);

                            if (appointment != null && appointment.getUserName() != null && !appointment.getUserName().isEmpty()) {
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


                                                String timeSlot = document.getString("time");
                                                appointment.setTimeSlot(timeSlot);

                                                appointmentList.add(appointment);
                                                appointmentAdapter.notifyDataSetChanged();
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("AppointmentsFragment", "Error fetching appointments: " + e.getMessage());
                                            Toast.makeText(getContext(), "Error fetching appointments: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        });
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AppointmentsFragment", "Error fetching appointments: " + e.getMessage());
                    Toast.makeText(getContext(), "Error fetching appointments: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });


    }

    private void fetchUserAppointments(String date) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("appointments")
                .document(date)
                .collection(counselorName)
                .whereEqualTo("userName", userName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();

                        for (DocumentSnapshot document : documents) {
                            AppointmentModel appointment = document.toObject(AppointmentModel.class);

                            if (appointment != null && appointment.getUserName() != null && !appointment.getUserName().isEmpty()) {
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
                                                appointment.setCounselorName(counselorName);

                                                String timeSlot = document.getString("time");
                                                appointment.setTimeSlot(timeSlot);

                                                appointmentList.add(appointment);
                                                appointmentAdapter.notifyDataSetChanged();
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("AppointmentsFragment", "Error fetching appointments: " + e.getMessage());
                                            Toast.makeText(getContext(), "Error fetching appointments: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        });
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AppointmentsFragment", "Error fetching appointments: " + e.getMessage());
                    Toast.makeText(getContext(), "Error fetching appointments: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private String getFormattedDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
}

