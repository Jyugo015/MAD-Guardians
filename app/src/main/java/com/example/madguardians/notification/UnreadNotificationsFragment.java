package com.example.madguardians.notification;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class UnreadNotificationsFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView noUnreadNotifications;
    private NotificationAdapter adapter;
    private FirebaseFirestore db;
    private List<Notification> notificationList;
    private ListenerRegistration unreadNotificationsListener;
    SharedPreferences sharedPreferences;
    String userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList);
        // Get SharedPreferences
        sharedPreferences = getContext().getSharedPreferences("user_preferences", MODE_PRIVATE);
        //Get userid
        userId = sharedPreferences.getString("user_id", null);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unread_notifications, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        noUnreadNotifications = view.findViewById(R.id.no_unread_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadUnreadNotifications();
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        loadUnreadNotifications();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (unreadNotificationsListener != null) {
            unreadNotificationsListener.remove();
            unreadNotificationsListener = null;
        }
    }

    private void loadUnreadNotifications() {
        unreadNotificationsListener = db.collection("notification")
                .whereEqualTo("userId", userId)
                .whereEqualTo("readTime", null)
                .orderBy("deliveredTime", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        noUnreadNotifications.setText("Load Notifications Fail");
                        noUnreadNotifications.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        return;
                    }

                    notificationList.clear();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Notification notification = document.toObject(Notification.class);
                            notificationList.add(notification);
                        }
                        showUnreadNotifications();
                    } else {
                        showNoUnreadNotifications();
                    }
                    adapter.notifyDataSetChanged();
                });
    }
    private void showNoUnreadNotifications() {
        recyclerView.setVisibility(View.GONE);
        noUnreadNotifications.setVisibility(View.VISIBLE);
    }

    private void showUnreadNotifications() {
        recyclerView.setVisibility(View.VISIBLE);
        noUnreadNotifications.setVisibility(View.GONE);
    }
}