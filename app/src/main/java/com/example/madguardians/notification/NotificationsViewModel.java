package com.example.madguardians.notification;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Notification;
import com.example.madguardians.database.NotificationDao;

import java.util.ArrayList;
import java.util.List;

public class NotificationsViewModel extends AndroidViewModel {

    private NotificationDao notificationDao;
    private List<Notification> unreadNotifications;
    private List<Notification> readNotifications;

    public NotificationsViewModel(@NonNull Application application) {
        super(application);
        notificationDao = AppDatabase.getDatabase(application).notificationDao();
    }

    public List<Notification> getUnreadNotifications(String userId) {
        if (unreadNotifications == null) {
            unreadNotifications = new ArrayList<>();
        }
        unreadNotifications = notificationDao.getUnreadNotificationsByUserId(userId);
        return unreadNotifications;
    }

    public List<Notification> getReadNotifications(String userId) {
        if (readNotifications == null) {
            unreadNotifications = new ArrayList<>();
        }
        readNotifications = notificationDao.getByUserId(userId);
        return readNotifications;
    }

    public void insertNotification(Notification notification) {
        new Thread(() -> notificationDao.insert(notification)).start();
    }

    public void deleteAllNotifications() {
        new Thread(() -> notificationDao.deleteAll()).start();
    }

    public void updateNotification(Notification notification) {
        new Thread(() -> notificationDao.update(notification)).start();
    }
}