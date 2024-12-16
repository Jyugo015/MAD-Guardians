package com.example.madguardians.ui.consult;

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




        if(true
//                isCounselor()
        ){
            BtnSetAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_appointmentFragment_to_appointmentSetFragment);
                }
            });
        }else{
            BtnSetAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_appointmentFragment_to_appointmentBookingFragment);
                }
            });

        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_appointmentFragment_to_appointmentScheduleFragment);
            }
        });


        return view;
    }

    public boolean isCounselor(){
        if(role.equals("Counselor")){
            return true;
        }
        return false;
    }
}