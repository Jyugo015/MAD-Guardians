package com.example.madguardians.ui.consult;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.madguardians.R;
import com.example.madguardians.databinding.FragmentConsultBinding;
import com.example.madguardians.ui.consult.utils_lo.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;

public class ConsultFragment extends Fragment {

    private FragmentConsultBinding binding;
    private String userID;
    private String counselorID;
    private String counselorName;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ConsultViewModel consultViewModel =
                new ViewModelProvider(this).get(ConsultViewModel.class);

        binding = FragmentConsultBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        TextView chatResponse = binding.chatResponse.findViewById(R.id.chat_response);
        TextView directingResponse = binding.directingResponse.findViewById(R.id.directing_response);
        userID = FirebaseUtil.currentUserId(getContext());

//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//        Map<String, Object> counselorData = new HashMap<>();
//        counselorData.put("online", false);
//        counselorData.put("skill", "Mental Health Specialist");
//        counselorData.put("name", "Test Counselor 1");
//        counselorData.put("experience", "30 years experience");
//
//        firestore.collection("counselors")
//                .document("U0025")
//                .set(counselorData, SetOptions.merge()) // Ensures it updates or adds missing fields
//                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document updated successfully!"))
//                .addOnFailureListener(e -> Log.e("Firestore", "Error updating document", e));

        isCounselor(new FirebaseUtil.SimpleCallback() {
            @Override
            public void onResult(boolean isCounselor) {
                String role = isCounselor ? "Counselor" : "User"; // Update role based on result

                if (isCounselor) {
                    binding.chatbotMessage.setText("Good day Counselor. What is your plan today?");

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("counselors")
                            .document(userID)
                            .update("online",true);

                } else {
                    Log.e("Counselor checking","not a counselor");
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("Error", "Failed to check if user is counselor", e);
                // Handle error (e.g., disable button or show an error message)
            }
        });


        binding.btnViewAppointmentHistory.setVisibility(View.GONE);

        checkChatroomExistence(hasChatroom -> {
            if (hasChatroom) {
                binding.btnViewAppointmentHistory.setVisibility(View.VISIBLE);
            }
        });

        binding.btnSeekAdvice.setOnClickListener(v -> {
            chatResponse.setText("Seek Advice From Counselor");
            chatResponse.setVisibility(View.VISIBLE);
            directingResponse.setText("Searching counselor....");
            new Handler().postDelayed(()->{

            },1000);


            new Handler().postDelayed(() -> {
                isCounselorOnline(isOnline -> {
                    if (isOnline) {
                        Bundle bundle = new Bundle();
                        bundle.putString("counselorID", counselorID);
                        bundle.putString("counselorName", counselorName);

                        NavController navController = Navigation.findNavController(v);
                        navController.navigate(R.id.action_nav_consult_to_chatFragment, bundle);
                        Log.d("ConsultFragment", "Navigating to ChatFragment with counselor: " + counselorName);
                    } else {
                        directingResponse.setText("Sorry, no counselor is available now. Please make an appointment.");
                        directingResponse.setVisibility(View.VISIBLE);
                    }
                });
            }, 2000);
        });


        binding.btnMakeAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatResponse.setText("Make an Appointment");
                chatResponse.setVisibility(View.VISIBLE);
                new Handler().postDelayed(()->{
                    directingResponse.setText("Redirecting to appointment page");
                },1000);



                new Handler().postDelayed(()->{
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_nav_consult_to_appointmentFragment);},2000);

            }
        });

        binding.btnViewAppointmentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_nav_consult_to_chatHistoryFragment);
            }
        });

        return root;
    }

    public void isCounselorOnline(Callback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

//        firestore.collection("counselors")
//                .get()
//                .addOnSuccessListener(querySnapshot -> {
//                    for (QueryDocumentSnapshot doc : querySnapshot) {
//                        Log.d("Firestore Debug", "Document: " + doc.getId() + ", Data: " + doc.getData());
//                    }
//                });
//
//        firestore.collection("counselors")
//                .document("0010")
//                .get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        Log.d("Firestore Document", "Document exists: " + documentSnapshot.getData());
//                    } else {
//                        Log.d("Firestore Document", "Document does not exist.");
//                    }
//                })
//                .addOnFailureListener(e -> Log.e("Firestore", "Error: ", e));

        firestore.collection("counselors")
                .whereEqualTo("online", true)
                .get(Source.SERVER)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d("Firestore Query", "Documents fetched: " + task.getResult().size());
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            Log.d("Firestore Document", "ID: " + documentSnapshot.getId() +
                                    ", Name: " + documentSnapshot.getString("name") +
                                    ", Online: " + documentSnapshot.getBoolean("online"));
                            counselorID = documentSnapshot.getId();
                            counselorName = documentSnapshot.getString("name");
                            callback.onResult(true);
                            return;
                        }
                    } else {
                        Log.d("Firestore Query", "Failed: ", task.getException());
                    }
                    callback.onResult(false);
                });



    }

    public interface Callback {
        void onResult(boolean isOnline);
    }


    private void checkChatroomExistence(Callback callback) {
        String currentUserId = FirebaseUtil.currentUserId(getContext());
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("chatrooms")
                .whereArrayContains("userIds", currentUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        callback.onResult(true);
                    } else {
                        callback.onResult(false);
                    }
                });
    }
    private void isCounselor(FirebaseUtil.SimpleCallback callback) {
        FirebaseUtil.isCounselor(callback, getActivity());
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}