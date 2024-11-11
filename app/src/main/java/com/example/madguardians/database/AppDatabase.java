package com.example.madguardians.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, DomainInterested.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract DomainInterestedDao domainInterestedDao();
    public abstract DomainDao domainDao();
    public abstract VerPostDao verPostDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
//put this code in the onCreate method in Main Activity or any Activity class u may
//need to access to the database
//    // Get the database instance
//    database = AppDatabase.getDatabase(this);
//
//    // Example: Use the UserDao to interact with the database
//    UserDao userDao = database.userDao();
//
//    // To perform an operation, such as inserting a user
//        new Thread(() -> {
//        User user = new User();
//        user.setUserId("U123");
//        user.setName("John Doe");
//        user.setEmail("john.doe@example.com");
//        user.setPhoneNo(123456789);
//        user.setPassword("password123");
//
//        userDao.insertUser(user);
//
//        // Retrieving and observing data
//        LiveData<User> userData = userDao.getUserById("U123");
//        userData.observe(this, new Observer<User>() {
//            @Override
//            public void onChanged(User user) {
//                // Update UI with the retrieved user data
//                if (user != null) {
//                    // Handle the user data here
//                }
//            }
//        });
//    }).start();
}
