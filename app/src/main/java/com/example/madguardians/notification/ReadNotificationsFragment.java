package com.example.madguardians.notification;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.database.Notification;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ReadNotificationsFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView noReadNotifications;
    private NotificationAdapter adapter;
    private FirebaseFirestore db;
    private List<Notification> notificationList;
    private ListenerRegistration readNotificationsListener;
    SharedPreferences sharedPreferences;
    String userId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList);
        // Get SharedPreferences
        sharedPreferences = getContext().getSharedPreferences("user_preferences", MODE_PRIVATE);
        //Get userid
        userId = sharedPreferences.getString("user_id", null);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_notifications, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        noReadNotifications = view.findViewById(R.id.no_read_notifications);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadReadNotifications();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadReadNotifications();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (readNotificationsListener != null) {
            readNotificationsListener.remove();
            readNotificationsListener = null;
        }
    }

    private void loadReadNotifications() {
        readNotificationsListener =db.collection("notification")
                .whereEqualTo("userId", userId)
                .whereNotEqualTo("readTime", null)
                .orderBy("readTime", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        noReadNotifications.setText("Load Notifications Fail");
                        noReadNotifications.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        return;
                    }

                    notificationList.clear();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Notification notification = document.toObject(Notification.class);
                            notificationList.add(notification);
                        }
                        showReadNotifications();
                    } else {
                        showNoReadNotifications();
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void showNoReadNotifications() {
        recyclerView.setVisibility(View.GONE);
        noReadNotifications.setVisibility(View.VISIBLE);
        noReadNotifications.setText("No Read Notifications");
    }

    private void showReadNotifications() {
        recyclerView.setVisibility(View.VISIBLE);
        noReadNotifications.setVisibility(View.GONE);
    }
}
