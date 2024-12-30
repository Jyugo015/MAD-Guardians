package com.example.madguardians.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madguardians.R;
import com.example.madguardians.database.Notification;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Notification> notifications;

    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.message.setText(notification.getMessage());
        holder.deliveryTime.setText(formatTimestamp(notification.getDeliveredTime()));
        if (notification.getReadTime() != null) {
            holder.itemView.setAlpha(0.5f); // set read translucence
        } else {
            holder.itemView.setAlpha(1.0f); // set unread as normal
        }
        holder.itemView.setOnClickListener(v -> {
            markAsRead(notification);
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView message, deliveryTime;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            deliveryTime = itemView.findViewById(R.id.delivery_time);
        }
    }

    private void markAsRead(Notification notification) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notification").document(notification.getNotificationId())
                .update("readTime", FieldValue.serverTimestamp())
                .addOnSuccessListener(aVoid -> {
                    notifications.remove(notification);
                    notifyDataSetChanged();
                });
    }
    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return "N/A";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return formatter.format(timestamp.toDate());
    }
}
