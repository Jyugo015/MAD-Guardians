package com.example.madguardians;

/*
	 *	This content is generated from the API File Info.
	 *	(Alt+Shift+Ctrl+I).
	 *
	 *	@desc 		
	 *	@file 		loginpage
	 *	@date 		Friday 01st of November 2024 07:54:54 AM
	 *	@title 		Main
	 *	@author 	
	 *	@keywords 	
	 *	@generator 	Export Kit v1.3.figma
	 *
	 */
	


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Executor;
import com.example.madguardians.database.FirestoreManager;
import com.example.madguardians.database.Staff;
import com.example.madguardians.database.User;
import com.example.madguardians.database.UserDao;
import com.example.madguardians.ui.home.HomeFragment;
import com.example.madguardians.database.StaffDao;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class loginpage_activity extends Activity {
	private EditText emailEditText, passwordEditText;
	private Button loginButton;
	private TextView signUpTextView, forgotPasswordTextView;
	private ImageView passwordToggle;
	private UserDao userDao;
	private StaffDao staffDao;


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginpage);
		configureSignUpButton();
//		configureloginButton();

// Initialize database and DAO
		AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
		userDao = db.userDao();
		staffDao = db.staffDao();

		// Initialize views
		emailEditText = findViewById(R.id.email);
		passwordEditText = findViewById(R.id.password);
		loginButton = findViewById(R.id.login);
		signUpTextView = findViewById(R.id.TVsign_up);
		forgotPasswordTextView = findViewById(R.id.forgot_password_);
		passwordToggle = findViewById(R.id.line_24);

		passwordToggle.setOnClickListener(v -> togglePasswordVisibility(passwordEditText, passwordToggle));

		// Set up login button click listener
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleLogin();
			}
		});

		// Set up sign-up button click listener
		signUpTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(loginpage_activity.this, signuppage_activity.class);
				startActivity(intent);
			}
		});

		// Set up forgot password click listener
		forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Toast.makeText(loginpage_activity.this, "Forgot Password Clicked", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(loginpage_activity.this, forgetpasswordpage.class);
				startActivity(intent);
			}
		});
	
	}
	private void handleLogin() {
		String email = emailEditText.getText().toString().trim();
		String password = passwordEditText.getText().toString().trim();

		if (email.isEmpty() || password.isEmpty()) {
			Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
			return;
		}

		// Query database for user
		new Thread(new Runnable() {
			@Override
			public void run() {
				LocalDate today= LocalDate.now();
				User user = userDao.getByEmail(email);
				Staff staff = staffDao.getByEmail(email);

				runOnUiThread(() -> {
					if (user != null && user.getPassword().equals(password)) {
						// Login successful
						Toast.makeText(loginpage_activity.this, "Login Successful", Toast.LENGTH_SHORT).show();

						// Get userId
						String userId = user.getUserId();
						int strikeLoginDay = user.getStrikeLoginDays();
						String lastLoginString = user.getLastLogin();

						// Check if the lastLogin is a special marker or a date
						if (lastLoginString.equals("SignUpDone")) {
							strikeLoginDay = 1; // Reset strikeLoginDay for new signups
						} else {
							try {
								// Attempt to parse the last login date
								LocalDate lastLogin = LocalDate.parse(lastLoginString);
								if (lastLogin.isEqual(today.minusDays(1))) {
									strikeLoginDay++; // Increment if logged in yesterday
								} else if (!lastLogin.isEqual(today)) {
									strikeLoginDay = 1; // Reset if not logged in yesterday or today
								}
							} catch (DateTimeParseException e) {
								strikeLoginDay = 1; // Reset if the last login date is invalid
							}
						}

						user.setStrikeLoginDays(strikeLoginDay);
						user.setLastLogin(today.toString());

						Executor.executeTask(() -> {
						// If username does not exist, proceed to insert the user
						FirestoreManager firestoreManager = new FirestoreManager(AppDatabase.getDatabase(getApplicationContext()));
						firestoreManager.onInsertUpdate("update","user", user, getApplicationContext());
							firestoreManager.onLoginSyncUser(userId);
						});
						// Get SharedPreferences
						SharedPreferences sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE);

						// Save userId in SharedPreferences
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putString("user_id", userId);
						editor.apply();

						// Navigate to another activity
						Intent intent = new Intent(loginpage_activity.this, NavVewBnv.class);
						startActivity(intent);
						finish();
					} else if (staff != null && staff.getPassword().equals(password)) {
						// Login successful for staff
						Toast.makeText(loginpage_activity.this, "Login Successful as Staff", Toast.LENGTH_SHORT).show();




						// Save staffId in SharedPreferences
						SharedPreferences sharedPreferences = getSharedPreferences("staff_preferences", MODE_PRIVATE);
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putString("staff_id", staff.getStaffId());
						editor.apply();
						// Navigate to staff-specific activity
						Intent intent = new Intent(loginpage_activity.this, NavVewBnvStaff.class);
						startActivity(intent);
						finish();
					}
					else {
						// Login failed
						Toast.makeText(loginpage_activity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
					}
				});
			}
		}).start();
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
		TextView TVsign_up = (TextView) findViewById(R.id.TVsign_up);
		TVsign_up.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(loginpage_activity.this,signuppage_activity.class));
			}
		});
	}
	private void configureloginButton() {
		Button btnLogin = (Button) findViewById(R.id.login);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(loginpage_activity.this, NavVewBnv.class));
			}
		});
	}
}
	
	