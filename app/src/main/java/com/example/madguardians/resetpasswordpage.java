package com.example.madguardians;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Executor;
import com.example.madguardians.database.FirestoreManager;
import com.example.madguardians.database.User;
import com.example.madguardians.database.UserDao;

public class resetpasswordpage extends AppCompatActivity {

    private EditText password;
    private EditText confirmPassword;
    private Button resetPasswordButton;
    private ImageView passwordToggle;
    private ImageView confirmPasswordToggle;

    private SharedPreferences sharedPreferences;
    private AppDatabase appDatabase;
    private UserDao userDao;
    private String userId;
    private String email;

        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_resetpasswordpage);

            email = getIntent().getStringExtra("email");
            if (email == null) {
                Toast.makeText(this, "Email not received", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            password = findViewById(R.id.new_password);
            confirmPassword = findViewById(R.id.confirmed_password);
            resetPasswordButton = findViewById(R.id.resetPassword);
            passwordToggle = findViewById(R.id.icon_passwoard);
            confirmPasswordToggle = findViewById(R.id.icon_passwoard2);

            // Get SharedPreferences
            sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
            // Get user ID from SharedPreferences
            userId = sharedPreferences.getString("user_id", null);
            // Initialize database
            appDatabase = AppDatabase.getDatabase(this);
            // Initialize userDao
            userDao = appDatabase.userDao();
            // Set password visibility toggles
            passwordToggle.setOnClickListener(v -> togglePasswordVisibility(password, passwordToggle));
            confirmPasswordToggle.setOnClickListener(v -> togglePasswordVisibility(confirmPassword, confirmPasswordToggle));


            resetPasswordButton.setOnClickListener(v -> {
                // Get the text input from the EditText fields
                String newPassword = password.getText().toString();
                String confirmPasswordText = confirmPassword.getText().toString();
                if (newPassword.isEmpty() || confirmPasswordText.isEmpty()) {
                    Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if passwords match
                if (newPassword.equals(confirmPasswordText)) {
                    // Fetch the user by userId asynchronously
                    Executor.executeTask(() -> {
                        User user = userDao.getByEmail(email);
                        if (user != null) {
                            // Update the user's password
                            user.setPassword(newPassword);

                            // Update the user in Firestore
                            FirestoreManager firestoreManager = new FirestoreManager(appDatabase);
                            Executor.executeTask(() -> firestoreManager.onInsertUpdate("user", user, getApplicationContext()));

                            // Redirect to login page on the main thread
                            runOnUiThread(() -> {
                                Intent intent = new Intent(resetpasswordpage.this, loginpage_activity.class);
                                startActivity(intent);
                                finish();  // Optionally, finish the current activity to prevent user from going back
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(resetpasswordpage.this, "User not found", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                } else {
                    // Passwords don't match
                    Toast.makeText(resetpasswordpage.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            });
            configureSignUpButton();
        }

    private void togglePasswordVisibility(EditText editText, ImageView toggleIcon) {
        if (editText.getTransformationMethod() instanceof PasswordTransformationMethod) {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            toggleIcon.setImageResource(R.drawable.icon_password); // Use your visible icon
        } else {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            toggleIcon.setImageResource(R.drawable.icon_password_seen); // Use your hidden icon
        }
        editText.setSelection(editText.getText().length()); // Move cursor to end
    }

    private void configureSignUpButton() {
        TextView TVsign_up = (TextView) findViewById(R.id.sign_up);
        TVsign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(resetpasswordpage.this,signuppage_activity.class));
            }
        });
    }

}