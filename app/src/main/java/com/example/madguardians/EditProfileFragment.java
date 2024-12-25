package com.example.madguardians;

import android.Manifest;
import static android.content.Context.MODE_PRIVATE;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.madguardians.database.AppDatabase;
import com.example.madguardians.database.Domain;
import com.example.madguardians.database.DomainDao;
import com.example.madguardians.database.Executor;
import com.example.madguardians.database.FirestoreManager;
import com.example.madguardians.database.User;
import com.example.madguardians.database.UserDao;
import com.example.madguardians.database.VerEducator;
import com.example.madguardians.database.VerEducatorDao;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Firebase;

import java.util.List;
import java.util.concurrent.Executors;

public class EditProfileFragment extends Fragment {
    // UI Elements
    private EditText ETname, mail, ETnumber;
    private TextView email, phone_number;
    private ImageView profileImageView;
    private AppDatabase db;
    private UserDao userDao;
    private VerEducatorDao verEducatorDao;
    private DomainDao domainDao;
    private LinearLayout verifiedContainer;
    private AppDatabase appDatabase;
    SharedPreferences sharedPreferences;
    String userId;
    // Query the user from the database in a background thread
    User user;
    // Constant for image picker request
    public static final int PICK_IMAGE_REQUEST = 1;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set the title dynamically in the Activity
        if (getActivity() != null) {
            getActivity().setTitle("Edit Profile");
        }
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Initialize UI elements
        ETname = rootView.findViewById(R.id.ETname);
        mail = rootView.findViewById(R.id.mail);
        ETnumber = rootView.findViewById(R.id.ETnumber);
        email = rootView.findViewById(R.id.email);
        phone_number = rootView.findViewById(R.id.phone_number);
        profileImageView = rootView.findViewById(R.id.profile);
        verifiedContainer = rootView.findViewById(R.id.verifiedContainer);
        appDatabase = AppDatabase.getDatabase(getContext());

        sharedPreferences = getContext().getSharedPreferences("user_preferences", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", null);

        // Initialize database and DAO
        db = AppDatabase.getDatabase(getContext());
        userDao = db.userDao();
        verEducatorDao = db.verEducatorDao();
        domainDao = db.domainDao();

        Executor.executeTask(()-> {user = userDao.getById(userId);});
        // Set click listener for profile image
        profileImageView.setOnClickListener(v -> openImageChooser());

        loadUserData(userId);
        loadVerifiedStatus(userId);

        // Set click listener for save button
        TextView saveButton = rootView.findViewById(R.id.save_profile);
        saveButton.setOnClickListener(v -> saveProfile());

        // Check and request permissions for Android 14+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
            }
        }

//        // Get the BottomNavigationView from the parent Activity
//        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_view);
//
//        // Wait for the layout to be drawn and then get the BottomNavigationView's height
//        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
//            // Get the height of the BottomNavigationView
//            int bottomNavHeight = bottomNavigationView.getHeight();
//
//            // Adjust the fragment content padding to avoid the BottomNavigationView
//            rootView.setPadding(0, 0, 0, bottomNavHeight);
//        });



        return rootView;
    }

    // This method will open the image chooser
    private void openImageChooser() {
        // Create a new Intent to pick an image
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        // Use ActivityResultContracts to handle the result
        imagePickerLauncher.launch(intent);
    }
    // ActivityResultContract to handle the image picking result
    private final ActivityResultCallback<ActivityResult> pickImageCallback = result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                Uri imageUri = data.getData(); // Get the URI of the selected image
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
//                    profileImageView.setImageBitmap(bitmap);
                    user.setProfilePic(imageUri.toString());
                    Glide.with(EditProfileFragment.this)
                            .load(bitmap)
                            .circleCrop()
                            .into(profileImageView);

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    };

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), pickImageCallback);

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void loadUserData(String userId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = userDao.getById(userId);
            // Query the user from the database in a background thread
//            User user = userDao.getById(userId);

            // Update the UI on the main thread
            getActivity().runOnUiThread(() -> {
                if (user != null) {
                    // Populate the UI with the user's data
                    ETname.setText(user.getName());
                    mail.setText(user.getEmail());
                    ETnumber.setText(user.getPhoneNo() != null ? user.getPhoneNo() : "+60xx-xxxxxxx");

                    // Check if the user has a profile pic
                    String profilePicUrl = user.getProfilePic();
                    if (profilePicUrl == null || profilePicUrl.equals("url link of default profile pic")) {
                        profileImageView.setImageResource(R.drawable.ic_profile);
                    } else {
                        Glide.with(EditProfileFragment.this)
                                .load(profilePicUrl)
                                .circleCrop()
                                .into(profileImageView);
                    }
                } else {
                    Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void saveProfile() {
        // Get data from EditText fields
        String name = ETname.getText().toString();
        String emailText = mail.getText().toString();
        String phoneNumber = ETnumber.getText().toString();

        // Validate the input fields
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(emailText) || TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            if (user != null) {
                user.setName(name);
                user.setEmail(emailText);
                user.setPhoneNo(phoneNumber);

                // Save the profile pic URI if available
                String currentProfilePic = user.getProfilePic();
                System.out.println(currentProfilePic);
                if (currentProfilePic != null) {
                    user.setProfilePic(currentProfilePic); // Ensure profilePic is set
                }

                // Update the user in Firestore
                FirestoreManager firestoreManager = new FirestoreManager(appDatabase);
                Executor.executeTask(() -> firestoreManager.onInsertUpdate("update","user", user, getContext()));

                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Profile saved successfully", Toast.LENGTH_SHORT).show()
                );
            } else {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void loadVerifiedStatus(String userId) {
        // Use Executor to run the database query in the background
        Executors.newSingleThreadExecutor().execute(() -> {
            // Retrieve VerEducator Data on background thread
            List<VerEducator> verEducatorList = verEducatorDao.getAll();

            // Post back to the main thread to update the UI
            getActivity().runOnUiThread(() -> {
                // Clear container
                verifiedContainer.removeAllViews();

                if (verEducatorList != null && !verEducatorList.isEmpty()) {
                    for (VerEducator verEducator : verEducatorList) {
                        String domainName = domainDao.getById(verEducator.getDomainId()).getDomainName();

                        // If domainName is not null and the status is approved
                        if (domainName != null && verEducator.getVerifiedStatus().equalsIgnoreCase("approved")) {
                            // Add new TextView to show Domain Name
                            TextView verifiedTextView = new TextView(getContext());
                            verifiedTextView.setText(domainName);
                            verifiedTextView.setTextSize(16);
                            verifiedTextView.setPadding(10, 10, 10, 10);

                            // Add TextView to container
                            verifiedContainer.addView(verifiedTextView);
                        }
                    }
                } else {
                    // No VerEducator Data, display a message
                    TextView noRecordTextView = new TextView(getContext());
                    noRecordTextView.setText("No Verification Records");
                    noRecordTextView.setTextSize(16);
                    verifiedContainer.addView(noRecordTextView);
                }
            });
        });
    }
}