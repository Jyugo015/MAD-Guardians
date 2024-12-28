package com.example.madguardians.notification;
import com.example.madguardians.database.Notification;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;

public class NotificationUtils {
    private FirebaseFirestore db;

    public NotificationUtils() {
        db = FirebaseFirestore.getInstance();
    }

    public void createTestNotification(String userId,String message) {
        // Test Notification
        String notificationId = "N" + System.currentTimeMillis();
        Notification notification = new Notification(
                notificationId,
                userId,
                message,
                Timestamp.now(), // deliveredTime
                null // readTime
        );

        // Store to Firestore
        db.collection("notification")
                .document(notificationId)
                .set(notification)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Notification store success.");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Store Notification error:" + e.getMessage());
                });
    }

    public void createMultipleTestNotifications(String userId, int count, String message) {
        for (int i = 0; i < count; i++) {
            createTestNotification(userId,message);
        }
    }
}
