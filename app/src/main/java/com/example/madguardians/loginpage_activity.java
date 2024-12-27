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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.example.madguardians.database.Achievement;
import com.example.madguardians.database.AchievementDao;
import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Badge;
import com.example.madguardians.database.BadgeDao;
import com.example.madguardians.database.Executor;
import com.example.madguardians.database.FirestoreManager;
import com.example.madguardians.database.Staff;
import com.example.madguardians.database.User;
import com.example.madguardians.database.UserDao;
import com.example.madguardians.ui.home.HomeFragment;
import com.example.madguardians.database.StaffDao;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class loginpage_activity extends Activity {
	private EditText emailEditText, passwordEditText;
	private Button loginButton;
	private TextView signUpTextView, forgotPasswordTextView;
	private ImageView passwordToggle;
	private FirebaseFirestore db;
//	private UserDao userDao;
//	private StaffDao staffDao;
//	private BadgeDao badgeDao;
//	private AchievementDao achievementDao;
//	private AppDatabase db;


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginpage);
		configureSignUpButton();
//		configureloginButton();

		// Initialize Firestore
		db = FirebaseFirestore.getInstance();

// Initialize database and DAO
//		db = AppDatabase.getDatabase(getApplicationContext());
//		userDao = db.userDao();
//		staffDao = db.staffDao();
//		badgeDao = db.badgeDao();

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

//		// Query database for user
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				LocalDate today= LocalDate.now();
//				User user = userDao.getByEmail(email);
//				Staff staff = staffDao.getByEmail(email);
//
//				runOnUiThread(() -> {
//					if (user != null && user.getPassword().equals(password)) {
//						// Login successful
//						Toast.makeText(loginpage_activity.this, "Login Successful", Toast.LENGTH_SHORT).show();
//
//						// Get userId
//						String userId = user.getUserId();
//						int strikeLoginDay = user.getStrikeLoginDays();
//						String lastLoginString = user.getLastLogin();
//
//						// Check if the lastLogin is a special marker or a date
//						if (lastLoginString.equals("SignUpDone")) {
//							strikeLoginDay = 1; // Reset strikeLoginDay for new signups
//						} else {
//							try {
//								// Attempt to parse the last login date
//								LocalDate lastLogin = LocalDate.parse(lastLoginString);
//								if (lastLogin.isEqual(today.minusDays(1))) {
//									strikeLoginDay++; // Increment if logged in yesterday
//								} else if (!lastLogin.isEqual(today)) {
//									strikeLoginDay = 1; // Reset if not logged in yesterday or today
//								}
//							} catch (DateTimeParseException e) {
//								strikeLoginDay = 1; // Reset if the last login date is invalid
//							}
//						}
//
//						user.setStrikeLoginDays(strikeLoginDay);
//						user.setLastLogin(today.toString());
//
//						Executor.executeTask(() -> {
//						// If username does not exist, proceed to insert the user
//						FirestoreManager firestoreManager = new FirestoreManager(AppDatabase.getDatabase(getApplicationContext()));
//						firestoreManager.onInsertUpdate("update","user", user, getApplicationContext());
//							firestoreManager.onLoginSyncUser(userId);
//						});
//						checkAndAssignAchievement(userId);
//						// Get SharedPreferences
//						SharedPreferences sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
//
//						// Save userId in SharedPreferences
//						SharedPreferences.Editor editor = sharedPreferences.edit();
//						editor.putString("user_id", userId);
//						editor.apply();
//
//						// Navigate to another activity
//						Intent intent = new Intent(loginpage_activity.this, NavVewBnv.class);
//						startActivity(intent);
//						finish();
//					} else if (staff != null && staff.getPassword().equals(password)) {
//						// Login successful for staff
//						Toast.makeText(loginpage_activity.this, "Login Successful as Staff", Toast.LENGTH_SHORT).show();
//
//
//
//
//						// Save staffId in SharedPreferences
//						SharedPreferences sharedPreferences = getSharedPreferences("staff_preferences", MODE_PRIVATE);
//						SharedPreferences.Editor editor = sharedPreferences.edit();
//						editor.putString("staff_id", staff.getStaffId());
//						editor.apply();
//						// Navigate to staff-specific activity
//						Intent intent = new Intent(loginpage_activity.this, NavVewBnvStaff.class);
//						startActivity(intent);
//						finish();
//					}
//					else {
//						// Login failed
//						Toast.makeText(loginpage_activity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
//					}
//				});
//			}
//		}).start();
		// Query Firestore for user
		db.collection("user")
				.whereEqualTo("email", email)
				.get()
				.addOnCompleteListener(task -> {
					if (task.isSuccessful() && !task.getResult().isEmpty()) {
						for (QueryDocumentSnapshot document : task.getResult()) {
							String storedPassword = document.getString("password");
							String userId = document.getString("userId");
							int strikeLoginDays = document.contains("strikeLoginDays") ? document.getLong("strikeLoginDays").intValue() : 0;

							if (storedPassword != null && storedPassword.equals(password)) {
								handleSuccessfulLogin(document, userId, strikeLoginDays);
								return;
							}
						}

						checkStaffLogin(email, password);
					} else {
						checkStaffLogin(email, password);
					}
				});
	}
	private void checkStaffLogin(String email, String password) {
		db.collection("staff")
				.whereEqualTo("email", email)
				.get()
				.addOnCompleteListener(task -> {
					if (task.isSuccessful() && !task.getResult().isEmpty()) {
						for (QueryDocumentSnapshot document : task.getResult()) {
							String storedPassword = document.getString("password");
							String staffId = document.getString("staffId");

							if (storedPassword != null && storedPassword.equals(password)) {
								handleStaffLogin(staffId);
								return;
							}
						}
						Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
					}
				});
	}

	private void handleStaffLogin(String staffId) {
		Toast.makeText(this, "Login Successful as Staff", Toast.LENGTH_SHORT).show();

		SharedPreferences sharedPreferences = getSharedPreferences("staff_preferences", MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("staff_id", staffId);
		editor.apply();

		Intent intent = new Intent(loginpage_activity.this, NavVewBnvStaff.class);
		startActivity(intent);
		finish();
	}
	private void handleSuccessfulLogin(QueryDocumentSnapshot document, String userId, int strikeLoginDays) {
		// Login successful
		Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

		// Update login streak
		LocalDate today = LocalDate.now();
		String lastLogin = document.getString("lastLogin");

		if (lastLogin == null || !lastLogin.equals(today.toString())) {
			if (lastLogin != null && lastLogin.equals(today.minusDays(1).toString())) {
				strikeLoginDays++;
			} else {
				strikeLoginDays = 1;
			}
			db.collection("user").document(document.getId())
					.update("strikeLoginDays", strikeLoginDays, "lastLogin", today.toString());
			checkAndAssignAchievement(userId);
		}

		// Save userId in SharedPreferences
		SharedPreferences sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("user_id", userId);
		editor.apply();

		// Navigate to another activity
		Intent intent = new Intent(loginpage_activity.this, NavVewBnv.class);
		startActivity(intent);
		finish();
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
	public void checkAndAssignAchievement(String userId) {
		db.collection("achievement")
				.whereEqualTo("userId", userId)
				.whereEqualTo("badgeId", "B0001")
				.get()
				.addOnCompleteListener(task -> {
					if (task.isSuccessful() && task.getResult().isEmpty()) {
						checkUserStrikeDays(userId);
					} else {
						System.out.println("User " + userId + " already has achievement B0001");
					}
				});
	}

	private void checkUserStrikeDays(String userId) {
		db.collection("user")
				.document(userId)
				.get()
				.addOnCompleteListener(task -> {
					if (task.isSuccessful() && task.getResult().exists()) {
						long strikeLoginDays = task.getResult().getLong("strikeLoginDays");
						if (strikeLoginDays >= 10) {
							assignAchievement(userId, "B0001");
						} else {
							System.out.println("User " + userId + " does not meet the criteria for achievement B0001");
						}
					} else {
						System.out.println("User with ID " + userId + " not found in Firestore");
					}
				});
	}

	private void assignAchievement(String userId, String badgeId) {
		Map<String, Object> achievement = new HashMap<>();
		achievement.put("userId", userId);
		achievement.put("badgeId", badgeId);

		db.collection("achievement")
				.add(achievement)
				.addOnSuccessListener(documentReference -> {
					System.out.println("Achievement " + badgeId + " added to user " + userId);
				})
				.addOnFailureListener(e -> {
					System.out.println("Failed to add achievement: " + e.getMessage());
				});
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
	
	