package com.example.madguardians;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Executor;
import com.example.madguardians.database.UserDao;
import com.google.firebase.firestore.FirebaseFirestore;

public class forgetpasswordpage extends AppCompatActivity {
    private EditText emailEditText;
    private Button resetPasswordButton;
    UserDao userDao;
    AppDatabase appDatabase;
    private String OTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgetpasswordpage);

        emailEditText = findViewById(R.id.email);
        resetPasswordButton = findViewById(R.id.submit);

        // Initialize database
        appDatabase = AppDatabase.getDatabase(getApplicationContext());
        userDao = appDatabase.userDao();

        resetPasswordButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();

            // Check if email is empty
            if (email.isEmpty()) {
                Toast.makeText(forgetpasswordpage.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Run the email existence check on a background thread
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("user")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // Email Exist
                            generateOTP();
                            MailgunAPI mailgunAPI = new MailgunAPI();
                            mailgunAPI.sendOTPEmail(email, OTP);

                    // Show OTP dialog
                    showOTPDialog(email);
                        } else {
                            // Email Not Exist
                            Toast.makeText(forgetpasswordpage.this, "Email not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(forgetpasswordpage.this, "Failed to check email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        configureLogInButton();
    }

    private void generateOTP() {
        // Generate a random 6-digit OTP
        OTP = String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    private void showOTPDialog(String email) {
        // Create a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter OTP");

        // Add an EditText to the dialog for OTP input
        final EditText otpEditText = new EditText(this);
        otpEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        otpEditText.setHint("Enter the OTP sent to your email");
        builder.setView(otpEditText);

        // Add "Verify" button
        builder.setPositiveButton("Verify", (dialog, which) -> {
            String enteredOTP = otpEditText.getText().toString().trim();

            if (enteredOTP.equals(OTP)) {
                // OTP is correct
                Toast.makeText(forgetpasswordpage.this, "OTP Verified", Toast.LENGTH_SHORT).show();

                // Proceed to the reset password page and pass the email
                Intent intent = new Intent(forgetpasswordpage.this, resetpasswordpage.class);
                intent.putExtra("email", email);  // Pass email to the next activity
                startActivity(intent);
            } else {
                // OTP is incorrect
                Toast.makeText(forgetpasswordpage.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            }
        });

        // Add "Cancel" button
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void configureLogInButton() {
        TextView logInHere = (TextView) findViewById(R.id.log_in);
        logInHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(forgetpasswordpage.this,loginpage_activity.class));
            }
        });
    }

                // Check if email is valid


//                // Call Firebase method to send password reset email
//                FirebaseAuth mAuth = FirebaseAuth.getInstance();
//                mAuth.sendPasswordResetEmail(email)
//                        .addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
//                                // If email was sent successfully, show a success message
//                                Toast.makeText(forgetpasswordpage.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
//                                // Redirect to another activity (e.g., a login page or verification page)
//                                Intent intent = new Intent(forgetpasswordpage.this, loginpage_activity.class);
//                                startActivity(intent);
//                            } else {
//                                // Handle error (e.g., email not found)
//                                Toast.makeText(forgetpasswordpage.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
//        });
//
//
//
//
//    }
}