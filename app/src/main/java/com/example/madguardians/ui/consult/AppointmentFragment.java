package com.example.madguardians.ui.consult;

import static com.example.madguardians.ui.consult.utils_lo.FirebaseUtil.isCounselor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.madguardians.R;
import com.example.madguardians.ui.consult.utils_lo.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;

public class AppointmentFragment extends Fragment {
    private String role = "User";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.d("User UID", uid);
        } else {
            Log.d("User UID", "No user is logged in");
        }

        View view = inflater.inflate(R.layout.fragment_appointment, container, false);
        Button BtnSetAppointment = view.findViewById(R.id.set_appointment_btn);
        ImageButton imageButton = view.findViewById(R.id.imageButton);



// Fetch and handle user's role
        isCounselor(new FirebaseUtil.SimpleCallback() {
            @Override
            public void onResult(boolean isCounselor) {
                role = isCounselor ? "Counselor" : "User"; // Update role based on result

                // Set button click listeners based on the role
                if (isCounselor) {
                    BtnSetAppointment.setOnClickListener(v -> {
                        NavController navController = Navigation.findNavController(v);
                        navController.navigate(R.id.action_appointmentFragment_to_appointmentSetFragment);
                    });
                } else {
                    BtnSetAppointment.setOnClickListener(v -> {
                        NavController navController = Navigation.findNavController(v);
                        navController.navigate(R.id.action_appointmentFragment_to_appointmentBookingFragment);
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("Error", "Failed to check if user is counselor", e);
                // Handle error (e.g., disable button or show an error message)
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_appointmentFragment_to_appointmentScheduleFragment);
            }
        });


        return view;
    }

    private void isCounselor(FirebaseUtil.SimpleCallback callback) {
        FirebaseUtil.isCounselor(callback, getActivity());
    }
}