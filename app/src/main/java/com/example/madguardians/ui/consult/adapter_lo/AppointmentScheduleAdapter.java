package com.example.madguardians.ui.consult.adapter_lo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.ui.consult.model_lo.AppointmentModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AppointmentScheduleAdapter extends RecyclerView.Adapter<AppointmentScheduleAdapter.AppointmentViewHolder> {

        private List<AppointmentModel> appointments;
        private Context context;

        public AppointmentScheduleAdapter(List<AppointmentModel> appointments, Context context) {
            this.appointments = appointments;
            this.context = context;
        }

        @NonNull
        @Override
        public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_appointment_schedule, parent, false);
            return new AppointmentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
            AppointmentModel appointment = appointments.get(position);

            String name = "Username: " + appointment.getUserName();
            String em = "Email: "+ appointment.getUserEmail();
            String date = "Date : "+ appointment.getDate();
            String timeSlot = "Time: " + appointment.getTimeSlot();
            String cName = "CounselorName: "+ appointment.getCounselorName();

            holder.userName.setText(name);
            holder.userEmail.setText(em);
            holder.date.setText(date);
            holder.timeSlot.setText(timeSlot);
            holder.counselorName.setText(cName);


            holder.rejectButton.setOnClickListener(v -> rejectAppointment(appointment));

        }

        @Override
        public int getItemCount() {
            return appointments.size();
        }

        public class AppointmentViewHolder extends RecyclerView.ViewHolder {
            TextView userName, userEmail, date, timeSlot, counselorName;
            Button rejectButton;

            public AppointmentViewHolder(View itemView) {
                super(itemView);
                userName = itemView.findViewById(R.id.tv_username);
                userEmail = itemView.findViewById(R.id.tv_email);
                date = itemView.findViewById(R.id.tv_date);
                timeSlot = itemView.findViewById(R.id.tv_timeslot);
                rejectButton = itemView.findViewById(R.id.btn_reject);
                counselorName = itemView.findViewById(R.id.tv_counselorName);
            }
        }



    private void rejectAppointment(AppointmentModel appointment) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("appointments")
                .document(appointment.getDate())
                .collection(appointment.getCounselorName())
                .document(appointment.getTimeSlot())
                .update("userName",null, "bookStatus", false)
                .addOnSuccessListener(aVoid -> {
                    appointment.setUserName(null);
                    appointment.setBookStatus(false);
                    Toast.makeText(context, "Appointment canceled", Toast.LENGTH_SHORT).show();

                    // Update RecyclerView without removing the slot
                    int index = appointments.indexOf(appointment);
                    notifyItemChanged(index);
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to cancel appointment", Toast.LENGTH_SHORT).show());
    }
    }
