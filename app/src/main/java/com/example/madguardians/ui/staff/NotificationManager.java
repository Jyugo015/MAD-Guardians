package com.example.madguardians.ui.staff;

import android.content.Context;

import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.FirestoreManager;
import com.example.madguardians.database.Notification;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class NotificationManager {
    private final Context context;
    private final FirestoreManager firestoreManager;

    public NotificationManager(Context context) {
        this.context = context;
        this.firestoreManager = new FirestoreManager(AppDatabase.getDatabase(context)); // Initialize FirestoreManager
    }

    public void sendNotification(String userId, String message) {
        try {
            String notificationId = UUID.randomUUID().toString();
            Timestamp deliveredTime = getCurrentTimestamp();

            Notification notification = new Notification(
                    notificationId,
                    userId,
                    message,
                    deliveredTime,
                    null // readTime is null by default
            );
            firestoreManager.onInsertUpdate("insert","notification", notification, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Timestamp getCurrentTimestamp() {
        return Timestamp.now();
//        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}