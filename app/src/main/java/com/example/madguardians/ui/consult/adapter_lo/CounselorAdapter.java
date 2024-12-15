package com.example.madguardians.ui.consult.adapter_lo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.ui.consult.model_lo.CounselorModel;
import com.example.madguardians.ui.consult.utils_lo.FirebaseUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CounselorAdapter extends RecyclerView.Adapter<CounselorAdapter.CounselorViewHolder> {

    Context context;
    List<CounselorModel> counselors;
    private String userName = "Test User 1";
    private String selectedDate;

    public CounselorAdapter(Context context, List<CounselorModel> counselors, String selectedDate) {
        this.context = context;
        this.counselors = counselors;
        this.selectedDate = selectedDate;
    }

    @NonNull
    @Override
    public CounselorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.counselor_recycler_view, parent, false);
        return new CounselorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CounselorViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String userID = FirebaseUtil.currentUserId();

//        FirebaseUtil.getUserNameById(userID).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                userName = task.getResult();
//                if (userName != null) {
//                    Log.d("Username", "Username retrieved successfully");
//                } else {
//                    Log.d("Username", "No such user name");
//                }
//            } else {
//                Log.d("Username", "Failed to get username");
//            }
//        });

        holder.TVName.setText(counselors.get(position).getName());
        holder.TVSkill.setText(counselors.get(position).getSkill());
        holder.TVExp.setText(counselors.get(position).getExperience());
        holder.imageButton.setImageResource(counselors.get(position).getImage());

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String counselorName = counselors.get(position).getName();

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("appointments")
                        .document(selectedDate)
                        .collection(counselorName)
                        .whereEqualTo("bookStatus",false)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                List<String> bookedSlots = new ArrayList<>();
                                for (DocumentSnapshot document : task.getResult()) {
                                    String timeSlot = document.getString("time");
                                    bookedSlots.add(timeSlot);
                                }


                                showAvailableTimeSlotsDialog(bookedSlots, counselorName);
                            } else {
                                Log.e("Firestore", "Failed to fetch time slots: " + task.getException().getMessage());
                            }
                        });
            }
        });
    }

    private void showAvailableTimeSlotsDialog(List<String> bookedSlots, String counselorName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select a Time Slot");

        // LinearLayout to hold the buttons
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);
        if (bookedSlots.isEmpty()) {
            builder.setMessage("No available time slots for " + counselorName + " on " + selectedDate);
            builder.setPositiveButton("OK", null);
            builder.show();
        } else {
            for (String timeSlot : bookedSlots) {
                Button timeSlotButton = new Button(context);
                timeSlotButton.setText(timeSlot);
                timeSlotButton.setOnClickListener(v -> {

                    confirmBooking(timeSlot, counselorName);
                });


                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                timeSlotButton.setLayoutParams(params);

                layout.addView(timeSlotButton);
            }

            builder.setView(layout);
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        }
    }

    private void confirmBooking(String selectedTimeSlot, String counselorName) {
        new AlertDialog.Builder(context)
                .setMessage("Do you want to book this time slot: " + selectedTimeSlot + " with " + counselorName + "?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    bookAppointment(selectedTimeSlot, counselorName);
                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                .create()
                .show();
    }

    private void bookAppointment(String selectedTimeSlot, String counselorName) {
        String userID = FirebaseUtil.currentUserId();

//        FirebaseUtil.getUserNameById(userID).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                userName = task.getResult();
//                if (userName != null) {
//                    Log.d("Username", "Username retrieved successfully");
//                } else {
//                    Log.d("Username", "No such user name");
//                }
//            } else {
//                userName = null;
//            }
//        });


        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("appointments")
                .document(selectedDate)
                .collection(counselorName)
                .whereEqualTo("time", selectedTimeSlot)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                        if (documentSnapshot.getString("userName") == null) {
                            firestore.collection("appointments")
                                    .document(selectedDate)
                                    .collection(counselorName)
                                    .document(documentSnapshot.getId())  // Update the specific document
                                    .update(
                                            "userName", userName,  // Set the current user as the getter
                                            "bookStatus", true       // Set the slot as booked
                                    )
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Booking successful");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Booking failed: " + e.getMessage());
                                    });
                        } else {
                            Log.d("Firestore", "Slot already booked by someone else.");
                        }
                    } else {
                        Log.d("Firestore", "Slot not found or query failed.");
                    }
                });
    }

    @Override
    public int getItemCount() {
        return counselors.size();
    }

    static class CounselorViewHolder extends RecyclerView.ViewHolder {

        ImageButton imageButton;
        TextView TVSkill, TVExp, TVName;

        CounselorViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.counselor_image);
            TVSkill = itemView.findViewById(R.id.counselor_skill);
            TVExp = itemView.findViewById(R.id.counselor_experience);
            TVName = itemView.findViewById(R.id.counselor_name);
        }
    }
}
