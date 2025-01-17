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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Executor;
import com.example.madguardians.database.FirestoreManager;
import com.example.madguardians.database.User;
import com.example.madguardians.database.UserDao;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

            // Initialize database
//            appDatabase = AppDatabase.getDatabase(this);
            // Initialize userDao
//            userDao = appDatabase.userDao();
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
                if (!newPassword.equals(confirmPasswordText)) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isPasswordStrong(newPassword)) {
                    showPasswordRequirementsDialog();
                    return;
                }

                String hashedPassword = hashPassword(newPassword);
                if (hashedPassword == null) {
                    Toast.makeText(this, "Error while hashing password. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("user")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                // Find user by email
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                String userId = document.getId();

                                // update new passwprd
                                db.collection("user")
                                        .document(userId)
                                        .update("password", hashedPassword)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(resetpasswordpage.this, "Password reset successfully", Toast.LENGTH_SHORT).show();

                                            // Redirect to login page
                                            Intent intent = new Intent(resetpasswordpage.this, loginpage_activity.class);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(resetpasswordpage.this, "Failed to reset password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(resetpasswordpage.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(resetpasswordpage.this, "Failed to fetch user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
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
    private void showPasswordRequirementsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Invalid Password")
                .setMessage("Password must:\n" +
                        "- Be at least 8 characters\n" +
                        "- Include 1 uppercase letter\n" +
                        "- Include 1 lowercase letter\n" +
                        "- Include 1 number\n" +
                        "- Include 1 special character")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private boolean isPasswordStrong(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(passwordPattern);
    }
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            String salt = "RandomSalt1234";
            String saltedPassword = password + salt;

            byte[] hashedBytes = digest.digest(saltedPassword.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
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