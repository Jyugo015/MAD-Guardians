package com.example.madguardians;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.DomainDao;
import com.example.madguardians.database.FirestoreManager;
import com.example.madguardians.database.NetworkAvailability;
import com.example.madguardians.database.UserDao;

public class MainActivity extends AppCompatActivity {
    private AppDatabase database;
    private FirestoreManager firestoreManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        configureSignUpButton();
        configureLoginInButton();
        firestoreManager.onLoginSyncUser("");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the database
        database = AppDatabase.getDatabase(this);
        //Initilaize FirestoreManager
        firestoreManager = new FirestoreManager (database);

        // Use the database as needed
        UserDao userDao = database.userDao();
//        DomainDao domainDao = database.domainDao();

        // Check Internet connection
//        if (!NetworkAvailability.isInternetAvailable(this)) {
//            disableUI();
//        } else {
//            loadContentFromFirestore();
//        }

//        retryButton.setOnClickListener(view -> {
//            if (NetworkAvailability.isInternetAvailable(this)) {
//                enableUI();
//                loadContentFromFirestore();
//            }
//        });
    }


//    private void disableUI() {
//        contentView.setVisibility(View.GONE);
//        retryButton.setVisibility(View.VISIBLE);
//    }

//    private void enableUI() {
//        contentView.setVisibility(View.VISIBLE);
//        retryButton.setVisibility(View.GONE);
//    }

//    private void loadContentFromFirestore() {
//         //Fetch Firestore content here
//    }

    private void configureSignUpButton() {
        Button signUpButton = (Button) findViewById(R.id.signup);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, signuppage_activity.class));
            }
        });
    }

    private void configureLoginInButton() {
        Button logInButton = (Button) findViewById(R.id.login);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, loginpage_activity.class));
            }
        });
    }
}