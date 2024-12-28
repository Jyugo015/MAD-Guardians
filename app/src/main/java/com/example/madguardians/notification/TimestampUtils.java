package com.example.madguardians.notification;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimestampUtils {
    public static String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }

        Date date = timestamp.toDate(); // 转换为 Date 对象
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return formatter.format(date);
    }
}
