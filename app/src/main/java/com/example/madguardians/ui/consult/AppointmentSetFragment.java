package com.example.madguardians.ui.consult;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.madguardians.R;
import com.example.madguardians.ui.consult.adapter_lo.TimeSlotAdapter;
import com.example.madguardians.ui.consult.model_lo.TimeSlotModel;
import com.example.madguardians.ui.consult.utils_lo.FirebaseUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class AppointmentSetFragment extends Fragment {

    private CalendarView calanderView;
    FirebaseFirestore firestore;
    RecyclerView recyclerView;
    TimeSlotAdapter adapter;
    Button doneButton;
    private List<TimeSlotModel> timeSlotList;
    private String selectedDate;
    private String userName;
    private Set<TimeSlotModel> selectedTimeSlots = new HashSet<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_appointment_set, container, false);
        recyclerView = view.findViewById(R.id.time_slot_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

         // Get today's timestamp
        calanderView = view.findViewById(R.id.calendarView);
        // Set the minimum date to today's midnight
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long todayInMillis = calendar.getTimeInMillis();
        calanderView.setMinDate(todayInMillis);// Set the minimum selectable date to today

        // Get today's date

        selectedDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);

        // Set default selection to today's date
        calanderView.setDate(todayInMillis, false, true);

        calanderView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            timeSlotList = generateTimeSlots(); // Generate new time slots for the selected date
            adapter = new TimeSlotAdapter(timeSlotList, (timeSlot, isSelected) -> {
                if (isSelected) {
                    selectedTimeSlots.add(timeSlot);
                } else {
                    selectedTimeSlots.remove(timeSlot);
                }
                // Refresh the selected position in the RecyclerView
                int position = timeSlotList.indexOf(timeSlot);
                adapter.notifyItemChanged(position); // Notify the change for that item
            });
            recyclerView.setAdapter(adapter); // Set the new adapter
            recyclerView.setVisibility(View.VISIBLE);
        });

        String userID = FirebaseUtil.currentUserId(getContext());
        FirebaseUtil.getUserNameById(userID).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userName = task.getResult();
                Log.d("Username", "Username getted success");
            } else {
                userName = "Hello World";
            }
        });

        doneButton = view.findViewById(R.id.done_btn);
        doneButton.setOnClickListener(v -> {
            bookSelectedSlots(selectedDate);
            recyclerView.setVisibility(View.GONE);
        });

        return view;
    }


    private List<TimeSlotModel> generateTimeSlots() {
        List<TimeSlotModel> list = new ArrayList<>();
        String[] times = {
                "8:00 AM - 9:00 AM", "9:00 AM - 10:00 AM",
                "10:00 AM - 11:00 AM", "11:00 AM - 12:00 PM",
                "12:00 PM - 1:00 PM", "1:00 PM - 2:00 PM",
                "2:00 PM - 3:00 PM", "3:00 PM - 4:00 PM",
                "4:00 PM - 5:00 PM", "5:00 PM - 6:00 PM"
        };
        for (String time : times) {
            list.add(new TimeSlotModel(null, null, time, null, null, false));
        }
        return list;
    }

    private void bookSelectedSlots(String selectedDate) {
        if (selectedDate == null) {
            Toast.makeText(getContext(), "Please select a date.", Toast.LENGTH_SHORT).show();
            Log.d("BookingError", "Selected date is null.");
            return;
        }

        if (selectedTimeSlots.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one time slot.", Toast.LENGTH_SHORT).show();
            Log.d("BookingError", "No time slots selected.");
            return;
        }

        Log.d("BookingInfo", "Selected date: " + selectedDate);
        Log.d("BookingInfo", "Selected time slots: " + selectedTimeSlots.toString());

        if (selectedDate == null || selectedTimeSlots.isEmpty()) {
            Toast.makeText(getContext(), "Please select a date and time slots.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        for (TimeSlotModel timeSlot : selectedTimeSlots) {
            timeSlot.setSetterName(userName);
            timeSlot.setTimestamp(Timestamp.now());

            firestore.collection("appointments")
                    .document(selectedDate)
                    .collection(userName)
                    .document(timeSlot.getTime()) // Directly check by document ID (time)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().exists()) {
                                // Slot is available, proceed with booking
                                Map<String, Object> bookingData = new HashMap<>();
                                bookingData.put("time", timeSlot.getTime());
                                bookingData.put("counselorName", timeSlot.getSetterName());
                                bookingData.put("timestamp", timeSlot.getTimestamp());
                                bookingData.put("userName", timeSlot.getGetterName());

                                firestore.collection("appointments")
                                        .document(selectedDate)
                                        .collection(userName)
                                        .document(timeSlot.getTime()) // Use time as the document ID
                                        .set(bookingData)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("Firestore", "Booking successful");
                                            Toast.makeText(getContext(), "Slot booked successfully!", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("Firestore", "Booking failed: " + e.getMessage());
                                        });
                            } else {
                                // Slot already exists
                                Toast.makeText(getContext(), "This slot is already booked.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("Firestore", "Error checking existing appointments: " + task.getException().getMessage());
                        }
                    });

        }
        selectedTimeSlots.clear();
        adapter.notifyDataSetChanged(); // Refresh adapter after booking
    }
}
