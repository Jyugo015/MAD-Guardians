package com.example.madguardians;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Executor;
import com.example.madguardians.database.FirestoreManager;
import com.example.madguardians.database.User;
import com.example.madguardians.database.UserDao;
import com.example.madguardians.notification.NotificationUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class signuppage_activity extends Activity {
		private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
		private ImageView passwordToggle, confirmPasswordToggle;
		private Button signupButton;
		private UserDao userDao;
		private FirebaseFirestore db;

		@Override
		public void onCreate(Bundle savedInstanceState) {

			super.onCreate(savedInstanceState);
			setContentView(R.layout.signuppage);
			configureLogInButton();

//			AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
//			userDao = db.userDao();
			db = FirebaseFirestore.getInstance();

			// Initialize UI components
			nameEditText = findViewById(R.id.name);
			emailEditText = findViewById(R.id.email);
			passwordEditText = findViewById(R.id.password);
			confirmPasswordEditText = findViewById(R.id.confirm_password);
			passwordToggle = findViewById(R.id.vector_14);
			confirmPasswordToggle = findViewById(R.id.vector_);
			signupButton = findViewById(R.id.next);

			// Set password visibility toggles
			passwordToggle.setOnClickListener(v -> togglePasswordVisibility(passwordEditText, passwordToggle));
			confirmPasswordToggle.setOnClickListener(v -> togglePasswordVisibility(confirmPasswordEditText, confirmPasswordToggle));

			// Signup button click listener
			signupButton.setOnClickListener(v -> handleSignup());
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
	private void saveUserToFirestore(String userId, String name, String email, String password) {
		String message = "Thank you for signing up!";
		User user = new User(userId, name, email, null, password, "url link of default profile pic", "SignUpDone", 0);
		NotificationUtils notificationUtils = new NotificationUtils();
		notificationUtils.createTestNotification(userId,message);
		db.collection("user").document(userId)
				.set(user)
				.addOnSuccessListener(aVoid -> {
					Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(signuppage_activity.this, loginpage_activity.class);
					startActivity(intent);
					finish();
				})
				.addOnFailureListener(e -> Toast.makeText(this, "Failed to sign up. Try again later.", Toast.LENGTH_SHORT).show());
	}
		private void handleSignup() {
			System.out.println("Success enter handle SignUp");
			String name = nameEditText.getText().toString().trim();
			String email = emailEditText.getText().toString().trim();
			String password = passwordEditText.getText().toString().trim();
			String confirmPassword = confirmPasswordEditText.getText().toString().trim();

			if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
				Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
				return;
			}

			if (!password.equals(confirmPassword)) {
				Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
				return;
			}

			if (!email.contains("@")) {
				Toast.makeText(this, "Invalid email format. Please enter a valid email address.", Toast.LENGTH_SHORT).show();
				return;
			}

//			Executor.executeTask(() -> {
//		     String userId = generateUserId();
//
//			// Save user to the database
//			User user = new User(userId, name, email, null, password, "url link of default profile pic", "SignUpDone", 0);
////		AppDatabase.getDatabase(this).userDao().insert(user);
//
//			// Check if the username already exists in the database
//
//				// Query the database to check if username already exists
//				boolean usernameExists = AppDatabase.getDatabase(getApplicationContext())
//						.userDao()
//						.usernameExists(user.getName());
//
//				// Query the database to check if email already exists
//				boolean emailExists = AppDatabase.getDatabase(getApplicationContext())
//						.userDao()
//						.emailExists(user.getEmail());
//
//				if (usernameExists) {
//					// If username exists, show a toast message to the user
//					runOnUiThread(() -> {
//						Toast.makeText(this, "Username already exists. Please choose another one.", Toast.LENGTH_SHORT).show();
//					});
//				} else if (emailExists) {
//					// If email exists, show a toast message
//					runOnUiThread(() -> {
//						Toast.makeText(this, "Email already exists. Please choose another one.", Toast.LENGTH_SHORT).show();
//					});
//				}else {
//					// If username does not exist, proceed to insert the user
//					FirestoreManager firestoreManager = new FirestoreManager(AppDatabase.getDatabase(getApplicationContext()));
//					firestoreManager.onInsertUpdate("insert","user", user, getApplicationContext());
//
//					// Redirect to login page after successful signup
//					runOnUiThread(() -> {
//						Intent intent = new Intent(com.example.madguardians.signuppage_activity.this, loginpage_activity.class);
//						startActivity(intent);
//						finish();
//					});
//				}
//			});
			db.collection("user")
					.whereEqualTo("email", email)
					.get()
					.addOnCompleteListener(emailTask -> {
						if (emailTask.isSuccessful() && !emailTask.getResult().isEmpty()) {
							Toast.makeText(this, "Email already exists. Please choose another one.", Toast.LENGTH_SHORT).show();
						} else {
							db.collection("user")
									.whereEqualTo("name", name)
									.get()
									.addOnCompleteListener(nameTask -> {
										if (nameTask.isSuccessful() && !nameTask.getResult().isEmpty()) {
											Toast.makeText(this, "Username already exists. Please choose another one.", Toast.LENGTH_SHORT).show();
										} else {
											generateUserIdAndSave(name, email, password);
										}
									});
						}
					});
		}

	private void generateUserIdAndSave(String name, String email, String password) {
		db.collection("user")
				.orderBy("userId", Query.Direction.DESCENDING)
				.limit(1)
				.get()
				.addOnCompleteListener(task -> {
					if (task.isSuccessful() && !task.getResult().isEmpty()) {
						QueryDocumentSnapshot lastUser = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
						String lastUserId = lastUser.getString("userId");

						if (lastUserId != null) {
							int idNumber = Integer.parseInt(lastUserId.substring(1)) + 1;
							String newUserId = String.format("U%04d", idNumber); // 格式化为 U0001
							saveUserToFirestore(newUserId, name, email, password);
						}
					} else {
						String newUserId = "U0001";
						saveUserToFirestore(newUserId, name, email, password);
					}
				});
	}

		private void configureLogInButton() {
			TextView logInHere = (TextView) findViewById(R.id.log_in_here_ek1);
			logInHere.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(com.example.madguardians.signuppage_activity.this,loginpage_activity.class));
				}
			});
		}


}
	
	