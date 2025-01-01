package com.example.madguardians.application;

import android.app.Application;

import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Executor;
import com.example.madguardians.database.FirestoreManager;

public class GuardianOfSDG extends Application {
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        FirestoreManager firestoreManager = new FirestoreManager(AppDatabase.getDatabase(this));
//        Executor.executeTask(() ->{
//            //for counselor
//            firestoreManager.initializeFirestoreListenersCounselor();
//            //for user
//            firestoreManager.initializeFirestoreListenersUser();
//            //for staff
//            firestoreManager.initializeFirestoreListenersStaff();
//        } );
//    }
}
