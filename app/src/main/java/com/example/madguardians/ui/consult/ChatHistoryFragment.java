package com.example.madguardians.ui.consult;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.madguardians.R;
import com.example.madguardians.ui.consult.adapter_lo.RecentChatRecyclerAdapter;
import com.example.madguardians.ui.consult.model_lo.ChatroomModel;
import com.example.madguardians.ui.consult.utils_lo.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ChatHistoryFragment extends Fragment {
    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat_history, container, false);
        recyclerView = root.findViewById(R.id.recyler_view);
        return root;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        Log.d("ChatHistoryFragment", "NavController: " + (navController != null));

        setupRecyclerView(navController);
    }

    void setupRecyclerView(NavController navController) {
        Query query = FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds", FirebaseUtil.currentUserId())
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null && !task.getResult().isEmpty()) {
                    Log.d("ChatHistoryFragment", "Found chatrooms: " + task.getResult().size());
                } else {
                    Log.d("ChatHistoryFragment", "No chatrooms found");
                }
            } else {
                Log.e("ChatHistoryFragment", "Error fetching chatrooms", task.getException());
            }
        });

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class).build();

        adapter = new RecentChatRecyclerAdapter(options, navController);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) adapter.notifyDataSetChanged();
    }
}
