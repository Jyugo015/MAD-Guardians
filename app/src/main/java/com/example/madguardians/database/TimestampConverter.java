package com.example.madguardians.database;

//import androidx.room.TypeConverter;
//
//import com.google.firebase.Timestamp;
//import com.google.firebase.firestore.PropertyName;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//public class TimestampConverter {
//
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//
//    @TypeConverter
//    public static String fromTimestamp(Date date) {
//        return date == null ? null : dateFormat.format(date);
//    }
//
//    @TypeConverter
//    public static Date toTimestamp(String dateString) {
//        try {
//            return dateString == null ? null : dateFormat.parse(dateString);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//    private String date;
//
//    @PropertyName("date")
//    public String getDateAsString() {
//        if (date != null) {
//            return new Timestamp(new Date()).toString();  // Convert to String
//        }
//        return null;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }
//}

import androidx.room.TypeConverter;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimestampConverter {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    @TypeConverter
    public static String fromTimestamp(Date date) {
        return date == null ? null : dateFormat.format(date);
    }

    @TypeConverter
    public static Date toTimestamp(String dateString) {
        try {
            return dateString == null ? null : dateFormat.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

