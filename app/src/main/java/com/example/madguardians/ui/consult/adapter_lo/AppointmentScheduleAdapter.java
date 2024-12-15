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

            holder.userName.setText(appointment.getUserName());
            holder.userEmail.setText(appointment.getUserEmail());
            holder.date.setText(appointment.getDate());
            holder.timeSlot.setText(appointment.getTimeSlot());
            holder.counselorName.setText(appointment.getCounselorName());

            holder.approveButton.setOnClickListener(v -> approveAppointment(appointment));
            holder.rejectButton.setOnClickListener(v -> rejectAppointment(appointment));

        }

        @Override
        public int getItemCount() {
            return appointments.size();
        }

        public class AppointmentViewHolder extends RecyclerView.ViewHolder {
            TextView userName, userEmail, date, timeSlot, counselorName;
            Button approveButton, rejectButton;

            public AppointmentViewHolder(View itemView) {
                super(itemView);
                userName = itemView.findViewById(R.id.tv_username);
                userEmail = itemView.findViewById(R.id.tv_email);
                date = itemView.findViewById(R.id.tv_date);
                timeSlot = itemView.findViewById(R.id.tv_timeslot);
                approveButton = itemView.findViewById(R.id.btn_approve);
                rejectButton = itemView.findViewById(R.id.btn_reject);
                counselorName = itemView.findViewById(R.id.tv_counselorName);
            }
        }


        private void approveAppointment(AppointmentModel appointment) {

        }


        private void rejectAppointment(AppointmentModel appointment) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("appointments")
                    .document(appointment.getDate())
                    .collection(appointment.getCounselorName())
                    .document(appointment.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Appointment canceled", Toast.LENGTH_SHORT).show();
                        appointments.remove(appointment);
                        notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Failed to cancel appointment", Toast.LENGTH_SHORT).show());
        }
    }
